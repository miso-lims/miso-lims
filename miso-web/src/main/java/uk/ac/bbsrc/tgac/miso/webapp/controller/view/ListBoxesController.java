package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
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

  private static final String ALL_TAB = "All";

  private static Comparator<String> TAB_SORTER = (o1, o2) -> {
    if (ALL_TAB.equals(o1)) {
      return -1;
    } else if (ALL_TAB.equals(o2)) {
      return 1;
    } else {
      return o1.compareTo(o2);
    }
  };

  @RequestMapping("/boxes")
  public ModelAndView listBoxes(ModelMap model) throws Exception {
    List<BoxUse> uses = boxUseService.list();
    BoxUse all = new BoxUse();
    all.setAlias(ALL_TAB);
    uses.add(0, all);
    return new TabbedListBoxPage("box", "boxUse", uses.stream(), TAB_SORTER, BoxUse::getAlias, BoxUse::getId).list(model);
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
