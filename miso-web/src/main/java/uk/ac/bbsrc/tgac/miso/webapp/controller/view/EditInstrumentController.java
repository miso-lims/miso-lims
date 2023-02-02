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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentDto;
import uk.ac.bbsrc.tgac.miso.dto.WorkstationDto;

@Controller
@RequestMapping("/instrument")
public class EditInstrumentController {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private ServiceRecordService serviceRecordService;
  @Autowired
  private WorkstationService workstationService;
  @Autowired
  private ObjectMapper mapper;

  public void setInstrumentService(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    authorizationManager.throwIfNonAdmin();
    model.put("title", "New Instrument");
    return setupForm(new InstrumentImpl(), model);
  }

  @GetMapping("/{instrumentId}")
  public ModelAndView viewInstrument(@PathVariable(value = "instrumentId") Long instrumentId, ModelMap model)
      throws IOException {
    Instrument instrument = instrumentService.get(instrumentId);
    if (instrument == null) {
      throw new NotFoundException("No instrument found for ID " + instrumentId.toString());
    }
    model.put("title", "Instrument " + instrument.getId());

    Collection<ServiceRecord> serviceRecords = instrument.getServiceRecords();
    model.put("serviceRecords", serviceRecords.stream().map(Dtos::asDto).collect(Collectors.toList()));
    return setupForm(instrument, model);
  }

  @GetMapping(value = "/{instrumentId}/serviceRecord/new/{recordId}")
  public ModelAndView newServiceRecord(@PathVariable(value = "instrumentId") Long instrumentId,
      @PathVariable(value = "recordId") Long recordId, ModelMap model) throws IOException {
    Instrument instrument = instrumentService.get(instrumentId);
    if (instrument == null) {
      throw new NotFoundException("No instrument found for ID " + instrumentId.toString());
    }
    ServiceRecord record = serviceRecordService.get(recordId);
    instrumentService.addServiceRecord(record, instrument);
    return showPage(record, instrument, model);
  }

  @GetMapping(value = "/{instrumentId}/serviceRecord/edit/{recordId}")
  public ModelAndView editServiceRecord(@PathVariable(value = "instrumentId") Long instrumentId,
      @PathVariable(value = "recordId") Long recordId, ModelMap model) throws IOException {
    Instrument instrument = instrumentService.get(instrumentId);
    if (instrument == null) {
      throw new NotFoundException("No instrument found for ID " + instrumentId.toString());
    }
    ServiceRecord record = serviceRecordService.get(recordId);
    if (record == null) {
      throw new NotFoundException("No record found for ID " + recordId.toString());
    }

    instrumentService.updateServiceRecord(record, instrument);
    return showPage(record, instrument, model);
  }

  private ModelAndView setupForm(Instrument instrument, ModelMap model) throws IOException {
    InstrumentDto instrumentDto = Dtos.asDto(instrument);

    if (instrument.isSaved()) {
      Instrument preUpgrade = instrumentService.getByUpgradedInstrumentId(instrument.getId());
      if (preUpgrade != null) {
        instrumentDto.setPreUpgradeInstrumentId(preUpgrade.getId());
        instrumentDto.setPreUpgradeInstrumentName(preUpgrade.getName());
      }
    }

    model.put("instrument", instrument);
    model.put("instrumentDto", mapper.writeValueAsString(instrumentDto));

    List<InstrumentDto> otherInstruments = instrumentService.list().stream()
        .filter(other -> other.getId() != instrument.getId())
        .map(Dtos::asDto)
        .collect(Collectors.toList());
    model.put("otherInstruments", mapper.writeValueAsString(otherInstruments));

    ArrayNode instrumentTypes = mapper.createArrayNode();
    for (InstrumentType type : InstrumentType.values()) {
      ObjectNode dto = instrumentTypes.addObject();
      dto.put("label", type.getLabel());
      dto.put("value", type.name());
    }
    model.put("instrumentTypes", mapper.writeValueAsString(instrumentTypes));

    List<WorkstationDto> workstationDtos = workstationService.list().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList());
    model.put("workstations", mapper.writeValueAsString(workstationDtos));

    return new ModelAndView("/WEB-INF/pages/editInstrument.jsp", model);
  }

  public ModelAndView showPage(ServiceRecord record, Instrument instrument, ModelMap model)
      throws JsonProcessingException {
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
