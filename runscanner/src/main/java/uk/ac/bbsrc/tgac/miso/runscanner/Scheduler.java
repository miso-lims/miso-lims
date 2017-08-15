package uk.ac.bbsrc.tgac.miso.runscanner;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LatencyHistogram;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

/**
 * Periodically scan the run directories and cache the results.
 */
@Service
public class Scheduler {
  /**
   * Holder for a run that has been scanned.
   */
  private static class FinishedWork {
    Instant created = Instant.now();
    NotificationDto dto;
    int epoch;

    /**
     * Determine if the run should be scanned again.
     * 
     * This only happens if the run is not marked as done by the processor and 10 minutes have past since the last process. The automatic
     * rerunning done by scheduler is not sufficient to determine if the run needs to be reprocessed since it isn't clear how long the run
     * waited in the processing queue.
     */
    public boolean shouldRerun() {
      return !dto.getHealthType().isDone() && Duration.between(created, Instant.now()).toMinutes() > 10;
    }
  }

  public static class OutputSizeLimit implements Predicate<FinishedWork> {
    private int count;
    private int highestEpoch;
    private final int softLimit;

    public OutputSizeLimit(int softLimit) {
      super();
      this.softLimit = softLimit;
    }

    public int getEpoch() {
      return highestEpoch;
    }

    public boolean hasCapacity() {
      return count < softLimit;
    }

    @Override
    public boolean test(FinishedWork work) {
      if (hasCapacity()) {
        count++;
        highestEpoch = work.epoch;
        return true;
      }
      return work.epoch == highestEpoch;
    }

  }

  public static class SuppliedDirectoryConfig {

    private String name;
    private ObjectNode parameters;
    private String path;
    private PlatformType platformType;
    private String timeZone;

    public String getName() {
      return name;
    }

    public ObjectNode getParameters() {
      return parameters;
    }

    public String getPath() {
      return path;
    }

    public PlatformType getPlatformType() {
      return platformType;
    }

