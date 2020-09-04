package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/workset")
public class EditWorksetController {

  private static final String JSP = "/WEB-INF/pages/editWorkset.jsp";

  private static final String MODEL_ATTR_TITLE = "title";
  private static final String MODEL_ATTR_PAGEMODE = "pageMode";
  private static final String MODEL_ATTR_ID = "worksetId";
  private static final String MODEL_ATTR_WORKSET = "workset";
  private static final String MODEL_ATTR_JSON = "worksetJson";

  @Autowired
  private WorksetService worksetService;
  
  @RequestMapping("/new")
  public ModelAndView newArray(ModelMap model) {
    model.addAttribute(MODEL_ATTR_TITLE, "New Workset");
    model.addAttribute(MODEL_ATTR_PAGEMODE, "create");
    return new ModelAndView(JSP, model);
  }

  @RequestMapping("/{worksetId}")
  public ModelAndView setupForm(@PathVariable(name = "worksetId", required = true) long worksetId, ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "Workset " + worksetId);
    model.addAttribute(MODEL_ATTR_PAGEMODE, "edit");
    Workset workset = worksetService.get(worksetId);
    if (workset == null) {
      throw new NotFoundException("Workset not found");
    }
    model.addAttribute(MODEL_ATTR_ID, worksetId);
    model.addAttribute(MODEL_ATTR_WORKSET, workset);
    ObjectMapper mapper = new ObjectMapper();
    model.addAttribute(MODEL_ATTR_JSON, mapper.writer().writeValueAsString(Dtos.asDto(workset)));
    return new ModelAndView(JSP, model);
  }

}
