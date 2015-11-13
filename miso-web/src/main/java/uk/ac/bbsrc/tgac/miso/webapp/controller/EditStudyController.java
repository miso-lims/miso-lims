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
import java.util.Arrays;
import java.util.Collection;
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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractStudy;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Controller
@RequestMapping("/study")
@SessionAttributes("study")
public class EditStudyController {
  protected static final Logger log = LoggerFactory.getLogger(EditStudyController.class);

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

  public Project populateProject(@PathVariable Long projectId) throws IOException {
    try {
      return requestManager.getProjectById(projectId);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to get parent project", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/rest/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> jsonRestChanges() throws IOException {
    return requestManager.listAllChanges("Study");
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return DbUtils.getColumnSizes(interfaceTemplate, "Study");
  }

  @ModelAttribute("studyTypes")
  public Collection<String> populateStudyTypes() throws IOException {
    return requestManager.listAllStudyTypes();
  }

  @RequestMapping(value = "/new/{projectId}", method = RequestMethod.GET)
  public ModelAndView newAssignedProject(@PathVariable Long projectId, ModelMap model) throws IOException {
    return setupForm(AbstractStudy.UNSAVED_ID, projectId, model);
  }

  @RequestMapping(value = "/rest/{studyId}", method = RequestMethod.GET)
  public @ResponseBody Study jsonRest(@PathVariable Long studyId) throws IOException {
    return requestManager.getStudyById(studyId);
  }

  @RequestMapping(value = "/{studyId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long studyId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Study study = requestManager.getStudyById(studyId);
      Project project;
      if (study != null) {
        if (!study.userCanRead(user)) {
          throw new SecurityException("Permission denied.");
        }
        project = study.getProject();
        model.put("formObj", study);
        model.put("project", project);
        model.put("study", study);
        model.put("title", "Study " + studyId);
      } else {
        throw new SecurityException("No such Study");
      }
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, study, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, study, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, study, securityManager.listAllGroups()));
      return new ModelAndView("/pages/editStudy.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Study", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/{studyId}/project/{projectId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long studyId, @PathVariable Long projectId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Study study = null;
      if (studyId == AbstractStudy.UNSAVED_ID) {
        study = dataObjectFactory.getStudy(user);
        model.put("title", "New Study");
      } else {
        study = requestManager.getStudyById(studyId);
        model.put("title", "Study " + studyId);
      }

      Project project = requestManager.getProjectById(projectId);
      model.addAttribute("project", project);
      study.setProject(project);
      if (Arrays.asList(user.getRoles()).contains("ROLE_TECH")) {
        SecurityProfile sp = new SecurityProfile(user);
        LimsUtils.inheritUsersAndGroups(study, project.getSecurityProfile());
        sp.setOwner(user);
        study.setSecurityProfile(sp);
      } else {
        study.inheritPermissions(project);
      }

      if (!study.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      model.put("formObj", study);
      model.put("study", study);
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, study, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, study, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, study, securityManager.listAllGroups()));
      return new ModelAndView("/pages/editStudy.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show Study", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("study") Study study, ModelMap model, SessionStatus session) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!study.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      study.setLastModifier(user);
      requestManager.saveStudy(study);
      session.setComplete();
      model.clear();
      return "redirect:/miso/study/" + study.getId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save Study", ex);
      }
      throw ex;
    }
  }
}
