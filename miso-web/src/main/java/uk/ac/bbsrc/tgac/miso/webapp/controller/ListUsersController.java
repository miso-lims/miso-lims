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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

@Controller
public class ListUsersController {
  protected static final Logger log = LoggerFactory.getLogger(ListUsersController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private SessionRegistryImpl sessionRegistry;

  public void setSecurityManager(SecurityManager securityManager) {
    assert (securityManager != null);
    this.securityManager = securityManager;
  }

  @RequestMapping("/admin/users")
  public ModelAndView adminListUsers(ModelMap model) throws IOException {
    try {

      log.info("Logged in users: " + sessionRegistry.getAllPrincipals().size());

      List<Object> nonExpiredUsers = new ArrayList<Object>();
      for (Object o : sessionRegistry.getAllPrincipals()) {
        List<SessionInformation> sessinfo = sessionRegistry.getAllSessions(o, false);
        for (SessionInformation si : sessinfo) {
          if (!si.isExpired()) {
            nonExpiredUsers.add(o);
          }
        }
      }
      model.addAttribute("loggedInUsers", nonExpiredUsers);
      model.addAttribute("total", nonExpiredUsers.size());

      return new ModelAndView("/pages/listUsers.jsp", "users", securityManager.listAllUsers());
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list users", ex);
      }
      throw ex;
    }
  }

  @RequestMapping("/tech/users")
  public ModelAndView techListUsers() throws IOException {
    try {
      return new ModelAndView("/pages/listUsers.jsp", "users", securityManager.listAllUsers());
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list users", ex);
      }
      throw ex;
    }
  }
}
