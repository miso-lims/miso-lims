package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListIndicesController {

  @Autowired
  private RequestManager requestManager;

  @ModelAttribute("title")
  public String title() {
    return "Indices";
  }

  @ModelAttribute("note")
  public String note() {
    return "<p>Archived indices are not shown.</p>";
  }

  @RequestMapping("/indices")
  public ModelAndView listIndices(ModelMap model) throws IOException {
    return TabbedListItemsPage.createForPlatformType("index", requestManager).list(model);
  }
}
