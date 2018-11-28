package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.service.PlatformService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListCompletionsController {
  class OrderListPage extends TabbedListItemsPage {

    private final String slug;

    public OrderListPage(String slug) throws IOException {
      super("completion", "platform", getPlatformTypes(platformService), PlatformType::getKey,
          PlatformType::name);
      this.slug = slug;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("slug", slug);
    }

  }

  private static final String MODEL_ATTR_TITLE = "customTitle";

  @Autowired
  private PlatformService platformService;

  @RequestMapping("/poolorders/active")
  public ModelAndView listActive(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "Active Orders");
    return new OrderListPage("active").list(model);
  }

  @RequestMapping("/poolorders/all")
  public ModelAndView listAll(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "All Orders");
    return new OrderListPage("all").list(model);
  }

  @RequestMapping("/poolorders/pending")
  public ModelAndView listPools(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "Pending Orders");
    return new OrderListPage("pending").list(model);
  }

  @ModelAttribute("title")
  public String title() {
    return "Orders";
  }
}
