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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.Protocol;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.eaglegenomics.simlims.core.store.DataReferenceStore;

import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.RequestControllerHelperLoader;

@Controller
@RequestMapping("/request")
@SessionAttributes({ "request", "dataModel" })
public class EditRequestController {
  protected static final Logger log = LoggerFactory.getLogger(EditRequestController.class);

  private RequestControllerHelperLoader requestControllerHelperLoader;

  private SecurityManager securityManager;

  private RequestManager requestManager;

  private ProtocolManager protocolManager;

  private DataReferenceStore dataReferenceStore;

  public void setRequestHelperLoader(RequestControllerHelperLoader requestControllerHelperLoader) {
    this.requestControllerHelperLoader = requestControllerHelperLoader;
  }

  public void setDataReferenceStore(DataReferenceStore dataReferenceStore) {
    this.dataReferenceStore = dataReferenceStore;
  }

  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @ModelAttribute("protocols")
  public Collection<Protocol> populateProtocols() throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Collection<Protocol> protocols = new ArrayList<Protocol>();
      for (Protocol protocol : protocolManager.listAllProtocols()) {
        if (protocol.userCanWrite(user)) {
          protocols.add(protocol);
        }
      }
      return protocols;
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list protocols", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("users")
  public Collection<User> populateUsers() throws IOException {
    try {
      return securityManager.listAllUsers();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list users", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("groups")
  public Collection<Group> populateGroups() throws IOException {
    try {
      return securityManager.listAllGroups();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to list groups", ex);
      }
      throw ex;
    }
  }
}
