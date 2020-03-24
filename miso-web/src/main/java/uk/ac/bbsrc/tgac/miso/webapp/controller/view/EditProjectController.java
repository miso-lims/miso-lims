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

package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import uk.ac.bbsrc.tgac.miso.core.data.Issue;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.context.ExternalUriBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

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
  private NamingSchemeHolder namingSchemeHolder;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping("/new")
  public ModelAndView setupForm(ModelMap model) throws IOException {
    model.put("title", "New Project");
    return setupForm(new ProjectImpl(), model);
  }

  @GetMapping("/shortname/{shortName}")
  public ModelAndView byProjectShortName(@PathVariable String shortName, ModelMap model) throws IOException {
    Project project = projectService.getProjectByShortName(shortName);
    if (project == null) {
      throw new NotFoundException("No project found for shortname " + shortName);
    }
    return setupForm(project, model);
  }

  @GetMapping("/{projectId}")
  public ModelAndView setupForm(@PathVariable Long projectId, ModelMap model) throws IOException {
    Project project = projectService.get(projectId);
    if (project == null) {
      throw new NotFoundException("No project found for ID " + projectId.toString());
    }
    return setupForm(project, model);
  }

  private ModelAndView setupForm(Project project, ModelMap model) throws IOException {
    if (project.isSaved()) {
      Collection<Subproject> subprojects = subprojectService.getByProjectId(project.getId());
      model.put("subprojects", Dtos.asSubprojectDtos(subprojects));
      model.put("title", "Project " + project.getId());
      List<Issue> issues = null;
      try {
        issues = issueTrackerManager.getIssuesByTag(project.getShortName());
      } catch (IOException e) {
        log.error("Error retrieving issues", e);
        issues = Collections.emptyList();
      }

      model.put("projectIssues", issues.stream().map(Dtos::asDto).collect(Collectors.toList()));
      model.put("projectReportLinks", externalUriBuilder.getUris(project));
    }

    model.put("project", project);

    ObjectMapper mapper = new ObjectMapper();
    model.put("projectDto", mapper.writeValueAsString(Dtos.asDto(project)));

    ObjectNode formConfig = mapper.createObjectNode();
    MisoWebUtils.addJsonArray(mapper, formConfig, "statusOptions", Arrays.asList(StatusType.values()), StatusType::getKey);
    ObjectNode namingConfig = formConfig.putObject("naming");
    addNamingSchemeConfig(namingConfig, "primary", namingSchemeHolder.getPrimary(), project);
    addNamingSchemeConfig(namingConfig, "secondary", namingSchemeHolder.getSecondary(), project);
    model.put("formConfig", mapper.writeValueAsString(formConfig));

    return new ModelAndView("/WEB-INF/pages/editProject.jsp", model);
  }

  private static void addNamingSchemeConfig(ObjectNode namingConfig, String property, NamingScheme scheme, Project project) {
    if (scheme != null) {
      ObjectNode config = namingConfig.putObject(property);
      config.put("shortNameRequired", !scheme.nullProjectShortNameAllowed());
      config.put("shortNameModifiable", scheme.nullProjectShortNameAllowed() || project.getSamples().isEmpty());
    }
  }

}
