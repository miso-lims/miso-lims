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
import uk.ac.bbsrc.tgac.miso.core.util.jackson.ProjectSampleRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.SampleRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.UserInfoMixin;
import uk.ac.bbsrc.tgac.miso.webapp.util.RestUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * A controller to handle all REST requests for Libraries
 *
 * @author Rob Davey
 * @date 16-Aug-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/library")
@SessionAttributes("library")
public class LibraryRestController {
  protected static final Logger log = LoggerFactory.getLogger(LibraryRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{libraryId}", method = RequestMethod.GET)
  public @ResponseBody String getLibraryById(@PathVariable Long libraryId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      Library l = requestManager.getLibraryById(libraryId);
      if (l != null) {
        mapper.getSerializationConfig().addMixInAnnotations(Project.class, ProjectSampleRecursionAvoidanceMixin.class);
        mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleRecursionAvoidanceMixin.class);
        mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
        return mapper.writeValueAsString(l);
      }
      return mapper.writeValueAsString(RestUtils.error("No such library with that ID.", "libraryId", libraryId.toString()));
    }
    catch (IOException ioe) {
      return mapper.writeValueAsString(RestUtils.error("Cannot retrieve library: " + ioe.getMessage(), "libraryId", libraryId.toString()));
    }
  }

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody String listAllLibraries() throws IOException {
    Collection<Library> libraries = requestManager.listAllLibraries();
    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializationConfig().addMixInAnnotations(Project.class, ProjectSampleRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    return mapper.writeValueAsString(libraries);
  }
}
