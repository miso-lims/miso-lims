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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListKitDescriptorsController {
  @Autowired
  private AuthorizationManager authorizationManager;

  @ModelAttribute("title")
  public String title() {
    return "Kits";
  }

  @RequestMapping("/kitdescriptors")
  public ModelAndView listKitDescriptors(ModelMap model) throws IOException {
    return new TabbedListKitDescriptorsPage("kit", "kitType", Arrays.stream(KitType.values()), KitType::getKey, KitType::name).list(model);
  }

  public class TabbedListKitDescriptorsPage extends TabbedListItemsPage {

    public <T> TabbedListKitDescriptorsPage(String targetType, String property, Stream<T> tabItems, Function<T, String> getName,
        Function<T, Object> getValue) {
      super(targetType, property, tabItems, getName, getValue);
    }

    public <T> TabbedListKitDescriptorsPage(String targetType, String property, Stream<T> tabItems, Comparator<String> tabSorter,
        Function<T, String> getName, Function<T, Object> getValue) {
      super(targetType, property, tabItems, tabSorter, getName, getValue);
    }

    public TabbedListKitDescriptorsPage(String targetType, String property, SortedMap<String, String> tabs) {
      super(targetType, property, tabs);
    }

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      User user = authorizationManager.getCurrentUser();
      config.put("isUserAdmin", user.isAdmin());
    }
  }
}
