/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.AuthorizationIOException;
import uk.ac.bbsrc.tgac.miso.core.security.PasswordCodecService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@SessionAttributes("user")
public class EditUserController {
  protected static final Logger log = LoggerFactory.getLogger(EditUserController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private PasswordCodecService passwordCodecService;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setPasswordCodecService(PasswordCodecService passwordCodecService) {
    this.passwordCodecService = passwordCodecService;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return securityManager.getUserColumnSizes();
  }

  @ModelAttribute("groups")
  public Collection<Group> populateGroups() throws IOException {
    try {
      List<Group> groups = new ArrayList<>(securityManager.listAllGroups());
      Collections.sort(groups);
      return groups;
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list groups", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("mutablePassword")
  public boolean populateMutablePassword() {
    return securityManager.isPasswordMutable();
  }

  @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
  public ModelAndView userForm(@PathVariable Long userId, ModelMap model, HttpServletRequest request)
      throws SecurityException, IOException {
    try {
      User user = securityManager.getUserById(userId);
      if (user != null) {
        if (authorizationManager.getCurrentUser().getLoginName().equals(user.getLoginName())) {
          model.put("user", user);

          return new ModelAndView("/pages/editUser.jsp", model);
        } else {
          throw new SecurityException("You can only edit your own user details.");
        }
      } else {
        throw new IOException("No such user");
      }
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show user", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/user/new", method = RequestMethod.GET)
  public ModelAndView newSetupForm(ModelMap model, HttpServletRequest request) throws IOException {
    if (securityManager.canCreateNewUser()) {
      return adminSetupForm(UserImpl.UNSAVED_ID, model, request);
    }
    throw new IOException(
        "Cannot add users through the MISO interface.");
  }

  @RequestMapping(value = "/admin/user/{userId}", method = RequestMethod.GET)
  public ModelAndView adminSetupForm(@PathVariable Long userId, ModelMap model, HttpServletRequest request) throws IOException {
    try {
      model.put("user", userId == UserImpl.UNSAVED_ID ? new UserImpl() : securityManager.getUserById(userId));
      model.put("title", userId == UserImpl.UNSAVED_ID ? "New User" : ("User " + userId));
      return new ModelAndView("/pages/editUser.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show user", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/admin/user", method = RequestMethod.POST)
  public String adminProcessSubmit(@ModelAttribute("user") User user, ModelMap model, SessionStatus session, HttpServletRequest request)
      throws IOException {
    if (!authorizationManager.isAdminUser()) {
      throw new IOException("Only administrator can use admin edit page.");
    }

    user.setPassword(null); // null password has the effect of keeping the current password in AbstractSecurityManager
    processPasswordInput(request, user);

    securityManager.saveUser(user);
    session.setComplete();
    model.clear();
    return "redirect:/miso/admin/users";
  }

  private void processPasswordInput(HttpServletRequest request, User user) throws IOException {
    if (!securityManager.isPasswordMutable()) {
      throw new IOException("Cannot change password in MISO directly. Please change your password as directed by your IT department.");
    }
    String newPassword = request.getParameter("newpassword");
    String confirmPassword = request.getParameter("confirmpassword");
    if (!isStringEmptyOrNull(newPassword) || !isStringEmptyOrNull(confirmPassword)) {
      if (isStringEmptyOrNull(newPassword) || isStringEmptyOrNull(confirmPassword)) {
        throw new IOException("New password cannot be empty");
      }
      if (!request.getParameter("newpassword").equals(request.getParameter("confirmpassword"))) {
        throw new IOException("New password and confirmation don't match.");
      }
      user.setPassword(passwordCodecService.encrypt(newPassword));
    }
  }

  @RequestMapping(value = "/user", method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("user") User user, ModelMap model, SessionStatus session, HttpServletRequest request)
      throws IOException {
    User original = authorizationManager.getCurrentUser();
    if (!original.getUserId().equals(user.getUserId())) {
      throw new AuthorizationIOException("Permission denied.");
    }

    user.setPassword(null); // null password has the effect of keeping the current password in AbstractSecurityManager
    if (!isStringEmptyOrNull(request.getParameter("currentPassword"))) {
      if (!passwordCodecService.getEncoder().isPasswordValid(original.getPassword(), request.getParameter("currentPassword"), null)) {
        throw new IOException("Existing password does not match.");
      }
      processPasswordInput(request, user);
    }

    securityManager.saveUser(user);
    session.setComplete();
    model.clear();
    return "redirect:/miso/myAccount";
  }
}
