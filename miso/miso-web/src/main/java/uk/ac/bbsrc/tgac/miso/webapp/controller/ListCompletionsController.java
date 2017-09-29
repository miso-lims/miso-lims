package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListCompletionsController {
  @ModelAttribute("title")
  public String title() {
    return "Orders";
  }

  @RequestMapping("/poolorders")
  public ModelAndView listPools(ModelMap model) throws IOException {
    return TabbedListItemsPage
        .<Boolean> createWithJson("completion", "activeOnly", Stream.of(true, false), x -> x ? "Active" : "All", x -> x)
        .list(model);
  }
}
