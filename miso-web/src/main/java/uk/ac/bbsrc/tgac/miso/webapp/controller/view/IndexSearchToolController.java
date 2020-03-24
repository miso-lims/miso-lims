package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexSearchToolController {

  @ModelAttribute("title")
  public String title() {
    return "Index Search Tool";
  }

  @GetMapping("/tools/indexsearch")
  public ModelAndView getTool(ModelMap model) {
    return new ModelAndView("/WEB-INF/pages/indexSearchTool.jsp", model);
  }

}
