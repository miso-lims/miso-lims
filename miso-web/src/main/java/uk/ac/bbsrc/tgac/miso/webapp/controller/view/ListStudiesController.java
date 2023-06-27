package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPageWithAuthorization;

@Controller
public class ListStudiesController {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ObjectMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Studies";
  }

  @RequestMapping("/studies")
  public ModelAndView listStudies(ModelMap model) throws Exception {
    return new ListItemsPageWithAuthorization("study", authorizationManager, mapper).list(model);
  }
}
