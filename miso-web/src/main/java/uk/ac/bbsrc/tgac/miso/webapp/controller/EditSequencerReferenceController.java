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
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;

/**
 * Controller for View (read-only) Sequencer and Service Record pages. Redirects to /stats for
 * write access if user is admin
 */
@Controller
@RequestMapping("/sequencer")
@SessionAttributes("sequencerReference")
public class EditSequencerReferenceController {

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private SequencerReferenceService sequencerReferenceService;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setSequencerReferenceService(SequencerReferenceService sequencerReferenceService) {
    this.sequencerReferenceService = sequencerReferenceService;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return sequencerReferenceService.getSequencerReferenceColumnSizes();
  }

  @RequestMapping("/{referenceId}")
  public ModelAndView viewSequencer(@PathVariable(value = "referenceId") Long referenceId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    SequencerReference sr = sequencerReferenceService.get(referenceId);
    Collection<SequencerServiceRecord> serviceRecords = sequencerReferenceService.listServiceRecordsByInstrument(referenceId);

    if (sr == null) {
      throw new IOException("Cannot retrieve the requested sequencer reference");
    }
    if (user.isAdmin()) {
      model.put("otherSequencerReferences",
          sequencerReferenceService.list().stream().filter(other -> other.getId() != referenceId).collect(Collectors.toList()));
    }
    model.put("preUpgradeSeqRef", sequencerReferenceService.getByUpgradedReferenceId(sr.getId()));

    model.put("sequencerReference", sr);
    model.put("sequencerServiceRecords", serviceRecords);
    model.put("title", "Sequencer " + sr.getId());
    String ip = sr.getIpAddress() == null ? "" : sr.getIpAddress().toString();
    model.put("trimmedIpAddress", ip.startsWith("/") ? ip.substring(1) : ip);
    return new ModelAndView("/pages/editSequencerReference.jsp", model);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("sequencerReference") SequencerReference sr, ModelMap model, SessionStatus session)
      throws IOException {
    Long srId = null;
    if (sr.getId() == SequencerReferenceImpl.UNSAVED_ID) {
      srId = sequencerReferenceService.create(sr);
    } else {
      sequencerReferenceService.update(sr);
      srId = sr.getId();
    }
    session.setComplete();
    model.clear();
    return "redirect:/miso/sequencer/" + srId;
  }

}
