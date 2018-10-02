package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.dto.AttachmentCategoryDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.AttachmentCategoryService;

@Controller
@RequestMapping("/attachmentcategories")
public class AttachmentCategoryDefaultsController extends AbstractInstituteDefaultsController<AttachmentCategory, AttachmentCategoryDto> {

  @Autowired
  private AttachmentCategoryService service;

  @Override
  protected AttachmentCategoryDto asDto(AttachmentCategory model) {
    return Dtos.asDto(model);
  }

  @Override
  protected AttachmentCategory get(long id) throws IOException {
    return service.get(id);
  }

  @Override
  protected Collection<AttachmentCategory> getAll() throws IOException {
    return service.list();
  }

  @Override
  protected AttachmentCategoryDto getBlankModel() {
    return new AttachmentCategoryDto();
  }

  @Override
  protected Class<AttachmentCategoryDto> getDtoClass() {
    return AttachmentCategoryDto.class;
  }

  @Override
  protected String getName() {
    return "Attachment Category";
  }

  @Override
  protected String getType() {
    return "attachmentcategory";
  }

}
