package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.service.QcTypeService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.QcTypeDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/qctype")
public class QcTypeController extends AbstractInstituteDefaultsController<QcType, QcTypeDto> {

  @Autowired
  private QcTypeService qcTypeService;
  @Autowired
  private ObjectMapper mapper;

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
    return qcTypeService.list();
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

  @GetMapping("/new")
  public ModelAndView create(ModelMap model) throws IOException {
    model.put("title", "New QC Type");
    model.put(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    return setupForm(new QcType(), model);
  }

  @GetMapping("/{qcTypeId}")
  public ModelAndView edit(@PathVariable long qcTypeId, ModelMap model) throws IOException {
    QcType qcType = qcTypeService.get(qcTypeId);
    if (qcType == null) {
      throw new NotFoundException("No QC Type found for ID: " + qcTypeId);
    }
    model.put("title", "QC Type " + qcType.getId());
    model.put(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    return setupForm(qcType, model);
  }

  private ModelAndView setupForm(QcType qcType, ModelMap model) throws JsonProcessingException {
    QcTypeDto dto = Dtos.asDto(qcType);
    model.put("qcTypeDto", mapper.writeValueAsString(dto));
    return new ModelAndView("/WEB-INF/pages/editQcType.jsp", model);
  }

}
