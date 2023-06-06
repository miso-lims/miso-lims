package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;

@Controller
public class ListProjectsController {

  @Autowired
  private ObjectMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Projects";
  }

  @RequestMapping("/projects")
  public ModelAndView listProjects(ModelMap model) throws Exception {
    return new ListItemsPage("project", mapper).list(model);
  }
}
