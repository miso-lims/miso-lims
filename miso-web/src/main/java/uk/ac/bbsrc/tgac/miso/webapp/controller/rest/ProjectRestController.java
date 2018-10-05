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
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.dto.AttachmentDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDto;
import uk.ac.bbsrc.tgac.miso.dto.PoolDto;
import uk.ac.bbsrc.tgac.miso.dto.ProjectDto;
import uk.ac.bbsrc.tgac.miso.dto.RunDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleDto;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

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
  private ProjectService projectService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private SampleGroupService sampleGroupService;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setPoolService(PoolService poolService) {
    this.poolService = poolService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @GetMapping(value = "/alias/{projectAlias}", produces = "application/json")
  public @ResponseBody ProjectDto getProjectByAlias(@PathVariable String projectAlias) throws IOException {
    Project project = projectService.getProjectByAlias(projectAlias);
    if (project == null) {
      throw new RestException("No project found with alias: " + projectAlias, Status.NOT_FOUND);
    }
    return Dtos.asDto(project);
  }

  @GetMapping(value = "{projectId}", produces = "application/json")
  public @ResponseBody ProjectDto getProjectById(@PathVariable Long projectId) throws IOException {
    Project project = projectService.get(projectId);
    if (project == null) {
      throw new RestException("No project found with ID: " + projectId, Status.NOT_FOUND);
    }
    return Dtos.asDto(project);
  }

  @GetMapping(value = "{projectId}/samples", produces = "application/json")
  public @ResponseBody List<SampleDto> getProjectSamples(@PathVariable Long projectId) throws IOException {
    Collection<Sample> sp = sampleService.listByProjectId(projectId);
    return Dtos.asSampleDtos(sp, false);
  }

  @GetMapping(value = "{projectId}/samples/full", produces = "application/json")
  public @ResponseBody List<SampleDto> getProjectSamplesFull(@PathVariable Long projectId) throws IOException {
    Collection<Sample> sp = sampleService.listByProjectId(projectId);
    return Dtos.asSampleDtos(sp, true);
  }

  @GetMapping(value = "{projectId}/libraries", produces = "application/json")
  public @ResponseBody List<LibraryDto> getProjectLibraries(@PathVariable Long projectId) throws IOException {
    Collection<Library> lp = libraryService.listByProjectId(projectId);
    return lp.stream().map(lib -> Dtos.asDto(lib, false)).collect(Collectors.toList());
  }

  @GetMapping(value = "{projectId}/pools", produces = "application/json")
  public @ResponseBody List<PoolDto> getProjectPools(@PathVariable Long projectId) throws IOException {
    Collection<Pool> pp = poolService.listByProjectId(projectId);
    return pp.stream().map(pool -> Dtos.asDto(pool, true, false)).collect(Collectors.toList());
  }

  @GetMapping(value = "{projectId}/runs", produces = "application/json")
  public @ResponseBody List<RunDto> getProjectRuns(@PathVariable Long projectId) throws IOException {
    Collection<Run> rp = runService.listByProjectId(projectId);
    return Dtos.asRunDtos(rp);
  }

  @GetMapping(produces = "application/json")
  public @ResponseBody List<ProjectDto> listAllProjects() throws IOException {
    Collection<Project> lp = projectService.listAllProjects();
    return Dtos.asProjectDtos(lp);
  }

  @GetMapping(value = "{id}/groups", produces = { "application/json" })
  @ResponseBody
  public Collection<Integer> getProjectSampleGroups(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder,
      HttpServletResponse response) throws IOException {
    Set<Integer> groups = new HashSet<>();
    for (SampleGroupId sgi : sampleGroupService.getAllForProject(id)) {
      groups.add(sgi.getGroupId());
    }
    return groups;
  }

  @GetMapping(value = "/picker/search")
  @ResponseBody
  public List<ProjectDto> getProjectsBySearch(@RequestParam("query") String query) throws IOException {
    return projectService.listAllProjectsBySearch(query).stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @GetMapping(value = "/{projectId}/files")
  public @ResponseBody List<AttachmentDto> getAttachments(@PathVariable(name = "projectId", required = true) long projectId)
      throws IOException {
    Project project = projectService.get(projectId);
    if (project == null) {
      throw new RestException("Project not found", Status.NOT_FOUND);
    }
    return project.getAttachments().stream().map(Dtos::asDto).collect(Collectors.toList());
  }

}
