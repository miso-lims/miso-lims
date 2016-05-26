package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;

@Controller
public class ListOutstandingOrdersController {
  @Autowired
  private PoolOrderCompletionService poolOrderCompletionService;

  @ModelAttribute("title")
  public String title() {
    return "List Outstanding Orders";
  }

  @RequestMapping("/poolorders/outstanding")
  public ModelAndView listPools() throws IOException {
    ModelAndView model = new ModelAndView("/pages/listPoolOrders.jsp");
    model.addObject("ordercompletions", poolOrderCompletionService.getOutstandingOrders());
    return model;
  }
}
