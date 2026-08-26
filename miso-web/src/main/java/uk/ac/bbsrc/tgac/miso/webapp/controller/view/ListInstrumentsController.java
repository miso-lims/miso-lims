package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
@RequestMapping("/instruments")
public class ListInstrumentsController {

  private static final Comparator<String> sortByOrdinal = (a, b) -> Integer
      .compare(InstrumentType.get(a).ordinal(), InstrumentType.get(b).ordinal());

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private JsonMapper mapper;

  @ModelAttribute("title")
  public String title() {
    return "Instruments";
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listInstruments(ModelMap model) throws IOException {
    TabbedListItemsPage listPage = new TabbedListItemsPage("instrument", "instrumentType",
        Stream.of(InstrumentType.values()), sortByOrdinal, InstrumentType::getLabel, InstrumentType::name, mapper) {

      @Override
      protected void writeConfiguration(JsonMapper mapper, ObjectNode config) throws IOException {
        config.put("isAdmin", authorizationManager.getCurrentUser().isAdmin());
      }

    };
    return listPage.list(model);
  }

}
