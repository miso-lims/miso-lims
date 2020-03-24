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
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

  private final TabbedListItemsPage listPage = new TabbedListItemsPage("instrument", "instrumentType",
      Stream.of(InstrumentType.values()), sortByOrdinal, InstrumentType::getLabel, InstrumentType::name) {

    @Override
    protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
      config.put("isAdmin", authorizationManager.getCurrentUser().isAdmin());
    }

  };

  @ModelAttribute("title")
  public String title() {
    return "Instruments";
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView listInstruments(ModelMap model) throws IOException {
    return listPage.list(model);
  }

}
