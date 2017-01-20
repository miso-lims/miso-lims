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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.service.printing.Backend;
import uk.ac.bbsrc.tgac.miso.core.service.printing.Driver;
import uk.ac.bbsrc.tgac.miso.service.PrinterService;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@Controller
@SessionAttributes("printer")
public class PrinterController {
  protected static final Logger log = LoggerFactory.getLogger(PrinterController.class);

  @Autowired
  private PrinterService printerService;

  public void setPrinterService(PrinterService printerService) {
    this.printerService = printerService;
  }

  @RequestMapping(value = "/printers", method = RequestMethod.GET)
  public ModelAndView view(ModelMap model) throws IOException {
    model.put("printers", printerService.getAll());
    ObjectMapper mapper = new ObjectMapper();
    ArrayNode backends = mapper.createArrayNode();
    for (Backend backend : Backend.values()) {
      ObjectNode node = mapper.createObjectNode();
      node.put("name", backend.name());
      node.put("id", backend.ordinal());
      ArrayNode configurationKeys = mapper.createArrayNode();
      for (String key : backend.getConfigurationKeys()) {
        configurationKeys.add(key);
      }
      node.put("configurationKeys", configurationKeys);
      backends.add(node);
    }
    model.put("backendsJSON", mapper.writeValueAsString(backends));
    model.put("backends", Backend.values());
    model.put("drivers", Driver.values());
    model.put("title", "Configure Printers");
    return new ModelAndView("/pages/viewPrinters.jsp", model);
  }
}
