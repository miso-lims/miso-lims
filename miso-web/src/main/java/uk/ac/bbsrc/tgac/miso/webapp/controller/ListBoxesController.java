package uk.ac.bbsrc.tgac.miso.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListBoxesController {

  @Autowired
  private BoxService boxService;

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  @ModelAttribute("title")
  public String title() {
    return "Boxes";
  }
  @RequestMapping("/boxes")
  public ModelAndView listBoxes(ModelMap model) throws Exception {
    return TabbedListItemsPage.createWithJson("box", "boxUse", boxService.listUses().stream(), BoxUse::getAlias, BoxUse::getId).list(model);
  }
}
