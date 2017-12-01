package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.InstituteDto;
import uk.ac.bbsrc.tgac.miso.service.InstituteService;

@Controller
@RequestMapping("/institute")
public class InstituteDefaultsController extends AbstractInstituteDefaultsController<Institute, InstituteDto> {

  @Autowired
  private InstituteService service;

  @Override
  protected InstituteDto asDto(Institute model) {
    return Dtos.asDto(model);
  }

  @Override
  protected Institute get(long id) throws IOException {
    return service.get(id);
  }

  @Override
  protected Collection<Institute> getAll() throws IOException {
    return service.getAll();
  }

  @Override
  protected InstituteDto getBlankModel() {
    return new InstituteDto();
  }

  @Override
  protected Class<InstituteDto> getDtoClass() {
    return InstituteDto.class;
  }

  @Override
  protected String getName() {
    return "Institutes";
  }

  @Override
  protected String getType() {
    return "institute";
  }

}
