package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListWorksetsController {

  private static final List<String> CREATORS = Arrays.asList("Mine", "All");

  @ModelAttribute("title")
  public String title() {
    return "Worksets";
  }

  @RequestMapping("/worksets")
  public ModelAndView listWorksets(ModelMap model) throws IOException {
    return new TabbedListItemsPage("workset", "creator", CREATORS.stream(), (t1, t2) -> 1, Function.identity(), String::toLowerCase)
        .list(model);
  }

}
