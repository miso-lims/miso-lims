package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ArrayRunService;
import uk.ac.bbsrc.tgac.miso.service.ArrayService;

@Controller
@RequestMapping("/array")
public class EditArrayController {

  private static final String JSP = "/WEB-INF/pages/editArray.jsp";

  private static final String MODEL_ATTR_PAGEMODE = "pageMode";
  private static final String MODEL_ATTR_MODELS = "arrayModels";
  private static final String MODEL_ATTR_JSON = "arrayJson";
  private static final String MODEL_ATTR_ARRAYRUNS = "arrayRuns";

  @Autowired
  private ArrayService arrayService;

  @Autowired
  private ArrayRunService arrayRunService;

  @ModelAttribute("maxLengths")
  public Map<String, Integer> getColumnSizes() throws IOException {
    return arrayService.getColumnSizes();
  }

  @RequestMapping("/new")
  public ModelAndView newArray(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_PAGEMODE, "create");
    model.addAttribute(MODEL_ATTR_MODELS, arrayService.listArrayModels());
    return new ModelAndView(JSP, model);
  }

  @RequestMapping("/{arrayId}")
  public ModelAndView setupForm(@PathVariable(name = "arrayId", required = true) long arrayId, ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_PAGEMODE, "edit");
    Array array = arrayService.get(arrayId);
    if (array == null) {
      throw new NotFoundException("Array not found");
    }
    model.addAttribute(MODEL_ATTR_ARRAYRUNS, arrayRunService.listByArrayId(arrayId).stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList()));

    ObjectMapper mapper = new ObjectMapper();
    model.addAttribute(MODEL_ATTR_JSON, mapper.writer().writeValueAsString(Dtos.asDto(array)));
    return new ModelAndView(JSP, model);
  }

}
