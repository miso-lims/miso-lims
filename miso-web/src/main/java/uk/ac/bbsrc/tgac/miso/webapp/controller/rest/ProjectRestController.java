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
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryQcException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedSampleQcException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.LibraryRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.SampleProjectAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.UserInfoMixin;
import uk.ac.bbsrc.tgac.miso.webapp.util.RestUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * A controller to handle all REST requests for Projects
 *
 * @author Rob Davey
 * @date 01-Sep-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/project")
@SessionAttributes("project")
public class ProjectRestController {
  protected static final Logger log = LoggerFactory.getLogger(ProjectRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "/alias/{projectAlias}", method = RequestMethod.GET)
  public @ResponseBody String getProjectByAlias(@PathVariable String projectAlias) throws IOException {
    try {
      Project project = requestManager.getProjectByAlias(projectAlias);
      if (project != null) {
        return getProjectById(project.getId());
      }
      return RestUtils.error("No such project with that alias.", "projectAlias", projectAlias).toString();
    }
    catch (IOException ioe) {
      return RestUtils.error("Cannot retrieve project: " + ioe.getMessage(), "projectAlias", projectAlias).toString();
    }
  }

  @RequestMapping(value = "{projectId}", method = RequestMethod.GET)
  public @ResponseBody String getProjectById(@PathVariable Long projectId) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      Project project = requestManager.getProjectById(projectId);
      if (project != null) {
        for (Sample s : project.getSamples()) {
          if (s.getLibraries().isEmpty()) {
            for (Library l : requestManager.listAllLibrariesBySampleId(s.getId())) {
              try {
                s.addLibrary(l);
              }
              catch (MalformedLibraryException e) {
                e.printStackTrace();
              }
            }
          }

          if (s.getSampleQCs().isEmpty()) {
            for (SampleQC qc : requestManager.listAllSampleQCsBySampleId(s.getId())) {
              try {
                s.addQc(qc);
              }
              catch (MalformedSampleQcException e) {
                e.printStackTrace();
              }
            }
          }
        }
        mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleProjectAvoidanceMixin.class);
        mapper.getSerializationConfig().addMixInAnnotations(Library.class, LibraryRecursionAvoidanceMixin.class);
        mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
        return mapper.writeValueAsString(project);
      }
      return mapper.writeValueAsString(RestUtils.error("No such project with that ID.", "projectId", projectId.toString()));
    }
    catch (IOException ioe) {
      return mapper.writeValueAsString(RestUtils.error("Cannot retrieve project: " + ioe.getMessage(), "projectId", projectId.toString()));
    }
  }

  @RequestMapping(value = "{projectId}/libraries", method = RequestMethod.GET)
  public @ResponseBody String getProjectLibraries(@PathVariable Long projectId) throws IOException {
    Collection<Library> lp = requestManager.listAllLibrariesByProjectId(projectId);
    for (Library l : lp) {
      for (LibraryDilution dil : requestManager.listAllLibraryDilutionsByLibraryId(l.getId())) {
        try {
          l.addDilution(dil);
        }
        catch (MalformedDilutionException e) {
          e.printStackTrace();
        }
      }

      for (LibraryQC qc : requestManager.listAllLibraryQCsByLibraryId(l.getId())) {
        try {
          l.addQc(qc);
        }
        catch (MalformedLibraryQcException e) {
          e.printStackTrace();
        }
      }
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleProjectAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(Library.class, LibraryRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    return mapper.writeValueAsString(lp);
  }

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody String listAllProjects() throws IOException {
    Collection<Project> lp = requestManager.listAllProjects();
    for (Project p : lp) {
      p.setSamples(requestManager.listAllSamplesByProjectId(p.getProjectId()));
      p.setStudies(requestManager.listAllStudiesByProjectId(p.getProjectId()));
    }

    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleProjectAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(Library.class, LibraryRecursionAvoidanceMixin.class);
    mapper.getSerializationConfig().addMixInAnnotations(User.class, UserInfoMixin.class);
    return mapper.writeValueAsString(lp);
  }
}
