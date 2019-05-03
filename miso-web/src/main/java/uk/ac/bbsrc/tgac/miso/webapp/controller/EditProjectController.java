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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import uk.ac.bbsrc.tgac.miso.core.data.Issue;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.webapp.context.ExternalUriBuilder;

@Controller
@RequestMapping("/project")
public class EditProjectController {
  private static final Logger log = LoggerFactory.getLogger(EditProjectController.class);

  @Autowired
  private ProjectService projectService;
  @Autowired
  private IssueTrackerManager issueTrackerManager;
  @Autowired
  private ExternalUriBuilder externalUriBuilder;
  @Autowired
  private SubprojectService subprojectService;
  @Autowired
  private NamingScheme namingScheme;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping("/new")
  public ModelAndView setupForm(ModelMap model) throws IOException {
    return setupForm(ProjectImpl.UNSAVED_ID, model);
  }

  @GetMapping("/shortname/{shortName}")
  public ModelAndView byProjectShortName(@PathVariable String shortName, ModelMap model) throws IOException {
    Project project = projectService.getProjectByShortName(shortName);
    if (project == null) throw new NotFoundException("No project found for shortname " + shortName);
    return setupForm(project.getId(), model);
  }

  @GetMapping("/{projectId}")
  public ModelAndView setupForm(@PathVariable Long projectId, ModelMap model) throws IOException {
    List<Issue> issues = Collections.emptyList();
    Project project = null;
    if (projectId == ProjectImpl.UNSAVED_ID) {
      project = new ProjectImpl();
      model.put("title", "New Project");
    } else {
      project = projectService.get(projectId);
      if (project == null) {
        throw new NotFoundException("No project found for ID " + projectId.toString());
      }
      Collection<Subproject> subprojects = subprojectService.getByProjectId(projectId);
      model.put("subprojects", Dtos.asSubprojectDtos(subprojects));
      model.put("title", "Project " + projectId);
      try {
        issues = issueTrackerManager.getIssuesByTag(project.getShortName());
      } catch (IOException e) {
        log.error("Error retrieving issues", e);
      }
    }

    model.put("projectIssues", issues.stream().map(Dtos::asDto).collect(Collectors.toList()));
    model.put("projectReportLinks", externalUriBuilder.getUris(project));
    model.put("project", project);

    ObjectMapper mapper = new ObjectMapper();
    model.put("projectDto", mapper.writeValueAsString(Dtos.asDto(project)));

    ArrayNode progressOptions = mapper.createArrayNode();
    for (ProgressType item : ProgressType.values()) {
      progressOptions.add(item.getKey());
    }
    model.put("progressOptions", progressOptions);
    model.put("shortNameRequired", !namingScheme.nullProjectShortNameAllowed());

    return new ModelAndView("/WEB-INF/pages/editProject.jsp", model);
  }

  @PostMapping
  public ModelAndView processSubmit(@ModelAttribute("project") Project project, ModelMap model, SessionStatus session,
      HttpServletRequest request)
      throws IOException {
    try {
      projectService.saveProject(project);
      session.setComplete();
      model.clear();
      return new ModelAndView("redirect:/miso/project/" + project.getId(), model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save project", ex);
      }
      throw ex;
    }
  }
}
