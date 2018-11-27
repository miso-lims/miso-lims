package uk.ac.bbsrc.tgac.miso.webapp.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.GetLaneContents;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.security.SuperuserAuthentication;
import uk.ac.bbsrc.tgac.miso.core.util.LatencyHistogram;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.RunService;

import ca.on.oicr.gsi.runscanner.dto.IlluminaNotificationDto;
import ca.on.oicr.gsi.runscanner.dto.NotificationDto;
import ca.on.oicr.gsi.runscanner.dto.ProgressiveRequestDto;
import ca.on.oicr.gsi.runscanner.dto.ProgressiveResponseDto;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;

@Service
public class RunScannerClient {
  private static final LatencyHistogram acquireTime = new LatencyHistogram("miso_runscanner_client_acquire_time",
      "Time to acquire the lock to put save runs (in seconds).");

  private static final Gauge badRunCount = Gauge.build()
      .name("miso_runscanner_client_bad_runs").help("The number of runs that failed to save.").register();

  private static final Logger log = LoggerFactory.getLogger(RunScannerClient.class);

  private static final Counter saveCount = Counter.build().name("miso_runscanner_client_run_count").help("The number of runs processed.")
      .register();
  private static final Counter saveFailures = Counter.build()
      .name("miso_runscanner_client_save_errors").help("The number of times a run failed to be saved.").register();

  private static final Counter saveNew = Counter.build()
      .name("miso_runscanner_client_save_new").help("The number of times a new run was found.").register();

  private static final LatencyHistogram saveTime = new LatencyHistogram("miso_runscanner_client_save_time",
      "Time to save a run (in seconds).");

  private static final Counter saveUpdate = Counter.build()
      .name("miso_runscanner_client_save_update").help("The number of times a run was found in the database.").register();

  private static final Gauge scanTimestamp = Gauge.build()
      .name("miso_runscanner_client_scan_timestamp").help("The UNIX time when the client last attempted to scan the servers.").register();

  private static final Counter serverFailures = Counter.build()
      .name("miso_runscanner_client_read_errors").help("The number of times the server failed to respond.").labelNames("url").register();

  private static final LatencyHistogram serverReadTime = new LatencyHistogram("miso_runscanner_client_read_time",
      "Time to download updates from a server in seconds.", "url");
  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  private final Set<String> badRuns = new HashSet<>();

  private final Semaphore lock = new Semaphore(1);
  @Autowired
  private RunService runService;
  @Autowired
  private SecurityManager securityManager;
  private final ConcurrentMap<String, ProgressiveRequestDto> servers = new ConcurrentHashMap<>();

  private void processResults(List<NotificationDto> results) {
    try {
      User user = securityManager.getUserByLoginName("notification");
      SecurityContextHolder.getContext().setAuthentication(new SuperuserAuthentication(user));

      try (AutoCloseable timer = acquireTime.start()) {
        lock.acquire();
      } catch (Exception e) {
        log.error("Failed to acquire lock", e);
        return;
      }
      for (NotificationDto dto : results) {
        try (AutoCloseable timer = saveTime.start()) {

          // Determine whether the SequencingParameters are of the correct type
          Predicate<SequencingParameters> isMatchingSequencingParameters;
          switch (dto.getPlatformType()) {
          case ILLUMINA:
            isMatchingSequencingParameters = params -> params.getPlatform().getPlatformType() == PlatformType.ILLUMINA &&
                Math.abs(params.getReadLength() - ((IlluminaNotificationDto) dto).getReadLength()) < 2
                && params.isPaired() == ((IlluminaNotificationDto) dto).isPairedEndRun()
                && params.getChemistry() == Dtos.getMisoIlluminaChemistryFromRunscanner(((IlluminaNotificationDto) dto).getChemistry());
            break;
          default:
            isMatchingSequencingParameters = params -> params.getPlatform()
                .getPlatformType() == Dtos.getMisoPlatformTypeFromRunscanner(dto.getPlatformType());
            break;
          }

          GetLaneContents laneContents = new GetLaneContents() {
            /**
             * Get getLaneContents implementation from DTO.
             * Illumina DTOs have a unique implementation of getLaneContents.
             */
            @Override
            public Optional<String> getLaneContents(int lane) {
              return dto.getLaneContents(lane);
            }
          };

          (runService.processNotification(Dtos.to(dto, user), dto.getLaneCount(), dto.getContainerModel(), dto.getContainerSerialNumber(),
              dto.getSequencerName(), isMatchingSequencingParameters, laneContents, dto.getSequencerPosition()) ? saveNew : saveUpdate).inc();
          saveCount.inc();
          badRuns.remove(dto.getRunAlias());
        } catch (Exception e) {
          log.error("Failed to save run: " + dto.getRunAlias(), e);
          saveFailures.inc();
          badRuns.add(dto.getRunAlias());
        }
        badRunCount.set(badRuns.size());
      }
      lock.release();
    } catch (IOException e) {
      log.error("Failed to save runs", e);
    }
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
          serverFailures.labels(entry.getKey()).inc();
        }
        if (response != null) {
          processResults(response.getUpdates());
        }
      } while (response != null && response.isMoreAvailable());
    }
  }

  @Value("${miso.runscanner.urls:}")
  public void setUrls(String urls) {
    WHITESPACE.splitAsStream(urls).map(url -> !url.isEmpty() && !servers.containsKey(url))
        .forEach(url -> servers.put(urls, new ProgressiveRequestDto()));
  }
}
