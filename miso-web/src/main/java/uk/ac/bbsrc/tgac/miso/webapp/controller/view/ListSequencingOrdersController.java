package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
@RequestMapping("/sequencingorders")
public class ListSequencingOrdersController {

  class OrderListPage extends TabbedListItemsPage {

    private final String slug;

    public OrderListPage(String slug, ObjectMapper mapper) throws IOException {
      super("sequencingordercompletion", "platform", getPlatformTypes(instrumentModelService), PlatformType::getKey,
          PlatformType::name, mapper);
      this.slug = slug;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("slug", slug);
    }

  }

  private static final String MODEL_ATTR_TITLE = "customTitle";

  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private ObjectMapper mapper;

  @GetMapping("/outstanding")
  public ModelAndView listActive(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "Outstanding Sequencing Orders");
    return new OrderListPage("outstanding", mapper).list(model);
  }

  @GetMapping("/all")
  public ModelAndView listAll(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "All Sequencing Orders");
    return new OrderListPage("all", mapper).list(model);
  }

  @GetMapping("/in-progress")
  public ModelAndView listPools(ModelMap model) throws IOException {
    model.addAttribute(MODEL_ATTR_TITLE, "In-Progress Sequencing Orders");
    return new OrderListPage("in-progress", mapper).list(model);
  }

  @ModelAttribute("title")
  public String title() {
    return "Sequencing Orders";
  }
}
