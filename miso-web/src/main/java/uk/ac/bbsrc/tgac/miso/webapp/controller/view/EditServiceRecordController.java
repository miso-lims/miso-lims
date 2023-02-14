/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK MISO project contacts: Robert Davey @
 * TGAC *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MISO. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/instrument/{instrumentId}/servicerecord")
public class EditServiceRecordController {

  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private ServiceRecordService serviceRecordService;
  @Autowired
  private ObjectMapper mapper;

  @GetMapping(value = "/{recordId}")
  public ModelAndView viewServiceRecord(@PathVariable(value = "recordId") Long recordId,
      @PathVariable(value = "instrumentId") Long instrumentId, ModelMap model)
      throws IOException {
    Instrument instrument = instrumentService.get(instrumentId);
    ServiceRecord record = serviceRecordService.get(recordId);
    if (record == null) {
      throw new NotFoundException("No service found for ID " + recordId.toString());
    }
    return showPage(record, instrument, model);
  }

  @GetMapping(value = "/new")
  public ModelAndView newServiceRecord(@PathVariable(value = "instrumentId") Long instrumentId, ModelMap model)
      throws IOException {
    Instrument instrument = instrumentService.get(instrumentId);
    if (instrument == null) {
      throw new NotFoundException("No instrument found for ID " + instrumentId.toString());
    }
    ServiceRecord record = new ServiceRecord();
    return showPage(record, instrument, model);
  }

  public ModelAndView showPage(ServiceRecord record, Instrument instrument, ModelMap model)
      throws JsonProcessingException, IOException {
    if (!record.isSaved()) {
      model.put("title", "New Service Record");
    } else {
      model.put("title", "Service Record " + record.getId());
    }
    model.put("serviceRecord", record);
    model.put("serviceRecordDto", mapper.writeValueAsString(Dtos.asDto(record)));
    ArrayNode positions = mapper.createArrayNode();
    for (InstrumentPosition pos : instrument.getInstrumentModel().getPositions()) {
      ObjectNode dto = positions.addObject();
      dto.put("id", pos.getId());
      dto.put("alias", pos.getAlias());
    }
    model.put("instrumentPositions", mapper.writeValueAsString(positions));
    return new ModelAndView("/WEB-INF/pages/editServiceRecord.jsp", model);
  }

}
