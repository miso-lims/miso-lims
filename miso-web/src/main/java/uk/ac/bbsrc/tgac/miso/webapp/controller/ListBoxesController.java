package uk.ac.bbsrc.tgac.miso.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListBoxesController {
  protected static final Logger log = LoggerFactory.getLogger(ListBoxesController.class);

  @Autowired
  private BoxService boxService;

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  @RequestMapping("/boxes")
  public ModelAndView listBoxes(ModelMap model) throws Exception {
    return TabbedListItemsPage.createWithJson("box", "boxUse", boxService.listUses().stream(), BoxUse::getAlias, BoxUse::getId).list(model);
  }
}
