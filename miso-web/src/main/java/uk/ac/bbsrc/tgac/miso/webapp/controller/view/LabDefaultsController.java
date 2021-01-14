package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;

@Controller
@RequestMapping("/lab")
public class LabDefaultsController extends AbstractInstituteDefaultsController<Lab, LabDto> {

  @Autowired
  private LabService service;

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
    return service.list();
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

}
