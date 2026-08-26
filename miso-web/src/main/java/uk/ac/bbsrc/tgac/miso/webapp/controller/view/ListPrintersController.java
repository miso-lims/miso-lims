package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPageWithAuthorization;

@Controller
@SessionAttributes("printer")
public class ListPrintersController {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private JsonMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Printers";
  }

  @RequestMapping(value = "/printers", method = RequestMethod.GET)
  public ModelAndView view(ModelMap model) throws IOException {
    ListItemsPage listPage = new ListItemsPageWithAuthorization("printer", authorizationManager, mapper);
    return listPage.list(model);
  }
}
