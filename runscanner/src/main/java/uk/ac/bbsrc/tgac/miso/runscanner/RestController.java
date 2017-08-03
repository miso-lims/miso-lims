package uk.ac.bbsrc.tgac.miso.runscanner;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.util.LatencyHistogram;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.dto.ProgressiveRequestDto;
import uk.ac.bbsrc.tgac.miso.dto.ProgressiveResponseDto;
import uk.ac.bbsrc.tgac.miso.runscanner.Scheduler.OutputSizeLimit;

/**
 * Provide information about the run scanner's current run cache via a REST interface.
 */
@Controller
public class RestController {
  private static final Logger log = LoggerFactory.getLogger(RestController.class);
  private static final LatencyHistogram progressiveLatency = new LatencyHistogram("miso_runscanner_progressive_latency",
      "Time to serve a progressive request (in seconds).");
  @Autowired
  private Scheduler scheduler;

  // We create a token that is effectively random upon initialisation so that we know if the client thinks it's talking to the same instance
  // of the server
  private final long token = System.currentTimeMillis();

  /**
   * Given a known run name.
   * If no run is found, null is returned. If there are multiple runs with the same name that are from different sequencers, one is randomly
   * selected.
   */
  @RequestMapping(value = "/run/{name}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public NotificationDto getByName(@PathVariable("name") String id) {
    return scheduler.finished().filter(dto -> dto.getRunAlias().equals(id)).findAny().orElse(null);
  }

  /**
   * Provide all the runs current cache as an array.
   * 
   * Runs are not guaranteed to have unique names.
   */
  @RequestMapping(value = "/runs/all", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public List<NotificationDto> list() {
    return scheduler.finished().collect(Collectors.toList());
  }

  /**
   * Send a progressive scan of results to the client.
   * 
   * This uses two pieces of information: a token and an epoch.
   * 
   * The purpose of the token is to identify ourselves. Since this service might be restarted between the requests, the token identifies
   * this instance of the server for the life time of its run. If the token doesn't match, we send the client all the data we know about and
   * give them the new token.
   * 
   * We also need to track time. Rather than keep track of wall time, we use an incrementing counter (epoch) that we increment whenever we
   * finish processing a run. If the client sends us a valid token, we will tell them the new epoch and give them only the work done since
   * the last epoch.
   * 
   * @param request
   * @return
   */
  @RequestMapping(value = "/runs/progressive", method = RequestMethod.POST, produces = { "application/json" })
  @ResponseBody
  public ProgressiveResponseDto progressive(@RequestBody ProgressiveRequestDto request) {
    ProgressiveResponseDto response = new ProgressiveResponseDto();
    response.setToken(token);
    try (AutoCloseable timer = progressiveLatency.start()) {
      Scheduler.OutputSizeLimit limit = new OutputSizeLimit(Math.min(request.getLimit(), 500));
      response.setUpdates(scheduler.finished(request.getToken() == token ? request.getEpoch() : 0, limit)
          .collect(Collectors.toList()));
      response.setMoreAvailable(!limit.hasCapacity());
      response.setEpoch(limit.getEpoch());
    } catch (Exception e) {
      log.error("Error during progressive run", e);
      response.setUpdates(Collections.emptyList());
    }
    return response;
  }

}
