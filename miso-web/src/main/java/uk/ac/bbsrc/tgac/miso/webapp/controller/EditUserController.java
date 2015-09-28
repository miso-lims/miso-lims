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
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
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
import uk.ac.bbsrc.tgac.miso.core.security.PasswordCodecService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
@SessionAttributes("user")
public class EditUserController {
  protected static final Logger log = LoggerFactory.getLogger(EditUserController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private PasswordCodecService passwordCodecService;

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

  public void setPasswordCodecService(PasswordCodecService passwordCodecService) {
    this.passwordCodecService = passwordCodecService;
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

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public ModelAndView userForm(@PathVariable Long userId, ModelMap model, HttpServletRequest request) throws SecurityException, IOException {
    try {
      User user = securityManager.getUserById(userId);
      if (user != null) {
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getLoginName())) {
          model.put("user", user);
          String securityMethod = (String)request.getSession().getServletContext().getAttribute("security.method");
          model.put("securityMethod", securityMethod);

          return new ModelAndView("/pages/editUser.jsp", model);
        }
        else {
          throw new SecurityException("You can only edit your own user details.");
        }
      }
      else {
        throw new IOException("No such user");
      }
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show user", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/user/new", method = RequestMethod.GET)
  public ModelAndView newSetupForm(ModelMap model, HttpServletRequest request) throws IOException {
    String securityMethod = (String)request.getSession().getServletContext().getAttribute("security.method");
    if ("ldap".equals(securityMethod)) {
      throw new IOException("Cannot add users through the MISO interface for LDAP-managed security. Please add the LDAP user then log in as normal.");
    }
    else {
      return adminSetupForm(UserImpl.UNSAVED_ID, model, request);
    }
  }

  @RequestMapping(value = "/admin/user/{userId}", method = RequestMethod.GET)
  public ModelAndView adminSetupForm(@PathVariable Long userId,
                                     ModelMap model, HttpServletRequest request) throws IOException {
    try {
      model.put("user", userId == UserImpl.UNSAVED_ID ? dataObjectFactory.getUser()
                                                      : securityManager.getUserById(userId));

      String securityMethod = (String)request.getSession().getServletContext().getAttribute("security.method");
      model.put("securityMethod", securityMethod);

      return new ModelAndView("/pages/editUser.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show user", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/user", method = RequestMethod.POST)
  public String adminProcessSubmit(@ModelAttribute("user") User user,
                                   ModelMap model, SessionStatus session, HttpServletRequest request) throws IOException {
    try {
      if (user.getUserId() == UserImpl.UNSAVED_ID) {
        //new user. don't require a password to be set initially
        if (!LimsUtils.isStringEmptyOrNull(request.getParameter("newpassword")) && !LimsUtils.isStringEmptyOrNull(request.getParameter("confirmpassword"))) {
          if (request.getParameter("newpassword").equals(request.getParameter("confirmpassword"))) {
            if (!"".equals(request.getParameter("newpassword")) && !"".equals(request.getParameter("confirmpassword"))) {
              if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new GrantedAuthorityImpl("ROLE_ADMIN"))) {
                //auth'ed user is the account holder or an admin
                log.info("Admin '"+SecurityContextHolder.getContext().getAuthentication().getName()+"' attempting user password change for user '"+user.getLoginName()+"'");
                user.setPassword(request.getParameter("newpassword"));
              }
              else {
                throw new IOException("Cannot create user - user isn't an admin.");
              }
            }
            else {
              throw new IOException("New password cannot be empty");
            }
          }
          else {
            throw new IOException("New password and confirmation don't match.");
          }
        }
      }
      else {
        if (!LimsUtils.isStringEmptyOrNull(request.getParameter("password")) && !LimsUtils.isStringEmptyOrNull(request.getParameter("newpassword"))) {
          if (!LimsUtils.isStringEmptyOrNull(request.getParameter("confirmpassword"))) {
            if (request.getParameter("newpassword").equals(request.getParameter("confirmpassword"))) {
              if (!"".equals(request.getParameter("newpassword")) && !"".equals(request.getParameter("confirmpassword"))) {
                if (SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getLoginName())) {
                  if (passwordCodecService.getEncoder().isPasswordValid(user.getPassword(), request.getParameter("password"), null)) {
                    log.debug("User '"+user.getLoginName()+"' attempting own password change");
                    user.setPassword(request.getParameter("newpassword"));
                  }
                }
                else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new GrantedAuthorityImpl("ROLE_ADMIN"))) {
                  //auth'ed user is the account holder or an admin
                  log.info("Admin '"+SecurityContextHolder.getContext().getAuthentication().getName()+"' attempting user password change for user '"+user.getLoginName()+"'");
                  user.setPassword(request.getParameter("newpassword"));
                }
                else {
                  throw new IOException("Cannot update user - existing password check failed, or user isn't an admin.");
                }
              }
              else {
                throw new IOException("New password cannot be empty");
              }
            }
            else {
              throw new IOException("New password and confirmation don't match.");
            }
          }
          else {
            throw new IOException("You must supply a confirmation of your new password.");
          }
        }
      }

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

  @RequestMapping(value = "/user", method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("user") User user,
                                   ModelMap model, SessionStatus session, HttpServletRequest request) throws IOException {
    try {
      if (!LimsUtils.isStringEmptyOrNull(request.getParameter("password")) && !LimsUtils.isStringEmptyOrNull(request.getParameter("newpassword"))) {
        if (!LimsUtils.isStringEmptyOrNull(request.getParameter("confirmpassword"))) {
          if (request.getParameter("newpassword").equals(request.getParameter("confirmpassword"))) {
            if (!"".equals(request.getParameter("newpassword")) && !"".equals(request.getParameter("confirmpassword"))) {
              if (SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getLoginName())) {
                if (passwordCodecService.getEncoder().isPasswordValid(user.getPassword(), request.getParameter("password"), null)) {
                  log.debug("User '"+user.getLoginName()+"' attempting own password change");
                  user.setPassword(request.getParameter("newpassword"));
                }
              }
              else {
                throw new IOException("Cannot update user - existing password check failed, or user isn't an admin.");
              }
            }
            else {
              throw new IOException("New password cannot be empty");
            }
          }
          else {
            throw new IOException("New password and confirmation don't match.");
          }
        }
        else {
          throw new IOException("You must supply a confirmation of your new password.");
        }
      }

      securityManager.saveUser(user);
      session.setComplete();
      model.clear();
      return "redirect:/miso/myAccount";
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to update user", ex);
      }
      throw ex;
    }
  }
}
