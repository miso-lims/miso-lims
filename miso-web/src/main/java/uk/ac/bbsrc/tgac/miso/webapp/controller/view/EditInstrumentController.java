package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstrumentDto;
import uk.ac.bbsrc.tgac.miso.dto.WorkstationDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;

@Controller
@RequestMapping("/instrument")
public class EditInstrumentController {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private InstrumentService instrumentService;
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
}
