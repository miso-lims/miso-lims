package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
@RequestMapping("/poolorders")
public class ListPoolOrdersController {

  @ModelAttribute("title")
  public String title() {
    return "Pool Orders";
  }

  @GetMapping
  public ModelAndView list(ModelMap model) throws IOException {
    return new TabbedListItemsPage("poolorder", "status", Arrays.stream(new String[] { "Outstanding", "Fulfilled", "Draft" }), null,
        Function.identity(),
        status -> status).list(model);
  }

}
