package uk.ac.bbsrc.tgac.miso.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IdentitySearchController {

  @RequestMapping(value = "/tools/identitysearch", method = RequestMethod.GET)
  public ModelAndView getTool(ModelMap model) {
    return new ModelAndView("/WEB-INF/pages/identitySearchTool.jsp", model);
  }
}
