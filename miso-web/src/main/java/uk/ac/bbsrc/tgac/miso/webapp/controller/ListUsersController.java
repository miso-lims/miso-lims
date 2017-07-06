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
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;

@Controller
public class ListUsersController {
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  @Qualifier("sessionRegistry")
  private SessionRegistry sessionRegistry;

  private final ListItemsPage usersPage = new ListItemsPage("user") {

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      config.put("isAdmin", user.isAdmin());
      config.put("isTech", Arrays.asList(user.getRoles()).contains("ROLE_TECH"));
    }

  };

  @RequestMapping("/admin/users")
  public ModelAndView adminListUsers(ModelMap model) throws IOException {
    Set<String> loggedIn = sessionRegistry.getAllPrincipals().stream()
        .filter(u -> !sessionRegistry.getAllSessions(u, false).isEmpty())
        .map(u -> ((org.springframework.security.core.userdetails.User) u).getUsername())
        .collect(Collectors.toSet());

    return usersPage.list(model,
        securityManager.listAllUsers().stream().map(Dtos::asDto).peek(user -> user.setLoggedIn(loggedIn.contains(user.getLoginName()))));
  }

  public void setSecurityManager(SecurityManager securityManager) {
    assert (securityManager != null);
    this.securityManager = securityManager;
  }

  @RequestMapping("/tech/users")
  public ModelAndView techListUsers(ModelMap model) throws IOException {
    return adminListUsers(model);
  }

  @ModelAttribute("title")
  public String title() {
    return "Users";
  }
}
