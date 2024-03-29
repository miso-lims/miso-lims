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

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.service.InstrumentModelService;
import uk.ac.bbsrc.tgac.miso.webapp.util.TabbedListItemsPage;

@Controller
public class ListRunsController {
  protected static final Logger log = LoggerFactory.getLogger(ListRunsController.class);

  @Autowired
  private InstrumentModelService instrumentModelService;
  @Autowired
  private ObjectMapper mapper;

  @RequestMapping("/runs")
  public ModelAndView listRuns(ModelMap model) throws Exception {
    return TabbedListItemsPage.createForPlatformType("run", instrumentModelService, mapper).list(model);
  }

  @ModelAttribute("title")
  public String title() {
    return "Runs";
  }

}
