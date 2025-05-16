package uk.ac.bbsrc.tgac.miso.webapp.service;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eaglegenomics.simlims.core.User;

import ca.on.oicr.gsi.runscanner.dto.IlluminaNotificationDto;
import ca.on.oicr.gsi.runscanner.dto.NotificationDto;
import ca.on.oicr.gsi.runscanner.dto.OxfordNanoporeNotificationDto;
import ca.on.oicr.gsi.runscanner.dto.PacBioNotificationDto;
import ca.on.oicr.gsi.runscanner.dto.PacBioNotificationDto.SMRTCellPosition;
import ca.on.oicr.gsi.runscanner.dto.ProgressiveRequestDto;
import ca.on.oicr.gsi.runscanner.dto.ProgressiveResponseDto;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaChemistry;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.SuperuserAuthentication;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingContainerModelService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.util.LatencyHistogram;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Service
public class RunScannerClient {
  private static final LatencyHistogram acquireTime = new LatencyHistogram("miso_runscanner_client_acquire_time",
      "Time to acquire the lock to put save runs (in seconds).");

  private static final Gauge badRunCount = Gauge.builder()
      .name("miso_runscanner_client_bad_runs").help("The number of runs that failed to save.").register();

  private static final Gauge fallbackContainerModelCount = Gauge.builder()
      .name("miso_runscanner_client_bad_container_models")
      .help("The number of runs that have unknown container models.").register();

  private static final Gauge unknownSequencingKitCount = Gauge.builder()
      .name("miso_runscanner_client_unknown_sequencing_kits")
      .help("The number of runs that have unknown sequencing kits.").register();

  private static final Logger log = LoggerFactory.getLogger(RunScannerClient.class);

  private static final Counter saveCount =
      Counter.builder().name("miso_runscanner_client_run_count").help("The number of runs processed.")
          .register();
  private static final Counter saveFailures = Counter.builder()
      .name("miso_runscanner_client_save_errors").help("The number of times a run failed to be saved.").register();

  private static final Counter saveNew = Counter.builder()
      .name("miso_runscanner_client_save_new").help("The number of times a new run was found.").register();

  private static final LatencyHistogram saveTime = new LatencyHistogram("miso_runscanner_client_save_time",
      "Time to save a run (in seconds).");

  private static final Counter saveUpdate = Counter.builder()
      .name("miso_runscanner_client_save_update").help("The number of times a run was found in the database.")
      .register();

  private static final Gauge scanTimestamp = Gauge.builder()
      .name("miso_runscanner_client_scan_timestamp")
      .help("The UNIX time when the client last attempted to scan the servers.").register();

  private static final Counter serverFailures = Counter.builder()
      .name("miso_runscanner_client_read_errors").help("The number of times the server failed to respond.")
      .labelNames("url").register();

  private static final LatencyHistogram serverReadTime = new LatencyHistogram("miso_runscanner_client_read_time",
      "Time to download updates from a server in seconds.", "url");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  private final Set<String> badRuns = new HashSet<>();

  private final Set<String> fallbackContainerModelRuns = new HashSet<>();
  private final Set<String> unknownSequencingKitRuns = new HashSet<>();

  private final Semaphore lock = new Semaphore(1);
  @Autowired
  private RunService runService;
  @Autowired
  private UserService userService;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private SequencingContainerModelService sequencingContainerModelService;
  @Autowired
  private PoolService poolService;

  private final ConcurrentMap<String, ProgressiveRequestDto> servers = new ConcurrentHashMap<>();

