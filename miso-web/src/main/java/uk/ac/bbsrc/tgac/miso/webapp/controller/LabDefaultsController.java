package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;
import uk.ac.bbsrc.tgac.miso.service.InstituteService;
import uk.ac.bbsrc.tgac.miso.service.LabService;

@Controller
@RequestMapping("/lab")
public class LabDefaultsController extends AbstractInstituteDefaultsController<Lab, LabDto> {

  @Autowired
  private LabService service;

  @Autowired
  private InstituteService instituteService;

  @Override
  protected LabDto asDto(Lab model) {
    return Dtos.asDto(model);
  }

  @Override
  protected Lab get(long id) throws IOException {
    return service.get(id);
  }

  @Override
  protected Collection<Lab> getAll() throws IOException {
    return service.getAll();
  }

  @Override
  protected LabDto getBlankModel() {
    return new LabDto();
  }

  @Override
  protected Class<LabDto> getDtoClass() {
    return LabDto.class;
  }

  @Override
  protected String getName() {
    return "Labs";
  }

  @Override
  protected String getType() {
    return "lab";
  }

  @Override
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
    ArrayNode node = config.putArray("institutes");
    instituteService.getAll().stream().map(Dtos::asDto).forEach(node::addPOJO);
  }

}
