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
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

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

  @Autowired
  private SopService sopService;

  @ModelAttribute("title")
  public String title() {
    return "Libraries";
  }

  @RequestMapping("/libraries")
  public ModelAndView listLibraries(ModelMap model) throws Exception {
    return new ListItemsPage("library", mapper) {
      @Override
      protected void writeConfiguration(JsonMapper mapper, ObjectNode config) throws IOException {
        MisoWebUtils.addJsonArray(mapper, config, "sops", sopService.listByCategory(SopCategory.LIBRARY), Dtos::asDto);
      }
    }.list(model);
  }
}
