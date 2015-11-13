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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractExperiment;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Controller
@RequestMapping("/experiment")
@SessionAttributes("experiment")
public class EditExperimentController {
  protected static final Logger log = LoggerFactory.getLogger(EditExperimentController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  @Autowired
  private JdbcTemplate interfaceTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return DbUtils.getColumnSizes(interfaceTemplate, "Experiment");
  }

  @ModelAttribute("platforms")
  public Collection<Platform> populatePlatforms() throws IOException {
    return requestManager.listAllPlatforms();
  }

  public Collection<? extends Pool> populateAvailablePools(User user, Experiment experiment) throws IOException {
    if (experiment.getPlatform() != null) {
      List<Pool> pools = new ArrayList<Pool>();
      for (Pool p : requestManager.listAllPoolsByPlatform(experiment.getPlatform().getPlatformType())) {
        if (experiment.getPool() == null || !experiment.getPool().equals(p)) {
          pools.add(p);
        }
        Collections.sort(pools);
      }
      return pools;
    }
    return Collections.emptyList();
  }

  @RequestMapping(value = "/new/{studyId}", method = RequestMethod.GET)
  public ModelAndView newAssignedExperiment(@PathVariable Long studyId, ModelMap model) throws IOException {
    return setupForm(AbstractExperiment.UNSAVED_ID, studyId, model);
  }

  @RequestMapping(value = "/rest/{experimentId}", method = RequestMethod.GET)
  public @ResponseBody Experiment jsonRest(@PathVariable Long experimentId) throws IOException {
    return requestManager.getExperimentById(experimentId);
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return requestManager.listAllChanges("Experiment");
  }

  @RequestMapping(value = "/{experimentId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long experimentId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Experiment experiment = requestManager.getExperimentById(experimentId);

      if (experiment == null) {
        throw new SecurityException("No such Experiment");
      }
      if (!experiment.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", experiment);
      model.put("experiment", experiment);
      model.put("libraryKits", experiment.getKitsByKitType(KitType.LIBRARY));
      model.put("emPcrKits", experiment.getKitsByKitType(KitType.EMPCR));
      model.put("clusteringKits", experiment.getKitsByKitType(KitType.CLUSTERING));
      model.put("sequencingKits", experiment.getKitsByKitType(KitType.SEQUENCING));
      model.put("multiplexingKits", experiment.getKitsByKitType(KitType.MULTIPLEXING));
      model.put("availablePools", populateAvailablePools(user, experiment));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, experiment, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, experiment, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, experiment, securityManager.listAllGroups()));
      model.put("title", "Experiment " + experimentId);
      return new ModelAndView("/pages/editExperiment.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show experiment", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/{experimentId}/study/{studyId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long experimentId, @PathVariable Long studyId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Experiment experiment = null;
      if (experimentId == AbstractExperiment.UNSAVED_ID) {
        experiment = dataObjectFactory.getExperiment();
        model.put("title", "New Experiment");
      } else {
        experiment = requestManager.getExperimentById(experimentId);
        model.put("title", "Experiment " + experimentId);
      }

      if (experiment == null) {
        throw new SecurityException("No such Experiment");
      }

      if (!experiment.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      if (studyId != null) {
        Study study = requestManager.getStudyById(studyId);
        model.addAttribute("study", study);
        experiment.setStudy(study);
        if (Arrays.asList(user.getRoles()).contains("ROLE_TECH")) {
          SecurityProfile sp = new SecurityProfile(user);
          LimsUtils.inheritUsersAndGroups(experiment, study.getSecurityProfile());
          sp.setOwner(user);
          experiment.setSecurityProfile(sp);
        } else {
          experiment.inheritPermissions(study);
        }
      }

      model.put("formObj", experiment);
      model.put("experiment", experiment);
      model.put("libraryKits", experiment.getKitsByKitType(KitType.LIBRARY));
      model.put("emPcrKits", experiment.getKitsByKitType(KitType.EMPCR));
      model.put("clusteringKits", experiment.getKitsByKitType(KitType.CLUSTERING));
      model.put("sequencingKits", experiment.getKitsByKitType(KitType.SEQUENCING));
      model.put("multiplexingKits", experiment.getKitsByKitType(KitType.MULTIPLEXING));
      model.put("availablePools", populateAvailablePools(user, experiment));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, experiment, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, experiment, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, experiment, securityManager.listAllGroups()));
      return new ModelAndView("/pages/editExperiment.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show experiment", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("experiment") Experiment experiment, ModelMap model, SessionStatus session)
      throws IOException, MalformedExperimentException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!experiment.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      experiment.setLastModifier(user);
      requestManager.saveExperiment(experiment);
      session.setComplete();
      model.clear();
      return "redirect:/miso/experiment/" + experiment.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save Experiment", ex);
      }
      throw ex;
    }
  }
}
