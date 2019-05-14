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

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPageWithAuthorization;

@Controller
public class ListUsersController {
  private static String getUsername(Object user) {
    if (user instanceof org.springframework.security.core.userdetails.User)
      return ((org.springframework.security.core.userdetails.User) user).getUsername();
    if (user instanceof InetOrgPerson) return ((InetOrgPerson) user).getUsername();
    throw new IllegalArgumentException("User principal of unsupported type: " + user.getClass().getName());
  }

  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private UserService userService;

  @Autowired
  @Qualifier("sessionRegistry")
  private SessionRegistry sessionRegistry;

  private final ListItemsPage usersPage = new ListItemsPageWithAuthorization("user", this::getAuthorizationManager) {
    @Override
    protected void writeConfigurationExtra(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("allowCreateUser", securityManager.canCreateNewUser());
      config.put("listMode", "list");
    }
  };

  @RequestMapping("/admin/users")
  public ModelAndView adminListUsers(ModelMap model) throws IOException {
    Set<String> loggedIn = sessionRegistry.getAllPrincipals().stream()
        .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
        .map(ListUsersController::getUsername)
        .collect(Collectors.toSet());

    return usersPage.list(model,
        userService.list().stream().map(Dtos::asDto).peek(user -> user.setLoggedIn(loggedIn.contains(user.getLoginName()))));
  }

  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @ModelAttribute("title")
  public String title() {
    return "Users";
  }
}
