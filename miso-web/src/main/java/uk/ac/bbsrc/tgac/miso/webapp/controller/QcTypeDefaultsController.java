package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcTypeDto;

@Controller
@RequestMapping("/qctype")
public class QcTypeDefaultsController extends AbstractInstituteDefaultsController<QcType, QcTypeDto> {
  @Autowired
  private QcTypeService qcTypeService;

  @Override
  protected QcTypeDto asDto(QcType model) {
    return Dtos.asDto(model);
  }

  @Override
  protected QcType get(long id) throws IOException {
    return qcTypeService.get(id);
  }

  @Override
  protected Collection<QcType> getAll() throws IOException {
    return qcTypeService.getAll();
  }

  @Override
  protected QcTypeDto getBlankModel() {
    return new QcTypeDto();
  }

  @Override
  protected Class<QcTypeDto> getDtoClass() {
    return QcTypeDto.class;
  }

  @Override
  protected String getName() {
    return "QC Type";
  }

  @Override
  protected String getType() {
    return "qctype";
  }

}
