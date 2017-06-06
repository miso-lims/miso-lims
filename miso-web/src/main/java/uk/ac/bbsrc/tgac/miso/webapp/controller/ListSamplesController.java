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
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

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
  protected static final Logger log = LoggerFactory.getLogger(ListSamplesController.class);

  @Autowired
  private SampleService sampleService;

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;
  
  @ModelAttribute("detailedSample")
  public Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  @ModelAttribute("title")
  public String title() {
    return "Samples";
  }

  @RequestMapping(value = "/samples/rest/", method = RequestMethod.GET)
  public @ResponseBody Collection<Sample> jsonRest() throws IOException {
    return sampleService.getAll();
  }

  @RequestMapping("/samples")
  public ModelAndView listSamples() throws Exception {
    return new ModelAndView("/pages/listSamples.jsp");
  }
}