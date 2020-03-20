/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
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

  private static class ListLocationsPage extends TabbedListItemsPage {

    private final AuthorizationManager authorizationManager;

    public ListLocationsPage(AuthorizationManager authorizationManager) {
      super("storage_location", "slug", LOCATIONS);
      this.authorizationManager = authorizationManager;
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.isAdminUser());
    }

  }

  @RequestMapping("/storagelocations")
  public ModelAndView listProjects(ModelMap model) throws Exception {
    return new ListLocationsPage(authorizationManager).list(key -> {
      switch (key) {
      case "Rooms":
        return storageLocationService.listRooms().stream().map(r -> Dtos.asDto(r, false, false));
      case "Freezers":
        return storageLocationService.listFreezers().stream().map(r -> Dtos.asDto(r, false, false));
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
