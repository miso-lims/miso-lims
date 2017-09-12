package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SubprojectDto;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;

@Controller
@RequestMapping("/subproject")
public class SubprojectDefaultsController extends AbstractInstituteDefaultsController<Subproject, SubprojectDto> {

  @Autowired
  private RequestManager requestManager;
  @Autowired
  private SubprojectService service;

  @Override
  protected SubprojectDto asDto(Subproject model) {
    return Dtos.asDto(model);
  }

  @Override
  protected Subproject get(long id) throws IOException {
    return service.get(id);
  }

  @Override
  protected Collection<Subproject> getAll() throws IOException {
    return service.getAll();
  }

  @Override
  protected SubprojectDto getBlankModel() {
    SubprojectDto dto = new SubprojectDto();
    dto.setPriority(false);
    return dto;
  }

  @Override
  protected Class<SubprojectDto> getDtoClass() {
    return SubprojectDto.class;
  }

  @Override
  protected String getName() {
    return "Subproject";
  }

  @Override
  protected String getType() {
    return "subproject";
  }

  @Override
  protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
    ArrayNode projects = config.putArray("projects");
    requestManager.listAllProjects().stream().map(Dtos::asDto).forEach(projects::addPOJO);
  }

}
