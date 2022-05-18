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

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
public class EditUserController {
  protected static final Logger log = LoggerFactory.getLogger(EditUserController.class);

  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private UserService userService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @ModelAttribute("usersEditable")
  public boolean populateUsersEditable() {
    return securityManager.canCreateNewUser();
  }

  @ModelAttribute("mutablePassword")
  public boolean populateMutablePassword() {
    return securityManager.isPasswordMutable();
  }

  @GetMapping(value = "/user/{userId}")
  public ModelAndView userForm(@PathVariable long userId, ModelMap model, HttpServletRequest request)
      throws SecurityException, IOException {
    User user = authorizationManager.getCurrentUser();
    if (userId != user.getId()) {
      throw new AuthorizationException("You can only edit your own user details.");
    }
    return setupForm(user, model);
  }

  @GetMapping(value = "/admin/user/new")
  public ModelAndView newSetupForm(ModelMap model, HttpServletRequest request) throws IOException {
    authorizationManager.throwIfNonAdmin();
    if (!securityManager.canCreateNewUser()) {
      throw new IOException("Cannot add users through the MISO interface.");
    }
    return setupForm(new UserImpl(), model);
  }

  @GetMapping(value = "/admin/user/{userId}")
  public ModelAndView adminSetupForm(@PathVariable long userId, ModelMap model) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = userService.get(userId);
    if (user == null) {
      throw new NotFoundException("No user found for ID " + userId);
    }
    return setupForm(user, model);
  }

  private ModelAndView setupForm(User user, ModelMap model) throws IOException {
    model.put("title", user.isSaved() ? ("User " + user.getId()) : "New User");
    model.put("user", user);
    model.put("userDto", mapper.writeValueAsString(Dtos.asDto(user)));

    if (user.isSaved()) {
      model.put("groups", user.getGroups().stream().map(Dtos::asDto).collect(Collectors.toList()));
    }

    return new ModelAndView("/WEB-INF/pages/editUser.jsp", model);
  }

}
