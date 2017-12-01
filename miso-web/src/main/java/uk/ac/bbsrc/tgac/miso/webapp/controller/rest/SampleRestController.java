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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

/**
 * A controller to handle all REST requests for Samples
 * 
 * @author Rob Davey
 * @date 19-Aug-2015
 * @since 0.2.1-SNAPSHOT
 */
@Controller
@RequestMapping("/rest/sample")
@SessionAttributes("sample")
public class SampleRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(SampleRestController.class);

  @Autowired
  private SampleService sampleService;

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  @RequestMapping(value = "{sampleId}", method = RequestMethod.GET, produces="application/json")
  public @ResponseBody String getSampleById(@PathVariable Long sampleId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Sample s = sampleService.get(sampleId);
    if (s == null) {
      throw new RestException("No sample found with ID: " + sampleId, Status.NOT_FOUND);
    }
    return mapper.writeValueAsString(s);
  }
  
}
