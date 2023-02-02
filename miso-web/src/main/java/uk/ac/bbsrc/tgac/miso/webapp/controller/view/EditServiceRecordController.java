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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.ServiceRecordService;

@Controller
@RequestMapping("/instrument/servicerecord")
public class EditServiceRecordController {

  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private ServiceRecordService serviceRecordService;
  @Autowired
  private ObjectMapper mapper;

  // @GetMapping(value = "/{recordId}")
  // public ModelAndView viewServiceRecord(@PathVariable(value = "recordId") Long recordId, ModelMap
  // model)
  // throws IOException {
  // ServiceRecord record = serviceRecordService.get(recordId);
  // if (record == null) {
  // throw new NotFoundException("No service found for ID " + recordId.toString());
  // }
  // return showPage(record, model);
  // }

  // @GetMapping(value = "/new/{instrumentId}")
  // public ModelAndView newServiceRecord(@PathVariable(value = "instrumentId") Long instrumentId,
  // ModelMap model)
  // throws IOException {
  // Instrument instrument = instrumentService.get(instrumentId);
  // if (instrument == null) {
  // throw new NotFoundException("No instrument found for ID " + instrumentId.toString());
  // }
  // ServiceRecord record = new ServiceRecord();
  // record.setInstrument(instrument);
  // return showPage(record, model);
  // }

  // public ModelAndView showPage(ServiceRecord record, ModelMap model) throws JsonProcessingException
  // {
  // if (!record.isSaved()) {
  // model.put("title", "New Service Record");
  // } else {
  // model.put("title", "Service Record " + record.getId());
  // }
  // model.put("serviceRecord", record);
  // model.put("serviceRecordDto", mapper.writeValueAsString(Dtos.asDto(record)));
  // ArrayNode positions = mapper.createArrayNode();
  // for (InstrumentPosition pos : record.getInstrument().getInstrumentModel().getPositions()) {
  // ObjectNode dto = positions.addObject();
  // dto.put("id", pos.getId());
  // dto.put("alias", pos.getAlias());
  // }
  // model.put("instrumentPositions", mapper.writeValueAsString(positions));
  // return new ModelAndView("/WEB-INF/pages/editServiceRecord.jsp", model);
  // }

}
