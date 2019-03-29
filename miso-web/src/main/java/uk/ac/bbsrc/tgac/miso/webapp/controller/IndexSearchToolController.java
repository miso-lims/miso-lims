package uk.ac.bbsrc.tgac.miso.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexSearchToolController {

  @GetMapping("/tools/indexsearch")
  public ModelAndView getTool(ModelMap model) {
    return new ModelAndView("/WEB-INF/pages/indexSearchTool.jsp", model);
  }

}
