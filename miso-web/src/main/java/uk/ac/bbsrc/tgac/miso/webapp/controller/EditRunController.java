/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedRunException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.runstats.client.RunStatsException;
import uk.ac.bbsrc.tgac.miso.runstats.client.manager.RunStatsManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.webapp.context.ApplicationContextProvider;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

import javax.xml.transform.TransformerException;

@Controller
@RequestMapping("/run")
@SessionAttributes("run")
public class EditRunController {
  protected static final Logger log = LoggerFactory.getLogger(EditRunController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private FilesManager filesManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private JdbcTemplate interfaceTemplate;

  @Autowired
  private RunAlertManager runAlertManager;

  private RunStatsManager runStatsManager;

  @Autowired
  private ApplicationContextProvider applicationContextProvider;

  public void setApplicationContextProvider(ApplicationContextProvider applicationContextProvider) {
    this.applicationContextProvider = applicationContextProvider;
  }

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRunAlertManager(RunAlertManager runAlertManager) {
    this.runAlertManager = runAlertManager;
  }

  public void setRunStatsManager(RunStatsManager runStatsManager) {
    this.runStatsManager = runStatsManager;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return DbUtils.getColumnSizes(interfaceTemplate, "Run");
  }

  @ModelAttribute("platformTypes")
  public Collection<String> populatePlatformTypes() {
    return PlatformType.getKeys();
  }

  @ModelAttribute("healthTypes")
  public Collection<String> populateHealthTypes() {
    return HealthType.getKeys();
  }

  @ModelAttribute("platforms")
  public Collection<Platform> populatePlatforms() throws IOException {
    return requestManager.listAllPlatforms();
  }

  public Boolean isMultiplexed(Run run) throws IOException {
    if (run != null && run.getId() != AbstractRun.UNSAVED_ID) {
      for (SequencerPartitionContainer<SequencerPoolPartition> f : run.getSequencerPartitionContainers()) {
        for (SequencerPoolPartition p : f.getPartitions()) {
          if (p.getPool() != null && p.getPool().getDilutions().size() > 1) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @ModelAttribute("metrixEnabled")
  public Boolean isMetrixEnabled() {
    MisoPropertyExporter exporter = (MisoPropertyExporter)applicationContextProvider.getApplicationContext().getBean("propertyConfigurer");
    Map<String, String> misoProperties = exporter.getResolvedProperties();
    return misoProperties.containsKey("miso.notification.interop.enabled") && Boolean.parseBoolean(misoProperties.get("miso.notification.interop.enabled"));
  }


  public Boolean hasOperationsQcPassed(Run run) throws IOException {
    if (run != null && run.getId() != AbstractRun.UNSAVED_ID) {
      for (RunQC qc : run.getRunQCs()) {
        if ("SeqOps QC".equals(qc.getQcType().getName()) && !qc.getDoNotProcess()) {
          return true;
        }
      }
    }
    return false;
  }

  public Boolean hasInformaticsQcPassed(Run run) throws IOException {
    if (run != null && run.getId() != AbstractRun.UNSAVED_ID) {
      for (RunQC qc : run.getRunQCs()) {
        if ("SeqInfo QC".equals(qc.getQcType().getName()) && !qc.getDoNotProcess()) {
          return true;
        }
      }
    }
    return false;
  }

  public Map<Integer, String> populateRunFiles(Long runId) throws IOException {
    if (runId != AbstractRun.UNSAVED_ID) {
      Run s = requestManager.getRunById(runId);
      if (s != null) {
        Map<Integer, String> fileMap = new HashMap<Integer, String>();
        for (String f : filesManager.getFileNames(Run.class, runId.toString())) {
          fileMap.put(f.hashCode(), f);
        }
        return fileMap;
      }
    }
    return Collections.emptyMap();
  }

  public Collection<Pool<? extends Poolable>> populateAvailablePools(User user) throws IOException {
    return requestManager.listAllPools();
  }

  public Collection<Pool> populateAvailablePools(PlatformType platformType, User user) throws IOException {
    List<Pool> pools = new ArrayList<Pool>(requestManager.listAllPoolsByPlatform(platformType));
    Collections.sort(pools);
    return pools;
  }

  public Collection<Experiment> populateAvailableExperiments(User user) throws IOException {
    return requestManager.listAllExperiments();
  }

  public Collection<Experiment> populateAvailableExperiments(PlatformType platformType, User user) throws IOException {
    List<Experiment> exps = new ArrayList<Experiment>();
    for (Experiment e : requestManager.listAllExperiments()) {
      if (e.getPlatform() != null && e.getPlatform().getPlatformType().equals(platformType)) {
        exps.add(e);
      }
    }
    Collections.sort(exps);
    return exps;
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView newUnassignedRun(ModelMap model) throws IOException {
    //clear any existing run in the model
    model.addAttribute("run", null);
    return setupForm(AbstractRun.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/rest/{runId}", method = RequestMethod.GET)
  public
  @ResponseBody
  Run jsonRest(@PathVariable Long runId) throws IOException {
    return requestManager.getRunById(runId);
  }

  @RequestMapping(value = "/{runId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long runId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Run run = null;

      if (runId == AbstractRun.UNSAVED_ID) {
        run = dataObjectFactory.getRun(user);
        model.put("title", "New Run");
        model.put("availablePools", populateAvailablePools(user));
        model.put("multiplexed", false);
      }
      else {
        run = requestManager.getRunById(runId);
        if (run == null) {
          throw new SecurityException("No such Run.");
        }
        else {
          model.put("title", "Run " + runId);
          model.put("multiplexed", isMultiplexed(run));
          try {
            if (runStatsManager != null) {
              model.put("statsAvailable", runStatsManager.hasStatsForRun(run));
            }
            else {
              log.warn("No runStatsManager available. No stats will be parsed.");
            }
            model.put("operationsQcPassed", hasOperationsQcPassed(run));
            model.put("informaticsQcPassed", hasInformaticsQcPassed(run));
          }
          catch (RunStatsException e) {
            model.put("error", MisoWebUtils.generateErrorDivMessage("Cannot retrieve generate run stats: " + run.getAlias(), e.getMessage()));
            e.printStackTrace();
          }
        }
      }

      if (!run.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      if (run.getStatus() == null) {
        run.setStatus(new StatusImpl());
      }
      else {
        try {
          InputStream in = StatsController.class.getResourceAsStream("/status/xsl/"+run.getPlatformType().getKey().toLowerCase()+"/statusXml.xsl");
          if (in != null && run.getStatus().getXml() != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            model.put("statusXml", (SubmissionUtils.xslTransform(run.getStatus().getXml(), xsl)));
          }
        }
        catch (TransformerException e) {
          model.put("error", MisoWebUtils.generateErrorDivMessage("Cannot retrieve status XML for the given run: " + run.getAlias(), e.getMessage()));
          e.printStackTrace();
        }
      }

      model.put("formObj", run);
      model.put("run", run);
      model.put("runFiles", populateRunFiles(runId));

      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, run, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, run, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, run, securityManager.listAllGroups()));

      Map<Long, String> runMap = new HashMap<Long, String>();
      if (run.getWatchers().contains(user)) {
        runMap.put(run.getId(), user.getLoginName());
      }
      model.put("overviewMap", runMap);

      return new ModelAndView("/pages/editRun.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show run", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("run") Run run,
                              ModelMap model, SessionStatus session) throws IOException, MalformedRunException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!run.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }

      requestManager.saveRun(run);
      session.setComplete();
      model.clear();
      return "redirect:/miso/run/" + run.getId();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save run", ex);
      }
      throw ex;
    }
  }
}
