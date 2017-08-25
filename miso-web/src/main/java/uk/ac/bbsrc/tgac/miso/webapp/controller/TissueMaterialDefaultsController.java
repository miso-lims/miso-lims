package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueMaterialDto;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;

@Controller
@RequestMapping("/tissuematerial")
public class TissueMaterialDefaultsController extends AbstractInstituteDefaultsController<TissueMaterial, TissueMaterialDto> {
  @Autowired
  private TissueMaterialService service;

  @Override
  protected TissueMaterialDto asDto(TissueMaterial model) {
    return Dtos.asDto(model);
  }

  @Override
  protected TissueMaterial get(long id) throws IOException {
    return service.get(id);
  }

  @Override
  protected Collection<TissueMaterial> getAll() throws IOException {
    return service.getAll();
  }

  @Override
  protected TissueMaterialDto getBlankModel() {
    return new TissueMaterialDto();
  }

  @Override
  protected Class<TissueMaterialDto> getDtoClass() {
    return TissueMaterialDto.class;
  }

  @Override
  protected String getName() {
    return "Tissue Material";
  }

  @Override
  protected String getType() {
    return "tissuematerial";
  }

}