    public String getTimeZone() {
      return timeZone;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setParameters(ObjectNode parameters) {
      this.parameters = parameters;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public void setPlatformType(PlatformType platformType) {
      this.platformType = platformType;
    }

    public void setTimeZone(String timeZone) {
      this.timeZone = timeZone;
    }
  }

  private static final Gauge acceptedDirectories = Gauge.build().name("miso_runscanner_directories_accepted")
      .help("The number of directories that were readable and sent for processing in the last pass.").register();
  private static final Gauge attemptedDirectories = Gauge.build().name("miso_runscanner_directories_attempted")
      .help("The number of directories that were considered in the last pass.").register();

  private static final Gauge configurationEntries = Gauge.build().name("miso_runscanner_configuration_entries")
      .help("The number of entries from the last configuration.").register();

  private static final Gauge configurationTimestamp = Gauge.build().name("miso_runscanner_configuration_timestamp")
      .help("The epoch time when the configuration was last read.").register();
  private static final Gauge configurationValid = Gauge.build().name("miso_runscanner_configuration_valid")
      .help("Whether the configuration loaded from disk is valid.").register();
  private static final Gauge epochGauge = Gauge.build().name("miso_runscanner_epoch")
      .help("The current round of processing done for keeping the client in sync when progressively scanning.").register();

  private static final Counter errors = Counter.build().name("miso_runscanner_errors").help("The number of bad directories encountered.")
      .labelNames("platform").register();

  private static Logger log = LoggerFactory.getLogger(Scheduler.class);
  private static final Gauge newRunsScanned = Gauge.build().name("miso_runscanner_new_runs_scanned")
      .help("The number of runs discovered in the last pass.").register();
  private static final Histogram processTime = Histogram.build().buckets(1, 5, 10, 30, 60, 300, 600, 3600)
      .name("miso_runscanner_directory_process_time").help("Time to process a run directories in seconds.")
      .labelNames("platform", "instrument").register();

  private static final Counter reentered = Counter.build().name("miso_runscanner_reentered")
      .help("The number of times the scanner was already running while scheduled to run again.").register();

  private static final LatencyHistogram scanTime = new LatencyHistogram("miso_runscanner_directory_scan_time",
      "Time to scan the run directories in seconds.");

  private File configurationFile;

  private Instant configurationLastRead = Instant.now();

  private final AtomicInteger epoch = new AtomicInteger();

  // The paths that threw an exception while processing.
  private final Set<File> failed = new ConcurrentSkipListSet<>();

  // The paths for which we have a notification to send.
  private final Map<File, FinishedWork> finishedWork = new ConcurrentHashMap<>();

  private boolean isConfigurationGood = true;

  // The paths that are current being processed (and the corresponding processor).
  private final Set<File> processing = new ConcurrentSkipListSet<>();

  // The directories that contain run directories that need to be scanned and the processors for those runs.
  private List<Configuration> roots = Collections.emptyList();

  private ScheduledFuture<?> scanDirectoriesFuture = null;

  private Instant scanLastStarted = null;

  private boolean scanningNow = false;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final ExecutorService workPool = Executors.newWorkStealingPool();

  // The paths that need to be processed (and the corresponding processor).
  private final Set<File> workToDo = new ConcurrentSkipListSet<>();

  public Stream<NotificationDto> finished() {
    return finished(0, x -> true);
  }

  public Stream<NotificationDto> finished(long epoch, Predicate<FinishedWork> filter) {
    return finishedWork.values().stream().filter(x -> x.epoch >= epoch).sorted((a, b) -> a.epoch - b.epoch).filter(filter).map(x -> x.dto);
  }

  public Iterable<Configuration> getConfiguration() {
    return roots;
  }

  public Instant getConfigurationLastRead() {
    return configurationLastRead;
  }

  public Set<File> getCurrentWork() {
    return processing;
  }

  public int getEpoch() {
    return epoch.get();
  }

  public Set<File> getFailedDirectories() {
    return failed;
  }

  public Set<File> getFinishedDirectories() {
    return finishedWork.keySet();
  }

  public Set<File> getRoots() {
    return roots.stream().map(Configuration::getPath).collect(Collectors.toSet());
  }

  public Instant getScanLastStarted() {
    return scanLastStarted;
  }

  public Set<File> getScheduledWork() {
    return workToDo;
  }

  private boolean isAcceptable(File directory) {
    return directory.canRead() && directory.canExecute();
  }

  public boolean isConfigurationGood() {
    return isConfigurationGood;
  }

  public boolean isScanningEnabled() {
    return scanDirectoriesFuture != null;
  }

  public boolean isScanningNow() {
    return scanningNow;
  }

  /**
   * Determine if a run directory is in need of processing.
   * 
   * This means that is is not in a processing queue, failed processing last time, nor needs reprocessing (for runs still active on the
   * sequencer)
   */
  private boolean isUnprocessed(File directory) {
    return !workToDo.contains(directory) && !processing.contains(directory) && !failed.contains(directory)
        && (!finishedWork.containsKey(directory) || finishedWork.get(directory).shouldRerun());
  }

  /**
   * Push a run directory into the processing queue.
   */
  private void queueDirectory(final File directory, final RunProcessor processor, final TimeZone tz) {
    workToDo.add(directory);
    workPool.submit(() -> {
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      processing.add(directory);
      workToDo.remove(directory);

      long runStartTime = System.nanoTime();
      String instrumentName = "unknown";
      try {
        NotificationDto dto = processor.process(directory, tz);
        if (!LimsUtils.isStringBlankOrNull(dto.getSequencerName())) {
          instrumentName = dto.getSequencerName();
        }
        FinishedWork work = new FinishedWork();
        work.dto = dto;
        work.epoch = epoch.incrementAndGet();
        finishedWork.put(directory, work);
        epochGauge.set(work.epoch);
      } catch (Exception e) {
        log.error("Failed to process run: " + directory.getPath(), e);
        errors.labels(processor.getPlatformType().name()).inc();
        failed.add(directory);
      }
      processTime.labels(processor.getPlatformType().name(), instrumentName).observe((System.nanoTime() - runStartTime) / 1e9);
      processing.remove(directory);
    });
  }

  /**
   * Rebuild the set of sequencer directories to scan from the configuration file.
   * 
   * If the configuration file is unreadable or contains no entries, the configuration is bad. The configuration file may still contain
   * defective/invalid entries and those directories will not be scanned.
   * 
   * Changing the configuration does not clear the cache. So if a sequencer's configuration is changed from valid to invalid to valid again,
   * it will not trigger re-processing of the previous output, even if the timezone or processor is changed.
   */
  private void readConfiguration() {
    ObjectMapper mapper = new ObjectMapper();
    configurationLastRead = Instant.now();
    configurationTimestamp.set(configurationLastRead.getEpochSecond());
    try {
      roots = Arrays.stream(mapper.readValue(configurationFile, SuppliedDirectoryConfig[].class)).map(source -> {
        Configuration destination = new Configuration();
        destination.setPath(new File(source.getPath()));
        destination.setTimeZone(TimeZone.getTimeZone(source.getTimeZone()));
        destination.setProcessor(RunProcessor.processorFor(source.getPlatformType(), source.getName(), source.getParameters())
            .orElse(null));
        return destination;
      }).collect(Collectors.toList());
      configurationEntries.set(roots.size());
      isConfigurationGood = roots.size() > 0;
    } catch (IOException e) {
      log.error("Confguration is bad.", e);
      isConfigurationGood = false;
    }
    configurationValid.set(isConfigurationGood ? 1 : 0);
  }

  @Value("${runscanner.configFile}")
  public void setConfigurationFile(String filename) {
    configurationFile = new File(filename);
    readConfiguration();
  }

  /**
   * Initiate scanning every 15 minutes until stopped.
   */
  public synchronized void start() {
    if (scanDirectoriesFuture == null) {
      scanDirectoriesFuture = scheduler.scheduleAtFixedRate(() -> {
        if (scanningNow) {
          reentered.inc();
          return;
        }
        scanningNow = true;
        if (configurationFile != null && configurationFile.exists()
            && configurationFile.lastModified() > configurationLastRead.getEpochSecond()) {
          readConfiguration();
        }
        scanLastStarted = Instant.now();
        try (StreamCountSpy<Pair<File, Configuration>> newRuns = new StreamCountSpy<>(newRunsScanned);
            StreamCountSpy<Pair<File, Configuration>> attempted = new StreamCountSpy<>(attemptedDirectories);
            StreamCountSpy<Pair<File, Configuration>> accepted = new StreamCountSpy<>(acceptedDirectories);
            AutoCloseable timer = scanTime.start()) {
          roots.stream()//
              .filter(Configuration::isValid)//
              .flatMap(Configuration::getRuns)//
              .peek(attempted)//
              .filter(entry -> isAcceptable(entry.getKey()))//
              .peek(accepted)//
              .filter(entry -> isUnprocessed(entry.getKey()))//
              .peek(newRuns)//
              .forEach(entry -> {
                queueDirectory(entry.getKey(), entry.getValue().getProcessor(), entry.getValue().getTimeZone());
              });
        } catch (Exception e) {
          log.error("Error scanning directory.", e);
        }
        scanningNow = false;
      }, 1, 15, TimeUnit.MINUTES);
    }
  }

  /**
   * Stop scanning. Queued run directories will still be processed.
   */
  public synchronized void stop() {
    if (scanDirectoriesFuture != null) {
      scanDirectoriesFuture.cancel(false);
      scanDirectoriesFuture = null;
    }
    workPool.shutdownNow();
  }

}
