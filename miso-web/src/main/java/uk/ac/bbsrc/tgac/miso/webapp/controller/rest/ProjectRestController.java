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
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.dto.AttachmentDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ProjectDto;

/**
 * A controller to handle all REST requests for Projects
 * 
 * @author Rob Davey
 * @date 01-Sep-2011
 * @since 0.1.0
 */
@Controller
@RequestMapping("/rest/projects")
public class ProjectRestController extends RestController {

  @Autowired
  private ProjectService projectService;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping(value = "/{projectId}", produces = "application/json")
  public @ResponseBody ProjectDto getProjectById(@PathVariable long projectId) throws IOException {
    return RestUtils.getObject("Project", projectId, projectService, Dtos::asDto);
  }

  @GetMapping(value = "/search")
  @ResponseBody
  public List<ProjectDto> getProjectsBySearch(@RequestParam("q") String query) throws IOException {
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

  @PostMapping
  public @ResponseBody ProjectDto create(@RequestBody ProjectDto dto) throws IOException {
    return RestUtils.createObject("Project", dto, Dtos::to, projectService, Dtos::asDto);
  }

  @PutMapping("/{projectId}")
  public @ResponseBody ProjectDto update(@PathVariable long projectId, @RequestBody ProjectDto dto) throws IOException {
    return RestUtils.updateObject("Project", projectId, dto, Dtos::to, projectService, Dtos::asDto);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Project", ids, projectService);
  }

}
