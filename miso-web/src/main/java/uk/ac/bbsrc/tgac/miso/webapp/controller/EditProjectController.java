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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractProject;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

@Controller
@RequestMapping("/project")
@SessionAttributes("project")
public class EditProjectController {
  private static final Logger log = LoggerFactory.getLogger(EditProjectController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private FilesManager filesManager;

  @Autowired
  private ReferenceGenomeService referenceGenomeService;
  @Autowired
  private RunService runService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private StudyService studyService;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    CustomDateEditor cde = new CustomDateEditor(LimsUtils.getDateFormat(), true);
    binder.registerCustomEditor(Date.class, cde);
  }

  public Map<Integer, String> populateProjectFiles(Long projectId) throws IOException {
    if (projectId != AbstractProject.UNSAVED_ID) {
      Project p = requestManager.getProjectById(projectId);
      if (p != null) {
        Map<Integer, String> fileMap = new HashMap<>();
        for (String s : filesManager.getFileNames(Project.class, projectId.toString())) {
          fileMap.put(s.hashCode(), s);
        }
        return fileMap;
      }
    }
    return Collections.emptyMap();
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return requestManager.getProjectColumnSizes();
  }

  @ModelAttribute("poolConcentrationUnits")
  public String poolConcentrationUnits() {
    return PoolImpl.CONCENTRATION_UNITS;
  }

  @ModelAttribute("libraryDilutionUnits")
  public String libraryDilutionUnits() {
    return LibraryDilution.UNITS;
  }

  @RequestMapping(value = "/graph/{projectId}", method = RequestMethod.GET)
  public @ResponseBody JSONObject graphRest(@PathVariable Long projectId) throws IOException {
    JSONObject j = new JSONObject();
    try {
      Collection<Sample> samples = sampleService.listByProjectId(projectId);
      Collection<Run> runs = runService.listByProjectId(projectId);
      Collection<Study> studies = studyService.listByProjectId(projectId);

      JSONObject runsJSON = new JSONObject();
      JSONObject studiesJSON = new JSONObject();
      JSONObject samplesJSON = new JSONObject();

      for (Run run : runs) {
        if (run.getHealth() == HealthType.Completed) {
          runsJSON.put(run.getName(), "1");
        } else {
          runsJSON.put(run.getName(), "0");
        }
      }

      for (Study study : studies) {
        Collection<Experiment> experiments = experimentService.listAllByStudyId(study.getId());
        if (experiments.size() == 0) {
          studiesJSON.put(study.getName(), "2");
        } else {
          JSONObject experimentsJSON = new JSONObject();
          for (Experiment e : experiments) {
            experimentsJSON.put(e.getName(), "2");
          }
          studiesJSON.put(study.getName(), experimentsJSON);
        }
      }

      for (Sample sample : samples) {
        Collection<Library> libraries = libraryService.listBySampleId(sample.getId());
        if (libraries.size() == 0) {
          if (sample.getQcPassed()) {
            samplesJSON.put(sample.getName(), "1");
          } else {
            samplesJSON.put(sample.getName(), "0");
          }
        } else {
          JSONObject librariesJSON = new JSONObject();
          for (Library library : libraries) {
            Collection<LibraryDilution> lds = dilutionService.listByLibraryId(library.getId());
            if (lds.size() > 0) {
              JSONObject dilutionsJSON = new JSONObject();
              for (LibraryDilution ld : lds) {
                dilutionsJSON.put(ld.getName(), "2");
              }
              librariesJSON.put(library.getName(), dilutionsJSON);
            } else {
              if (library.getQCs().size() > 0) {
                librariesJSON.put(library.getName(), "1");
              } else {
                librariesJSON.put(library.getName(), "0");
              }
            }
          }
          samplesJSON.put(sample.getName(), librariesJSON);
        }
      }

      j.put("Runs", runsJSON);
      j.put("Studies", studiesJSON);
      j.put("Samples", samplesJSON);

      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap model) throws IOException {
    return setupForm(AbstractProject.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long projectId, ModelMap model) throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Project project = null;
      if (projectId == AbstractProject.UNSAVED_ID) {
        project = new ProjectImpl(user);
        model.put("title", "New Project");
      } else {
        project = requestManager.getProjectById(projectId);
        model.put("title", "Project " + projectId);
      }

      if (project == null) {
        throw new SecurityException("No such Project");
      }

      if (!project.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }
      model.put("referenceGenome", referenceGenomeService.listAllReferenceGenomeTypes());
      model.put("formObj", project);
      model.put("project", project);
      model.put("projectFiles", populateProjectFiles(projectId));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, project, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, project, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, project, securityManager.listAllGroups()));
      model.put("overviews", project.getOverviews());

      Map<Long, String> overviewMap = new HashMap<>();
      for (ProjectOverview po : project.getOverviews()) {
        if (po.getWatchers().contains(user)) {
          overviewMap.put(po.getId(), user.getLoginName());
        }
      }
      model.put("overviewMap", overviewMap);

      return new ModelAndView("/pages/editProject.jsp", model);
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show project", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("project") Project project, ModelMap model, SessionStatus session, HttpServletRequest request)
      throws IOException {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      if (!project.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      requestManager.saveProject(project);
      for (ProjectOverview overview : project.getOverviews()) {
        requestManager.saveProjectOverview(overview);
      }
      session.setComplete();
      model.clear();
      return "redirect:/miso/project/" + project.getProjectId();
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save project", ex);
      }
      throw ex;
    }
  }
}
