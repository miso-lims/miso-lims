package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayModelService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.NotFoundException;
import uk.ac.bbsrc.tgac.miso.webapp.util.PageMode;

@Controller
@RequestMapping("/array")
public class EditArrayController {

  private static final String JSP = "/WEB-INF/pages/editArray.jsp";

  private static final String MODEL_ATTR_MODELS = "arrayModels";
  private static final String MODEL_ATTR_JSON = "arrayJson";
  private static final String MODEL_ATTR_ARRAYRUNS = "arrayRuns";

  @Autowired
  private ArrayService arrayService;
  @Autowired
  private ArrayModelService arrayModelService;
  @Autowired
  private ArrayRunService arrayRunService;
  @Autowired
  private ObjectMapper mapper;

  @RequestMapping("/new")
  public ModelAndView newArray(ModelMap model) throws IOException {
    model.addAttribute(PageMode.PROPERTY, PageMode.CREATE.getLabel());
    List<ArrayModel> models = arrayModelService.list();
    model.addAttribute(MODEL_ATTR_MODELS,
        mapper.writeValueAsString(models.stream().map(Dtos::asDto).collect(Collectors.toList())));
    return new ModelAndView(JSP, model);
  }

  @RequestMapping("/{arrayId}")
  public ModelAndView setupForm(@PathVariable(name = "arrayId", required = true) long arrayId, ModelMap model)
      throws IOException {
    model.addAttribute(PageMode.PROPERTY, PageMode.EDIT.getLabel());
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new NotFoundException("Array not found");
    }
    model.addAttribute(MODEL_ATTR_ARRAYRUNS, arrayRunService.listByArrayId(arrayId).stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));

    model.addAttribute(MODEL_ATTR_JSON, mapper.writer().writeValueAsString(Dtos.asDto(array)));
    return new ModelAndView(JSP, model);
  }

}
