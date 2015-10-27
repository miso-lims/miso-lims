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
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.Activity;
import com.eaglegenomics.simlims.core.ManualActivity;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

@Controller
public class ListActivitiesController {
  protected static final Logger log = LoggerFactory.getLogger(ListActivitiesController.class);

  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private ProtocolManager protocolManager;

  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  @RequestMapping("/activity/activities")
  public ModelAndView listActivities() throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Collection<Activity> activities = new HashSet<Activity>();
      for (Activity activity : protocolManager.listAllActivities()) {
        if (activity instanceof ManualActivity) {
          activities.add(activity);
        }
      }
      return new ModelAndView("/pages/listActivities.jsp", "activities", activities);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list activities", ex);
      }
      throw ex;
    }
  }
}
