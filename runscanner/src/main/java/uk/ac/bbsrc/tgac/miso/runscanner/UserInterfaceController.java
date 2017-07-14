package uk.ac.bbsrc.tgac.miso.runscanner;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableSortedMap;

/**
 * Front-end status monitoring for run scanner
 */
@Controller
public class UserInterfaceController {
  /**
   * These are all the collections of files that the scheduler can report.s
   */
  private static final Map<String, Function<Scheduler, Iterable<File>>> COLLECTIONS = ImmutableSortedMap
      .<String, Function<Scheduler, Iterable<File>>> naturalOrder().put("Finished", Scheduler::getFinishedDirectories)
      .put("Scheduled", Scheduler::getScheduledWork).put("Processing", Scheduler::getCurrentWork).put("Instruments", Scheduler::getRoots)
      .put("Failed", Scheduler::getFailedDirectories).build();

  @Autowired
  private Scheduler scheduler;
  private final Instant startTime = Instant.now();

  /**
   * List a collection of files
   */
  @RequestMapping(value = "/list/{collection}", method = RequestMethod.GET)
  public ModelAndView listPaths(@PathVariable String collection, ModelMap model) throws IOException {
    model.put("runs", COLLECTIONS.containsKey(collection) ? COLLECTIONS.get(collection).apply(scheduler) : Collections.emptyList());
    model.put("collection", collection);
    return new ModelAndView("/pages/list.jsp", model);
  }

  @ModelAttribute("collections")
  public Iterable<String> populateCollections() {
    return COLLECTIONS.keySet();
  }

  @ModelAttribute("isScanningEnabled")
  public boolean populateIsScanningEnabled() {
    return scheduler.isScanningEnabled();
  }

  @ModelAttribute("isScanningNow")
  public boolean populateIsScanningNow() {
    return scheduler.isScanningNow();
  }

  @ModelAttribute("processors")
  public Iterable<RunProcessor.Builder> populateProcessors() {
    return RunProcessor.builders().sorted((a, b) -> {
      int c = a.getPlatformType().ordinal() - b.getPlatformType().ordinal();
      return c == 0 ? a.getName().compareTo(b.getName()) : c;
    }).collect(Collectors.toList());
  }

  @ModelAttribute("uptime")
  public String populateUpdate() {
    return Duration.between(startTime, Instant.now()).toString();
  }

  /**
   * Show the main status page
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ModelAndView showStatus(ModelMap model) throws IOException {
    model.put("finished", scheduler.getFinishedDirectories().size());
    model.put("scheduled", scheduler.getScheduledWork().size());
    model.put("configurations", scheduler.getConfiguration());
    model.put("isConfigurationGood", scheduler.isConfigurationGood());
    model.put("lastConfigurationRead", scheduler.getConfigurationLastRead());
    Instant lastScanTime = scheduler.getScanLastStarted();
    model.put("timeSinceLastScan", lastScanTime == null ? "Not yet scanned" : Duration.between(lastScanTime, Instant.now()).toString());
    return new ModelAndView("/pages/status.jsp", model);
  }
}