  private void processResults(List<NotificationDto> results) {
    try {
      User user = userService.getByLoginName("notification");
      SecurityContextHolder.getContext().setAuthentication(new SuperuserAuthentication(user));

      try (AutoCloseable timer = acquireTime.start()) {
        lock.acquire();
      } catch (Exception e) {
        log.error("Failed to acquire lock", e);
        return;
      }
      for (NotificationDto dto : results) {
        try (AutoCloseable timer = saveTime.start()) {
          Run notificationRun = Dtos.to(dto);
          setSequencer(notificationRun, dto.getSequencerName());
          setRunSequencingParameters(notificationRun, dto);
          setContainers(notificationRun, dto);

          boolean isNew = runService.processNotification(notificationRun);
          (isNew ? saveNew : saveUpdate).inc();
          saveCount.inc();
          badRuns.remove(dto.getRunAlias());
          Run saved = runService.getRunByAlias(notificationRun.getAlias());
          if (hasFallbackContainerModel(saved)) {
            if (isNew && dto.getContainerModel() != null) {
              fallbackContainerModelRuns.add(saved.getAlias());
              log.info(
                  "Could not find container model with model=%s and lanes=%d for run %s; used fallback container model instead."
                      .formatted(dto.getContainerModel(), dto.getLaneCount(), dto.getRunAlias()));
            }
          } else {
            fallbackContainerModelRuns.remove(saved.getAlias());
          }
          if (dto.getSequencingKit() != null && saved.getSequencingKit() == null) {
            if (isNew) {
              unknownSequencingKitRuns.add(saved.getAlias());
            }
          } else {
            unknownSequencingKitRuns.remove(saved.getAlias());
          }
        } catch (ValidationException e) {
          String errors = e.getErrors().stream()
              .map(error -> error.getProperty() + ": " + error.getMessage())
              .collect(Collectors.joining("; "));
          log.error("Failed to save run due to validation errors: " + errors, e);
          saveFailures.inc();
          badRuns.add(dto.getRunAlias());
        } catch (Exception e) {
          log.error("Failed to save run: " + dto.getRunAlias(), e);
          saveFailures.inc();
          badRuns.add(dto.getRunAlias());
        }
        badRunCount.set(badRuns.size());
        fallbackContainerModelCount.set(fallbackContainerModelRuns.size());
        unknownSequencingKitCount.set(unknownSequencingKitRuns.size());
      }
      lock.release();
    } catch (IOException e) {
      log.error("Failed to save runs", e);
    }
  }

  private void setSequencer(Run run, String sequencerName) throws IOException {
    Instrument sequencer = instrumentService.getByName(sequencerName);
    if (sequencer == null) {
      throw new IllegalArgumentException("No such sequencer: " + sequencerName);
    }
    run.setSequencer(sequencer);
  }

  private void setRunSequencingParameters(Run to, NotificationDto from) throws IOException {
    InstrumentModel model = to.getSequencer().getInstrumentModel();
    if (model.getPlatformType().hasContainerLevelParameters()) {
      // Nothing to do - these are set on the RunPositions instead
      return;
    }

    Stream<SequencingParameters> instrumentParams =
        sequencingParametersService.listByInstrumentModelId(model.getId()).stream()
            .filter(parameters -> !parameters.getName().startsWith("Custom"));
    switch (model.getPlatformType()) {
      case ILLUMINA:
        setIlluminaSequencingParameters(to, (IlluminaNotificationDto) from, instrumentParams);
        break;
      case OXFORDNANOPORE:
        OxfordNanoporeNotificationDto ontDto = (OxfordNanoporeNotificationDto) from;
        List<SequencingParameters> matchingParams =
            instrumentParams.filter(params -> params.getRunType().equals(ontDto.getRunType()))
                .toList();
        if (matchingParams.size() == 1) {
          to.setSequencingParameters(matchingParams.get(0));
        }
        break;
      default:
        throw new NotImplementedException("Platform not supported: %s".formatted(to.getPlatformType()));
    }
  }

  private void setIlluminaSequencingParameters(Run to, IlluminaNotificationDto from,
      Stream<SequencingParameters> instrumentParams) {
    IlluminaChemistry chemistry = Dtos.getMisoIlluminaChemistryFromRunscanner(from.getChemistry());
    List<SequencingParameters> matchingParams = instrumentParams.filter(params -> {
      if (params.getChemistry() != chemistry) {
        return false;
      }
      // If we are talking to an old Run Scanner that doens't provide read lengths, use the old logic
      if (from.getReadLengths() == null) {
        // The read length must match the first read length
        if (Math.abs(params.getReadLength() - from.getReadLength()) < 2) {
          // if there is no second read length, then this must be single ended
          if (params.getReadLength2() == 0) {
            return !from.isPairedEndRun();
          } else {
            // If there is, it must be paired and symmetric
            return from.isPairedEndRun() && params.getReadLength() == params.getReadLength2();
          }
        } else {
          return false;
        }
      }
      // If we have real read lengths, check they match what we see from Run Scanner
      if (from.getReadLengths().size() == 0) {
        return false;
      }
      if (Math.abs(params.getReadLength() - from.getReadLengths().get(0)) > 1) {
        return false;
      }
      // If no second read is provided, make sure none is required
      if (from.getReadLengths().size() == 1) {
        return params.getReadLength2() == 0;
      }
      // Otherwise, check the second read matches the right length
      return Math.abs(params.getReadLength2() - from.getReadLengths().get(1)) < 2;
    }).toList();
    if (matchingParams.size() == 1) {
      to.setSequencingParameters(matchingParams.get(0));
    }
  }

