package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.dto.StorageLocationDto;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListStorageLocationsController {

  private static final SortedMap<String, String> LOCATIONS = new TreeMap<>();

  static {
    LOCATIONS.put("Rooms", "'rooms'");
    LOCATIONS.put("Freezers", "'freezers'");
  }

  @Autowired
  private StorageLocationService storageLocationService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private JsonMapper mapper;

  private static class ListLocationsPage extends TabbedListItemsPage {

    private final AuthorizationManager authorizationManager;

    public ListLocationsPage(AuthorizationManager authorizationManager, JsonMapper mapper) {
      super("storage_location", "slug", LOCATIONS, mapper);
      this.authorizationManager = authorizationManager;
    }

    @Override
    protected void writeConfiguration(JsonMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.isAdminUser());
    }

  }

  @RequestMapping("/storagelocations")
  public ModelAndView listProjects(ModelMap model) throws Exception {
    return new ListLocationsPage(authorizationManager, mapper).list(key -> {
      switch (key) {
        case "Rooms":
          return storageLocationService.listRooms().stream().map(r -> StorageLocationDto.from(r, false, false));
        case "Freezers":
          return storageLocationService.listFreezers().stream().map(r -> StorageLocationDto.from(r, false, false));
        default:
          throw new IllegalArgumentException();
      }
    }, model);
  }

  @ModelAttribute("title")
  public String title() {
    return "Storage Locations";
  }
}
