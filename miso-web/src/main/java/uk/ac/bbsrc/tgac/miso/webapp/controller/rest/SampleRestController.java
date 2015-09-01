/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

import com.eaglegenomics.simlims.core.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.LibraryRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.ProjectSampleRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.UserInfoMixin;
import uk.ac.bbsrc.tgac.miso.webapp.util.RestUtils;

import java.io.IOException;

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
public class SampleRestController {
  protected static final Logger log = LoggerFactory.getLogger(SampleRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{sampleId}", method = RequestMethod.GET)
  public @ResponseBody String getSampleById(@PathVariable Long sampleId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializationConfig().addMixInAnnotations(Project.class, ProjectSampleRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(Library.class, LibraryRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    try {
      Sample s = requestManager.getSampleById(sampleId);
      if (s != null) {
        return mapper.writeValueAsString(s);
      }
      return mapper.writeValueAsString(RestUtils.error("No such sample with that ID.", "sampleId", sampleId.toString()));
    }
    catch (IOException ioe) {
      return mapper.writeValueAsString(RestUtils.error("Cannot retrieve sample: " + ioe.getMessage(), "sampleId", sampleId.toString()));
    }
  }
}