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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;

@Controller
@SessionAttributes("user")
public class EditGroupController {
  protected static final Logger log = LoggerFactory.getLogger(EditGroupController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @ModelAttribute("users")
  public Collection<User> populateUsers() throws IOException {
    try {
      List<User> users = new ArrayList<User>(securityManager.listAllUsers());
      Collections.sort(users);
      return users;
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list users", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/group/new", method = RequestMethod.GET)
  public ModelAndView adminSetupForm(ModelMap model) throws IOException {
    return adminSetupForm(Group.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/tech/group/new", method = RequestMethod.GET)
  public ModelAndView techSetupForm(ModelMap model) throws IOException {
    return techSetupForm(Group.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/admin/group/{groupId}", method = RequestMethod.GET)
  public ModelAndView adminSetupForm(@PathVariable Long groupId, ModelMap model) throws IOException {
    try {
      model.put("group", groupId == Group.UNSAVED_ID ? dataObjectFactory.getGroup() : securityManager.getGroupById(groupId));
      return new ModelAndView("/pages/editGroup.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show group", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/tech/group/{groupId}", method = RequestMethod.GET)
  public ModelAndView techSetupForm(@PathVariable Long groupId, ModelMap model) throws IOException {
    return adminSetupForm(groupId, model);
  }

  @RequestMapping(value = "/admin/group/new", method = RequestMethod.POST)
  public String adminProcessSubmitNew(@ModelAttribute("group") Group group, ModelMap model, SessionStatus session) throws IOException {
    return adminProcessSubmit(group, model, session);
  }

  @RequestMapping(value = "/admin/group/{groupId}", method = RequestMethod.POST)
  public String adminProcessSubmitExisting(@PathVariable Long groupId, @ModelAttribute("group") Group group, ModelMap model,
      SessionStatus session) throws IOException {
    group.setGroupId(groupId);
    return adminProcessSubmit(group, model, session);
  }

  public String adminProcessSubmit(@ModelAttribute("group") Group group, ModelMap model, SessionStatus session) throws IOException {
    try {
      securityManager.saveGroup(group);
      session.setComplete();
      model.clear();
      return "redirect:/miso/admin/groups";
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save group", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/tech/group/new", method = RequestMethod.POST)
  public String techProcessSubmitNew(@ModelAttribute("group") Group group, ModelMap model, SessionStatus session) throws IOException {
    return techProcessSubmit(group, model, session);
  }

  @RequestMapping(value = "/tech/group/{groupId}", method = RequestMethod.POST)
  public String techProcessSubmitExisting(@ModelAttribute("group") Group group, ModelMap model, SessionStatus session) throws IOException {
    return techProcessSubmit(group, model, session);
  }

  public String techProcessSubmit(@ModelAttribute("group") Group group, ModelMap model, SessionStatus session) throws IOException {
    try {
      securityManager.saveGroup(group);
      session.setComplete();
      model.clear();
      return "redirect:/miso/tech/groups";
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save group", ex);
      }
      throw ex;
    }
  }
}
