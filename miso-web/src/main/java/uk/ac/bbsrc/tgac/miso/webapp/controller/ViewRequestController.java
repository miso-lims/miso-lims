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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.RequestControllerHelperLoader;

@Controller
public class ViewRequestController {
  protected static final Logger log = LoggerFactory.getLogger(ViewRequestController.class);

  // @Autowired
  // private RequestControllerHelperLoader requestControllerHelperLoader;

  // @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private ProtocolManager protocolManager;

  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  public void setRequestHelperLoader(RequestControllerHelperLoader requestControllerHelperLoader) {
    // this.requestControllerHelperLoader = requestControllerHelperLoader;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }
  /*
   * @RequestMapping("/request/view/${requestId}") public ModelAndView viewRequest(
   * 
   * @RequestParam(value = "requestId", required = true) long requestId) throws IOException { try { User user = securityManager
   * .getUserByLoginName(SecurityContextHolder.getContext() .getAuthentication().getName()); Request request =
   * requestManager.getRequestById(requestId); if (!request.userCanRead(user)) { throw new SecurityException("Permission denied."); } return
   * new ModelAndView("/pages/viewRequest.jsp", "request", request); } catch (IOException ex) { if (log.isDebugEnabled()) { log.debug(
   * "Failed to show request", ex); } throw ex; } }
   * 
   * @RequestMapping("/request/view/${requestId}/${executionCount}") public String listRequestResults(
   * 
   * @RequestParam(value = "requestId", required = true) long requestId,
   * 
   * @RequestParam(value = "executionCount", required = true) int executionCount) throws IOException { try { User user = securityManager
   * .getUserByLoginName(SecurityContextHolder.getContext() .getAuthentication().getName()); Request request =
   * requestManager.getRequestById(requestId); if (!request.userCanRead(user)) { throw new SecurityException("Permission denied."); } return
   * "redirect:" + requestControllerHelperLoader.getHelper( protocolManager.getProtocol(request .getProtocolUniqueIdentifier()))
   * .getResultsControllerUrl() + "/" + requestId + "/" + executionCount; } catch (IOException ex) { if (log.isDebugEnabled()) { log.debug(
   * "Failed to show request results", ex); } throw ex; } }
   */
}
