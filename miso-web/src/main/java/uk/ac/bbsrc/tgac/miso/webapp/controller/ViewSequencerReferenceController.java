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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

/**
 * Controller for View (read-only) Sequencer Reference and Service Record pages. Redirects to /stats for 
 * write access if user is admin
 */
@Controller
@RequestMapping("/sequencer")
public class ViewSequencerReferenceController {

  private enum ModelKeys {
    
    SEQUENCER("sequencerReference"),
    RUNS("sequencerRuns"),
    RECORDS("sequencerServiceRecords"),
    TRIMMED_IP("trimmedIpAddress");
    
    private final String key;
    
    ModelKeys(String key) {
      this.key = key;
    }
    
    public String getKey() {
      return key;
    }
    
  }
  
  @Autowired
  private SecurityManager securityManager;
  
  @Autowired
  private RequestManager requestManager;
  
  @Autowired
  private EditServiceRecordController editServiceRecordController;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }
  
  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
  
  public void setEditServiceRecordController(EditServiceRecordController editServiceRecordController) {
    this.editServiceRecordController = editServiceRecordController;
  }
  
  @RequestMapping("/{referenceId}")
  public ModelAndView viewSequencer(@PathVariable(value = "referenceId") Long referenceId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    if (user.isAdmin()) {
      return new ModelAndView("redirect:/miso/stats/sequencer/" + referenceId);
    }
    else {
      SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
      Collection<Run> runs = requestManager.listRunsBySequencerId(referenceId);
      Collection<SequencerServiceRecord> serviceRecords = requestManager.listSequencerServiceRecordsBySequencerId(referenceId);
      
      if (sr != null) {
        model.put(ModelKeys.SEQUENCER.getKey(), sr);
        model.put(ModelKeys.RUNS.getKey(), runs);
        model.put(ModelKeys.RECORDS.getKey(), serviceRecords);
        model.put("title", "Edit Sequencer");
        String ip = sr.getIpAddress() == null ? "" : sr.getIpAddress().toString();
        if (ip.startsWith("/")) {
          model.put(ModelKeys.TRIMMED_IP.getKey(), ip.substring(1));
        } else {
          model.put(ModelKeys.TRIMMED_IP.getKey(), ip);
        }
      } else {
        throw new IOException("Cannot retrieve the requested sequencer reference");
      }
      return new ModelAndView("/pages/editSequencerReference.jsp", model);
    }
  }
  
  @RequestMapping("/servicerecord/{recordId}")
  public ModelAndView viewServiceRecord(@PathVariable("recordId") Long recordId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    if (user.isAdmin()) {
      return new ModelAndView("redirect:/miso/stats/sequencer/servicerecord/" + recordId);
    }
    else {
      return editServiceRecordController.viewServiceRecord(recordId, model);
    }
  }

}
