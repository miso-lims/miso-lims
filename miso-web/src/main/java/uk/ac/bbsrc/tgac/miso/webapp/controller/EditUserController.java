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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Activity;
import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Protocol;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;

@Controller
@SessionAttributes("user")
public class EditUserController {
  protected static final Logger log = LoggerFactory.getLogger(EditUserController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private ProtocolManager protocolManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @ModelAttribute("groups")
  public Collection<Group> populateGroups() throws IOException {
    try {
      List<Group> groups = new ArrayList<Group>(securityManager.listAllGroups());
      Collections.sort(groups);
      return groups;
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list groups", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("roles")
  public Collection<String> populateRoles() throws IOException {
    try {
      Collection<String> roles = new ArrayList<String>();
      for (Protocol protocol : protocolManager.listAllProtocols()) {
        roles.add(protocol.getRole());
      }
      for (Activity activity : protocolManager.listAllActivities()) {
        roles.add(activity.getRole());
      }
      return roles;
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list roles", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/user/new", method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap model) throws IOException {
    return adminSetupForm(UserImpl.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/tech/user/{userId}", method = RequestMethod.GET)
  public ModelAndView techSetupForm(@PathVariable Long userId,
                                    ModelMap model) throws IOException {
    try {
      model.put("user", userId == UserImpl.UNSAVED_ID ? dataObjectFactory.getUser()
                                                      : securityManager.getUserById(userId));
      return new ModelAndView("/pages/editUser.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show user", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/user/{userId}", method = RequestMethod.GET)
  public ModelAndView adminSetupForm(@PathVariable Long userId,
                                     ModelMap model) throws IOException {
    try {
      model.put("user", userId == UserImpl.UNSAVED_ID ? dataObjectFactory.getUser()
                                                      : securityManager.getUserById(userId));
      return new ModelAndView("/pages/editUser.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show user", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/tech/user/{userId}", method = RequestMethod.POST)
  public String techProcessSubmit(@ModelAttribute("user") User user,
                                  ModelMap model, SessionStatus session) throws IOException {
    try {
      securityManager.saveUser(user);
      session.setComplete();
      model.clear();
      return "redirect:/miso/tech/users";
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to update user", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/user/{userId}", method = RequestMethod.POST)
  public String adminProcessSubmit(@ModelAttribute("user") User user,
                                   ModelMap model, SessionStatus session) throws IOException {
    try {
      securityManager.saveUser(user);
      session.setComplete();
      model.clear();
      return "redirect:/miso/admin/users";
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to update user", ex);
      }
      throw ex;
    }
  }
}
