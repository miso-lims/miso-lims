package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ListPoolOrdersController {

  @ModelAttribute("title")
  public String title() {
    return "Orders";
  }

  @RequestMapping("/poolorders")
  public ModelAndView listPools() throws IOException {
    return new ModelAndView("/pages/listPoolOrders.jsp");
  }
}
