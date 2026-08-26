package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;

@Controller
public class ListDeletionsController {

  @Autowired
  private JsonMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Deletions";
  }

  @RequestMapping("/deletions")
  public ModelAndView listDeletions(ModelMap model) throws IOException {
    return new ListItemsPage("deletion", mapper).list(model);
  }

}
