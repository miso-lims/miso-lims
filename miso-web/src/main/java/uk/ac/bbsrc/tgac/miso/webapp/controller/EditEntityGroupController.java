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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractEntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchicalEntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.HierarchicalEntityGroupImpl;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.workflow.Workflow;

import java.io.IOException;
import java.util.*;

@Controller
@SessionAttributes("entitygroup")
public class EditEntityGroupController {
  protected static final Logger log = LoggerFactory.getLogger(EditEntityGroupController.class);

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  @ModelAttribute("users")
  public Collection<User> populateUsers() throws IOException {
    try {
      return LimsSecurityUtils.getInternalUsers(securityManager.listAllUsers());
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list users", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("workflows")
  public Collection<Workflow> populateWorkflows() throws IOException {
    return requestManager.listAllWorkflows();
  }

  @ModelAttribute("currentUser")
  public User getCurrentUser() throws IOException {
    return securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
  }

  @RequestMapping(value = "/samplegroup/new", method = RequestMethod.GET)
  public ModelAndView setupSampleGroupForm(ModelMap model) throws IOException {
    return setupSampleGroupForm(AbstractEntityGroup.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/samplegroup/{entityGroupId}", method = RequestMethod.GET)
  public ModelAndView setupSampleGroupForm(@PathVariable Long entityGroupId, ModelMap model) throws IOException {
    try {
      HierarchicalEntityGroup<? extends Nameable, ? extends Nameable> samplegroup = requestManager.getEntityGroupById(entityGroupId);
      if (samplegroup != null) {
        model.put("formObj", samplegroup);
        model.put("entitygroup", samplegroup);
      }
      else {
        samplegroup = new HierarchicalEntityGroupImpl<>();
        samplegroup.setCreationDate(new Date());
        model.put("entitygroup", samplegroup);
      }

      return new ModelAndView("/pages/editSampleGroup.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show group", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String submitSampleGroupForm(@ModelAttribute("entitygroup") HierarchicalEntityGroup<Nameable, Nameable> entityGroup,
                              ModelMap model, SessionStatus session) throws IOException {
    try {
      requestManager.saveEntityGroup(entityGroup);
      session.setComplete();
      model.clear();
      return "redirect:/miso/samplegroup/"+entityGroup.getId();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save EntityGroup", ex);
      }
      throw ex;
    }
  }
}
