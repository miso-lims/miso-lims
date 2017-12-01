package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SamplePurposeDto;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;

@Controller
@RequestMapping("/samplepurpose")
public class SamplePurposeDefaultsController extends AbstractInstituteDefaultsController<SamplePurpose, SamplePurposeDto> {

  @Autowired
  private SamplePurposeService service;
  @Override
  protected SamplePurposeDto asDto(SamplePurpose model) {
    return Dtos.asDto(model);
  }

  @Override
  protected SamplePurpose get(long id) throws IOException {
    return service.get(id);
  }

  @Override
  protected Collection<SamplePurpose> getAll() throws IOException {
    return service.getAll();
  }

  @Override
  protected SamplePurposeDto getBlankModel() {
    return new SamplePurposeDto();
  }

  @Override
  protected Class<SamplePurposeDto> getDtoClass() {
    return SamplePurposeDto.class;
  }

  @Override
  protected String getName() {
    return "Sample Purpose";
  }

  @Override
  protected String getType() {
    return "samplepurpose";
  }

}
