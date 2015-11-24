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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.eaglegenomics.simlims.core.ActivitySessionFactory;
import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.webapp.util.ActivityControllerHelperLoader;

@Controller
@SessionAttributes({ "activitySession", "dataModel" })
public class EditActivityController {
  protected static final Logger log = LoggerFactory.getLogger(EditActivityController.class);

  private InputDataXmlView inputDataXmlView;

  private ActivityControllerHelperLoader activityControllerHelperLoader;

  private SecurityManager securityManager;

  private ProtocolManager protocolManager;

  private ActivitySessionFactory activitySessionFactory;

  public void setInputDataXmlView(InputDataXmlView inputDataXmlView) {
    this.inputDataXmlView = inputDataXmlView;
  }

  public void setActivitySessionFactory(ActivitySessionFactory activitySessionFactory) {
    this.activitySessionFactory = activitySessionFactory;
  }

  public void setActivityHelperLoader(ActivityControllerHelperLoader activityControllerHelperLoader) {
    this.activityControllerHelperLoader = activityControllerHelperLoader;
  }

  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }
}
