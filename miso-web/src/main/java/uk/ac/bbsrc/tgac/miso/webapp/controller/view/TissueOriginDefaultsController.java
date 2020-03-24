package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.TissueOriginDto;

@Controller
@RequestMapping("/tissueorigin")
public class TissueOriginDefaultsController extends AbstractInstituteDefaultsController<TissueOrigin, TissueOriginDto> {

  @Autowired
  private TissueOriginService tissueOriginService;

  @Override
  protected TissueOriginDto asDto(TissueOrigin model) {
    return Dtos.asDto(model);
  }

  @Override
  protected TissueOrigin get(long id) throws IOException {
    return tissueOriginService.get(id);
  }

  @Override
  protected Collection<TissueOrigin> getAll() throws IOException {
    return tissueOriginService.list();
  }

  @Override
  protected TissueOriginDto getBlankModel() {
    return new TissueOriginDto();
  }

  @Override
  protected Class<TissueOriginDto> getDtoClass() {
    return TissueOriginDto.class;
  }

  @Override
  protected String getName() {
    return "Tissue Origins";
  }

  @Override
  protected String getType() {
    return "tissueorigin";
  }

}
