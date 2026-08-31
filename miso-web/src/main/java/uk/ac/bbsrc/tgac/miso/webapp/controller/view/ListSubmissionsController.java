package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SubmissionService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;

@Controller
public class ListSubmissionsController {

  @Autowired
  private SubmissionService submissionService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private JsonMapper mapper;

  private static class ListSubmissionsPage extends ListItemsPage {

    private final AuthorizationManager authorizationManager;

    public ListSubmissionsPage(AuthorizationManager authorizationManager, JsonMapper mapper) {
      super("submission", mapper);
      this.authorizationManager = authorizationManager;
    }

    @Override
    protected void writeConfiguration(JsonMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.isAdminUser());
    }
  }

  @RequestMapping("/submissions")
  public ModelAndView listSubmissions(ModelMap model) throws Exception {
    return new ListSubmissionsPage(authorizationManager, mapper)
        .list(model, submissionService.list().stream().map(Dtos::asDto));
  }

  @ModelAttribute("title")
  public String title() {
    return "Submissions";
  }

}
