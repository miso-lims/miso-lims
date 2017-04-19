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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.ProjectDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;

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
public class ProjectRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(ProjectRestController.class);

  @Autowired
  private RequestManager requestManager;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private SampleGroupService sampleGroupService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @RequestMapping(value = "/alias/{projectAlias}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody ProjectDto getProjectByAlias(@PathVariable String projectAlias) throws IOException {
    Project project = requestManager.getProjectByAlias(projectAlias);
    if (project == null) {
      throw new RestException("No project found with alias: " + projectAlias, Status.NOT_FOUND);
    }
    return Dtos.asDto(project);
  }

  @RequestMapping(value = "{projectId}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody ProjectDto getProjectById(@PathVariable Long projectId) throws IOException {
    Project project = requestManager.getProjectById(projectId);
    if (project == null) {
      throw new RestException("No project found with ID: " + projectId, Status.NOT_FOUND);
    }
    return Dtos.asDto(project);
  }

  @RequestMapping(value = "{projectId}/libraries", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody List<LibraryDto> getProjectLibraries(@PathVariable Long projectId) throws IOException {
    Collection<Library> lp = libraryService.listByProjectId(projectId);
    return Dtos.asLibraryDtos(lp);
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody List<ProjectDto> listAllProjects() throws IOException {
    Collection<Project> lp = requestManager.listAllProjects();
    return Dtos.asProjectDtos(lp);
  }

  @RequestMapping(value = "{id}/groups", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Collection<Integer> getProjectSampleGroups(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    Set<Integer> groups = new HashSet<>();
    for (SampleGroupId sgi : sampleGroupService.getAllForProject(id)) {
      groups.add(sgi.getGroupId());
    }
    return groups;
  }

}
