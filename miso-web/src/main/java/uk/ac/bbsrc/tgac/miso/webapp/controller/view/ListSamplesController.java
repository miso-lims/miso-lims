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

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.util.ListItemsPage;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

/**
 * com.eaglegenomics.miso.web
 * <p/>
 * TODO Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
public class ListSamplesController {

  @Autowired
  private ObjectMapper mapper;
  @Autowired
  private SopService sopService;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private WorkstationService workstationService;

  @ModelAttribute("title")
  public String title() {
    return "Samples";
  }

  @RequestMapping("/samples")
  public ModelAndView listSamples(ModelMap model) throws Exception {
    return new ListItemsPage("sample", mapper) {
      @Override
      protected void writeConfiguration(ObjectMapper mapper, ObjectNode config) throws IOException {
        MisoWebUtils.addJsonArray(mapper, config, "sops", sopService.listByCategory(SopCategory.SAMPLE),
            Dtos::asDto);
        MisoWebUtils.addJsonArray(mapper, config, "instruments", instrumentService.list(), Dtos::asDto);
        MisoWebUtils.addJsonArray(mapper, config, "workstations", workstationService.list(), Dtos::asDto);
      }
    }.list(model);
  }
}