  private void setContainers(Run to, NotificationDto from) throws IOException {
    if (from.getContainerModel() != null) {
      addContainer(to, from.getContainerModel(), from.getLaneCount(), from.getContainerSerialNumber(),
          from.getSequencerPosition(), from::getLaneContents);
    } else if (to.getSequencer().getInstrumentModel().getPlatformType() == PlatformType.PACBIO) {
      PacBioNotificationDto pacbioDto = (PacBioNotificationDto) from;
      List<SequencingParameters> instrumentParams =
          sequencingParametersService.listByInstrumentModelId(to.getSequencer().getInstrumentModel().getId()).stream()
              .filter(parameters -> !parameters.getName().startsWith("Custom"))
              .toList();
      for (SMRTCellPosition pos : pacbioDto.getSequencerPositions()) {
        RunPosition runPos = addContainer(to, pos.containerModel(), 1, pos.containerSerialNumber(), pos.position(),
            lane -> Optional.ofNullable(pos.poolName()));
        if (pos.movieLength() != null) {
          Integer movieLengthInt = Double.valueOf(pos.movieLength()).intValue();
          List<SequencingParameters> matchingParams = instrumentParams.stream()
              .filter(params -> Objects.equals(params.getMovieTime(), movieLengthInt))
              .toList();
          if (matchingParams.size() == 1) {
            runPos.setSequencingParameters(matchingParams.get(0));
          }
        }
      }
    }
  }

  private RunPosition addContainer(Run to, String containerModel, int laneCount, String containerSerialNumber,
      String sequencerPosition, Function<Integer, Optional<String>> getLaneContents) throws IOException {
    SequencingContainerModel model =
        sequencingContainerModelService.find(to.getSequencer().getInstrumentModel(), containerModel, laneCount);
    if (model == null) {
      throw new IllegalArgumentException("Could not find container or fallback for parameters: model=%s, lanes=%d"
          .formatted(containerModel, laneCount));
    }
    SequencerPartitionContainer container = model.getPlatformType().createContainer();
    container.setModel(model);
    container.setIdentificationBarcode(containerSerialNumber);
    container.setPartitionLimit(laneCount);
    container.setPartitions(IntStream.range(0, laneCount)
        .mapToObj(i -> new PartitionImpl(container, i + 1))
        .collect(Collectors.toList()));

    InstrumentPosition position = null;
    if (!isStringEmptyOrNull(sequencerPosition)) {
      position = to.getSequencer().getInstrumentModel().getPositions().stream()
          .filter(pos -> sequencerPosition.equals(pos.getAlias()))
          .findFirst().orElseThrow(
              () -> new IllegalArgumentException(
                  String.format("Unknown position '%s' for platform '%s'", sequencerPosition,
                      to.getSequencer().getInstrumentModel().getAlias())));
    }

    for (Partition partition : container.getPartitions()) {
      String poolString = getLaneContents.apply(partition.getPartitionNumber()).orElse(null);
      if (poolString != null) {
        Pool pool = poolService.getByBarcode(poolString);
        if (pool == null) {
          pool = poolService.getByAlias(poolString);
        }
        if (pool != null && pool.getPlatformType() == to.getSequencer().getInstrumentModel().getPlatformType()) {
          partition.setPool(pool);
        }
      }
    }

    RunPosition runPos = new RunPosition();
    runPos.setRun(to);
    runPos.setContainer(container);
    runPos.setPosition(position);
    to.getRunPositions().add(runPos);
    return runPos;
  }

  private static boolean hasFallbackContainerModel(Run run) {
    return run.getSequencerPartitionContainers().stream()
        .map(SequencerPartitionContainer::getModel)
        .anyMatch(SequencingContainerModel::isFallback);
  }

  @Scheduled(fixedDelayString = "${miso.runscanner.interval:300000}")
  public void scheduler() {
    scanTimestamp.set(System.currentTimeMillis() / 1000);
    RestTemplate template = new RestTemplate();
    for (Entry<String, ProgressiveRequestDto> entry : servers.entrySet()) {
      ProgressiveResponseDto response = null;
      do {
        try (AutoCloseable timer = serverReadTime.start(entry.getKey())) {
          response = template.postForObject(entry.getKey() + "/runs/progressive", entry.getValue(),
              ProgressiveResponseDto.class);
          entry.getValue().update(response);
        } catch (Exception e) {
          log.error("Failed to get runs from " + entry.getKey(), e);
          serverFailures.labelValues(entry.getKey()).inc();
        }
        if (response != null) {
          processResults(response.getUpdates());
        }
      } while (response != null && response.isMoreAvailable());
    }
  }

  @Value("${miso.runscanner.urls:}")
  public void setUrls(String urls) {
    WHITESPACE.splitAsStream(urls).filter(url -> !url.isEmpty() && !servers.containsKey(url))
        .forEach(url -> servers.put(url, new ProgressiveRequestDto()));
  }
}
