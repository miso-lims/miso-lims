package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.service.BoxUseService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;


@Controller
public class ListBoxesController {

  @Autowired
  private BoxUseService boxUseService;

  @ModelAttribute("title")
  public String title() {
    return "Boxes";
  }

  @RequestMapping("/boxes")
  public ModelAndView listBoxes(ModelMap model) throws Exception {
    return new TabbedListBoxPage("box", "boxUse", boxUseService.list().stream(), BoxUse::getAlias, BoxUse::getId).list(model);
  }

  public class TabbedListBoxPage extends TabbedListItemsPage {

    public <T> TabbedListBoxPage(String targetType, String property, Stream<T> tabItems, Function<T, String> getName,
        Function<T, Object> getValue) {
      super(targetType, property, tabItems, getName, getValue);
    }

    public <T> TabbedListBoxPage(String targetType, String property, Stream<T> tabItems, Comparator<String> tabSorter,
        Function<T, String> getName, Function<T, Object> getValue) {
      super(targetType, property, tabItems, tabSorter, getName, getValue);
    }

    public TabbedListBoxPage(String targetType, String property, SortedMap<String, String> tabs) {
      super(targetType, property, tabs);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("showFreezerLocation", false);
      config.put("showStorageLocation", true);
    }
  }
}
