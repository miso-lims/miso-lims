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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

@Controller
@RequestMapping("/note")
@SessionAttributes("user")
public class CreateNoteController {
  protected static final Logger log = LoggerFactory.getLogger(CreateNoteController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
  /*
   * @RequestMapping(method = RequestMethod.POST) public String processSubmit(
   * 
   * @RequestParam(value = "requestId", required = true) long requestId,
   * 
   * @RequestParam(value = "text", required = true) String text,
   * 
   * @RequestParam(value = "internalOnly", defaultValue = "false") boolean internalOnly, ModelMap model, SessionStatus session) throws
   * IOException { try { User user = securityManager .getUserByLoginName(SecurityContextHolder.getContext() .getAuthentication().getName());
   * Request request = requestManager.getRequestById(requestId); if (!request.userCanWrite(user)) { throw new SecurityException(
   * "Permission denied."); } Note note = request.createNote(user); note.setText(text); note.setInternalOnly(internalOnly);
   * requestManager.saveRequest(request); session.setComplete(); model.clear(); return "redirect:/miso/request/view/" + requestId; } catch
   * (IOException ex) { if (log.isDebugEnabled()) { log.debug("Failed to create note", ex); } throw ex; } }
   */
}
