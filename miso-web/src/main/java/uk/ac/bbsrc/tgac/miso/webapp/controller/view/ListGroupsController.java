package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.GroupService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPageWithAuthorization;

@Controller
public class ListGroupsController {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private GroupService groupService;
  @Autowired
  private JsonMapper mapper;

  @RequestMapping("/admin/groups")
  public ModelAndView adminListGroups(ModelMap model) throws IOException {
    ListItemsPage groupsPage = new ListItemsPageWithAuthorization("group", authorizationManager, mapper);
    return groupsPage.list(model, groupService.list().stream().map(Dtos::asDto));
  }

  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @ModelAttribute("title")
  public String title() {
    return "Groups";
  }
}
