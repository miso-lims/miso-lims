package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListPoolsController {

  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private JsonMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Pools";
  }

  @RequestMapping("/pools")
  public ModelAndView listPools(ModelMap model) throws IOException {
    return new TabbedListPoolsPage("pool", "platformType",
        TabbedListItemsPage.getPlatformTypes(instrumentModelService),
        PlatformType::getKey, PlatformType::name, mapper)
            .list(model);
  }

  public class TabbedListPoolsPage extends TabbedListItemsPage {

    public <T> TabbedListPoolsPage(String targetType, String property, Stream<T> tabItems, Function<T, String> getName,
        Function<T, Object> getValue, JsonMapper mapper) {
      super(targetType, property, tabItems, getName, getValue, mapper);
    }
  }

}
