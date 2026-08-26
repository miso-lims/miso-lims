package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Arrays;
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

import com.eaglegenomics.simlims.core.User;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListKitDescriptorsController {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private JsonMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Kits";
  }

  @RequestMapping("/kitdescriptors")
  public ModelAndView listKitDescriptors(ModelMap model) throws IOException {
    return new TabbedListKitDescriptorsPage("kit", "kitType", Arrays.stream(KitType.values()), KitType::getKey,
        KitType::name, mapper).list(model);
  }

  public class TabbedListKitDescriptorsPage extends TabbedListItemsPage {

    public <T> TabbedListKitDescriptorsPage(String targetType, String property, Stream<T> tabItems,
        Function<T, String> getName,
        Function<T, Object> getValue, JsonMapper mapper) {
      super(targetType, property, tabItems, getName, getValue, mapper);
    }

    public <T> TabbedListKitDescriptorsPage(String targetType, String property, Stream<T> tabItems,
        Comparator<String> tabSorter,
        Function<T, String> getName, Function<T, Object> getValue, JsonMapper mapper) {
      super(targetType, property, tabItems, tabSorter, getName, getValue, mapper);
    }

    public TabbedListKitDescriptorsPage(String targetType, String property, SortedMap<String, String> tabs,
        JsonMapper mapper) {
      super(targetType, property, tabs, mapper);
    }

    @Override
    protected void writeConfiguration(JsonMapper mapper, ObjectNode config) throws IOException {
      User user = authorizationManager.getCurrentUser();
      config.put("isUserAdmin", user.isAdmin());
    }
  }
}
