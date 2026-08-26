package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListRunsController {
  protected static final Logger log = LoggerFactory.getLogger(ListRunsController.class);

  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private JsonMapper mapper;

  @RequestMapping("/runs")
  public ModelAndView listRuns(ModelMap model) throws Exception {
    return TabbedListItemsPage.createForPlatformType("run", instrumentModelService, mapper).list(model);
  }

  @ModelAttribute("title")
  public String title() {
    return "Runs";
  }

}
