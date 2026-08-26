package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;

/**
 * Controller for listing libraries
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
public class ListLibrariesController {

  @Autowired
  private JsonMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Libraries";
  }

  @RequestMapping("/libraries")
  public ModelAndView listLibraries(ModelMap model) throws Exception {
    return new ListItemsPage("library", mapper).list(model);
  }
}
