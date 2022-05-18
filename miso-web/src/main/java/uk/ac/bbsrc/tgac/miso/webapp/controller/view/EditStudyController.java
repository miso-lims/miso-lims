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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.StudyService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

@Controller
@RequestMapping("/study")
public class EditStudyController {

  @Autowired
  private ProjectService projectService;
  @Autowired
  private StudyService studyService;
  @Autowired
  private ObjectMapper mapper;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  @GetMapping(value = "/new")
  public ModelAndView newStudy(ModelMap model) throws IOException {
    return setupForm(new StudyImpl(), model);
  }

  @GetMapping(value = "/new/{projectId}")
  public ModelAndView newAssignedProject(@PathVariable long projectId, ModelMap model) throws IOException {
    Study study = new StudyImpl();
    Project project = projectService.get(projectId);
    if (project == null) {
      throw new NotFoundException("No project found with ID " + projectId);
    }
    study.setProject(project);
    return setupForm(study, model);
  }

  @GetMapping(value = "/{studyId}")
  public ModelAndView setupForm(@PathVariable Long studyId, ModelMap model) throws IOException {
    Study study = studyService.get(studyId);
    if (study == null) throw new NotFoundException("No study found for ID " + studyId.toString());

    return setupForm(study, model);
  }

  private ModelAndView setupForm(Study study, ModelMap model) throws IOException {
    model.put("title", study.isSaved() ? ("Study " + study.getId()) : "New Study");
    model.put("study", study);
    model.put("studyDto", mapper.writeValueAsString(Dtos.asDto(study)));
    model.put("projects", mapper.writeValueAsString(projectService.list().stream()
        .map(Dtos::asDto)
        .collect(Collectors.toList())));

    model.put("experiments",
        study.getExperiments().stream().map(expt -> Dtos.asDto(expt)).collect(Collectors.toList()));
    return new ModelAndView("/WEB-INF/pages/editStudy.jsp", model);
  }
}
