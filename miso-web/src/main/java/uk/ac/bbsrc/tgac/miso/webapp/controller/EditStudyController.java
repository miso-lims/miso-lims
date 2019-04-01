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
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Controller
@RequestMapping("/study")
@SessionAttributes("study")
public class EditStudyController {
  protected static final Logger log = LoggerFactory.getLogger(EditStudyController.class);

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private StudyService studyService;

  /**
   * Retrieves from miso.properties whether detailed sample mode has been enabled
   */
  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  /**
   * Gets status of detailed sample mode
   * 
   * @return whether detailed sample mode has been enabled by miso.properties
   */
  @ModelAttribute("detailedSample")
  private Boolean isDetailedSampleEnabled() {
    return detailedSample;
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public Project populateProject(@PathVariable Long projectId) throws IOException {
    try {
      return projectService.get(projectId);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to get parent project", ex);
      }
      throw ex;
    }
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return studyService.getColumnSizes();
  }

  @ModelAttribute("studyTypes")
  public Collection<StudyType> populateStudyTypes() throws IOException {
    return studyService.listTypes();
  }
  
  /**
   * Populates 'projects' model attribute with list of projects sorted by name if in plain sample mode, by shortname if in detailed
   * sample mode.
   * 
   * @return Collection of Project sorted by name if in plain sample mode, by shortname if in detailed sample mode
   * @throws IOException upon failure to access Projects
   */
  @ModelAttribute("projects")
  public Collection<Project> populateProjects() throws IOException {
    if (detailedSample) {
      return projectService.listAllProjectsByShortname();
    } else {
      return projectService.listAllProjects();
    }
  }

  @GetMapping(value = "/new")
  public ModelAndView newStudy(ModelMap model) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Study study = new StudyImpl();

    return setupForm(study, user, "New Study", model);
  }

  @GetMapping(value = "/new/{projectId}")
  public ModelAndView newAssignedProject(@PathVariable Long projectId, ModelMap model) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Study study = new StudyImpl();
    Project project = projectService.get(projectId);
    study.setProject(project);
    return setupForm(study, user, "New Study", model);
  }

  @GetMapping(value = "/{studyId}")
  public ModelAndView setupForm(@PathVariable Long studyId, ModelMap model) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Study study = studyService.get(studyId);
    if (study == null) throw new NotFoundException("No study found for ID " + studyId.toString());

    return setupForm(study, user, "Study " + studyId, model);
  }

  private ModelAndView setupForm(Study study, User user, String title, ModelMap model) throws IOException {
    model.put("title", title);
    model.put("formObj", study);
    model.put("study", study);
    model.put("experiments", study.getExperiments().stream().map(Dtos::asDto).collect(Collectors.toList()));
    return new ModelAndView("/WEB-INF/pages/editStudy.jsp", model);
  }

  @PostMapping
  public ModelAndView processSubmit(@ModelAttribute("study") Study study, ModelMap model, SessionStatus session) throws IOException {
    studyService.save(study);
    session.setComplete();
    model.clear();
    return new ModelAndView("redirect:/miso/study/" + study.getId(), model);
  }
}
