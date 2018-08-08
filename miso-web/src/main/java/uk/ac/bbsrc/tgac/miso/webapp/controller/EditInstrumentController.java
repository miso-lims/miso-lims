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
import org.springframework.security.acls.model.NotFoundException;
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

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.service.ServiceRecordService;

@Controller
@RequestMapping("/instrument")
@SessionAttributes("instrument")
public class EditInstrumentController {

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private InstrumentService instrumentService;

  @Autowired
  private ServiceRecordService serviceRecordService;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setInstrumentService(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return instrumentService.getColumnSizes();
  }

  @RequestMapping("/{instrumentId}")
  public ModelAndView viewInstrument(@PathVariable(value = "instrumentId") Long instrumentId, ModelMap model) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Instrument sr = instrumentService.get(instrumentId);
    Collection<ServiceRecord> serviceRecords = serviceRecordService.listByInstrument(instrumentId);

    if (sr == null) throw new NotFoundException("No instrument found for ID " + instrumentId.toString());
    if (user.isAdmin()) {
      model.put("otherInstruments",
          instrumentService.list().stream().filter(other -> other.getId() != instrumentId).collect(Collectors.toList()));
    }
    model.put("preUpgradeInstrument", instrumentService.getByUpgradedInstrumentId(sr.getId()));

    model.put("instrument", sr);
    model.put("serviceRecords", serviceRecords.stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("title", "Instrument " + sr.getId());
    String ip = sr.getIpAddress() == null ? "" : sr.getIpAddress();
    model.put("trimmedIpAddress", ip.startsWith("/") ? ip.substring(1) : ip);
    return new ModelAndView("/pages/editInstrument.jsp", model);
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("instrument") Instrument sr, ModelMap model, SessionStatus session)
      throws IOException {
    Long srId = null;
    if (sr.getId() == InstrumentImpl.UNSAVED_ID) {
      srId = instrumentService.create(sr);
    } else {
      instrumentService.update(sr);
      srId = sr.getId();
    }
    session.setComplete();
    model.clear();
    return "redirect:/miso/instrument/" + srId;
  }

}
