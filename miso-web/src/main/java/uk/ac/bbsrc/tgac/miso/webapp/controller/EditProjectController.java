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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.*;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedLibraryQcException;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.security.util.LimsSecurityUtils;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;
import uk.ac.bbsrc.tgac.miso.webapp.context.ApplicationContextProvider;

import javax.servlet.http.HttpServletRequest;

 @Controller
@RequestMapping("/project")
@SessionAttributes("project")
public class EditProjectController {
  protected static final Logger log = LoggerFactory.getLogger(EditProjectController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private FilesManager filesManager;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

  @Autowired
  private JdbcTemplate interfaceTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    CustomDateEditor cde = new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true);
    binder.registerCustomEditor(Date.class, cde);
  }

  public Map<Integer, String> populateProjectFiles(Long projectId) throws IOException {
    if (projectId != AbstractProject.UNSAVED_ID) {
      Project p = requestManager.getProjectById(projectId);
      if (p != null) {
        //User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        Map<Integer, String> fileMap = new HashMap<Integer, String>();
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
    return DbUtils.getColumnSizes(interfaceTemplate, "Project");
  }

  @ModelAttribute("sampleQcTypesString")
  public String sampleTypesString() throws IOException {
    List<String> types = new ArrayList<String>();
    for (QcType s : requestManager.listAllSampleQcTypes()) {
      types.add("\"" + s.getQcTypeId() + "\"" + ":" + "\"" + s.getName() + "\"");
    }
    Collections.sort(types);
    return LimsUtils.join(types, ",");
  }

  @ModelAttribute("sampleQCUnits")
  public String sampleQCUnits() throws IOException {
    return AbstractSampleQC.UNITS;
  }

  @ModelAttribute("libraryQcTypesString")
  public String libraryTypesString() throws IOException {
    List<String> types = new ArrayList<String>();
    List<QcType> libraryQcTypes = new ArrayList<QcType>(requestManager.listAllLibraryQcTypes());
    Collections.sort(libraryQcTypes);
    for (QcType s : libraryQcTypes) {
      types.add("\"" + s.getQcTypeId() + "\"" + ":" + "\"" + s.getName() + "\"");
    }
    return LimsUtils.join(types, ",");
  }

  public Collection<Run> populateProjectRuns(long projectId) throws IOException {
    List<Run> runs = new ArrayList<Run>(requestManager.listAllRunsByProjectId(projectId));
    try {
      Collections.sort(runs, new AliasComparator(Run.class));
      for (Run r : runs) {
        RunImpl ri = (RunImpl) r;
        ri.setSequencerPartitionContainers(new ArrayList<SequencerPartitionContainer<SequencerPoolPartition>>(
                requestManager.listSequencerPartitionContainersByRunId(r.getId())));
      }
      return runs;
    }
    catch (NoSuchMethodException e) {
      throw new IOException(e);
    }
  }

  public Collection<Library> populateProjectLibraries(long projectId) throws IOException {
    List<Library> libraries = new ArrayList<Library>(requestManager.listAllLibrariesByProjectId(projectId));
    try {
      Collections.sort(libraries, new AliasComparator(Library.class));
      for (Library l : libraries) {
        for (LibraryQC qc : requestManager.listAllLibraryQCsByLibraryId(l.getId())) {
          try {
            l.addQc(qc);
          }
          catch (MalformedLibraryQcException e) {
            throw new IOException(e);
          }
        }
      }
    }
    catch (NoSuchMethodException e) {
      throw new IOException(e);
    }

    return libraries;
  }

  public Collection<LibraryDilution> populateProjectLibraryDilutions(Collection<Library> projectLibraries) throws IOException {
    List<LibraryDilution> dilutions = new ArrayList<LibraryDilution>();
    for (Library l : projectLibraries) {
      dilutions.addAll(requestManager.listAllLibraryDilutionsByLibraryId(l.getId()));
    }
    Collections.sort(dilutions);
    return dilutions;
  }

  public Collection<LibraryDilution> populateProjectLibraryDilutions(long projectId) throws IOException {
    List<LibraryDilution> dilutions = new ArrayList<LibraryDilution>(requestManager.listAllLibraryDilutionsByProjectId(projectId));
    Collections.sort(dilutions);
    return dilutions;
  }

  public Collection<Pool> populateProjectPools(long projectId) throws IOException {
    List<Pool> pools = new ArrayList<Pool>(requestManager.listPoolsByProjectId(projectId));
    Collections.sort(pools);
    return pools;
  }

  public boolean existsAnyEmPcrLibrary(Collection<LibraryDilution> projectLibraryDilutions) throws IOException {
    boolean exists = false;
    for (LibraryDilution dil : projectLibraryDilutions) {
      if (!dil.getLibrary().getPlatformName().equals(PlatformType.ILLUMINA.getKey())) {
        exists = true;
      }
    }
    return exists;
  }

  public boolean existsAnyEmPcrLibrary(long projectId) throws IOException {
    boolean exists = false;
    for (LibraryDilution dil : populateProjectLibraryDilutions(projectId)) {
      if (!dil.getLibrary().getPlatformName().equals(PlatformType.ILLUMINA.getKey())) {
        exists = true;
      }
    }
    return exists;
  }

  public Collection<emPCR> populateProjectEmPCRs(long projectId) throws IOException {
    List<emPCR> pcrs = new ArrayList<emPCR>(requestManager.listAllEmPCRsByProjectId(projectId));
    Collections.sort(pcrs);
    return pcrs;
  }

  public Collection<emPCRDilution> populateProjectEmPcrDilutions(Collection<emPCR> projectEmPCRs) throws IOException {
    List<emPCRDilution> dilutions = new ArrayList<emPCRDilution>();
    for (emPCR e : projectEmPCRs) {
      dilutions.addAll(requestManager.listAllEmPcrDilutionsByEmPcrId(e.getId()));
    }
    Collections.sort(dilutions);
    return dilutions;
  }

  public Collection<emPCRDilution> populateProjectEmPcrDilutions(long projectId) throws IOException {
    List<emPCRDilution> dilutions = new ArrayList<emPCRDilution>(requestManager.listAllEmPcrDilutionsByProjectId(projectId));
    Collections.sort(dilutions);
    return dilutions;
  }

  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> populateProjectPlates(long projectId) throws IOException {
    List<Plate<? extends List<? extends Plateable>, ? extends Plateable>> plates = new ArrayList<Plate<? extends List<? extends Plateable>, ? extends Plateable>>(requestManager.listAllPlatesByProjectId(projectId));
    Collections.sort(plates);
    return plates;
  }

  public Map<Long, Collection<Library>> populateLibraryGroupMap(Project project, Collection<Library> projectLibraries) throws IOException {
    Map<Long, Collection<Library>> libraryGroupMap = new HashMap<>();

    for (ProjectOverview po : project.getOverviews()) {
      if (po.getSampleGroup() != null && !po.getSampleGroup().getEntities().isEmpty()) {
        Set<Library> libs = new HashSet<>();
        for (Sample s : po.getSampleGroup().getEntities()) {
          for (Library pl : projectLibraries) {
            if (pl.getSample().equals(s)) {
              libs.add(pl);
            }
          }
        }
        libraryGroupMap.put(po.getId(), libs);
      }
    }

    return libraryGroupMap;
  }

  @RequestMapping(value = "/graph/{projectId}", method = RequestMethod.GET)
  public
  @ResponseBody
  JSONObject graphRest(@PathVariable Long projectId) throws IOException {
    JSONObject j = new JSONObject();
    try {
      Collection<Sample> samples = requestManager.listAllSamplesByProjectId(projectId);
      Collection<Run> runs = requestManager.listAllRunsByProjectId(projectId);
      Collection<Study> studies = requestManager.listAllStudiesByProjectId(projectId);

      JSONObject runsJSON = new JSONObject();
      JSONObject studiesJSON = new JSONObject();
      JSONObject samplesJSON = new JSONObject();

      for (Run run : runs) {
        if (run.getStatus() != null
            && run.getStatus().getHealth() != null
            && run.getStatus().getHealth().getKey().equals("Completed")) {
          runsJSON.put(run.getName(), "1");
        }
        else {
          runsJSON.put(run.getName(), "0");
        }
      }

      for (Study study : studies) {
        Collection<Experiment> experiments = requestManager.listAllExperimentsByStudyId(study.getId());
        if (experiments.size() == 0) {
          studiesJSON.put(study.getName(), "2");
        }
        else {
          JSONObject experimentsJSON = new JSONObject();
          for (Experiment e : experiments) {
            experimentsJSON.put(e.getName(), "2");
          }
          studiesJSON.put(study.getName(), experimentsJSON);
        }
      }

      for (Sample sample : samples) {
        Collection<Library> libraries = requestManager.listAllLibrariesBySampleId(sample.getId());
        if (libraries.size() == 0) {
          if (sample.getQcPassed()) {
            samplesJSON.put(sample.getName(), "1");
          }
          else {
            samplesJSON.put(sample.getName(), "0");
          }
        }
        else {
          JSONObject librariesJSON = new JSONObject();
          for (Library library : libraries) {
            Collection<LibraryDilution> lds = requestManager.listAllLibraryDilutionsByLibraryId(library.getId());
            if (lds.size() > 0) {
              JSONObject dilutionsJSON = new JSONObject();
              for (LibraryDilution ld : lds) {
                dilutionsJSON.put(ld.getName(), "2");
              }
              librariesJSON.put(library.getName(), dilutionsJSON);
            }
            else {
              if (library.getLibraryQCs().size() > 0) {
                librariesJSON.put(library.getName(), "1");
              }
              else {
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
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  @RequestMapping(value = "/new", method = RequestMethod.GET)
  public ModelAndView setupForm(ModelMap model) throws IOException {
    return setupForm(AbstractProject.UNSAVED_ID, model);
  }

  @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
  public ModelAndView setupForm(@PathVariable Long projectId,
                                ModelMap model) throws IOException {
    try {
      User user = securityManager
              .getUserByLoginName(SecurityContextHolder.getContext()
                      .getAuthentication().getName());
      Project project = null;
      if (projectId == AbstractProject.UNSAVED_ID) {
        project = dataObjectFactory.getProject(user);
        model.put("title", "New Project");
      }
      else {
        project = requestManager.getProjectById(projectId);
        model.put("title", "Project " + projectId);
        model.put("projectRuns", populateProjectRuns(projectId));

        Collection<Library> libraries = populateProjectLibraries(projectId);
        model.put("projectLibraries", libraries);

        Collection<LibraryDilution> libraryDilutions = populateProjectLibraryDilutions(libraries);
        model.put("projectLibraryDilutions", libraryDilutions);
        model.put("existsAnyEmPcrLibrary", existsAnyEmPcrLibrary(libraryDilutions));

        Collection<emPCR> emPcrs = populateProjectEmPCRs(projectId);
        model.put("projectEmPcrs", emPcrs);
        model.put("projectEmPcrDilutions", populateProjectEmPcrDilutions(emPcrs));

        model.put("projectPools", populateProjectPools(projectId));
        model.put("projectPlates", populateProjectPlates(projectId));

        model.put("libraryGroupMap", populateLibraryGroupMap(project, libraries));
      }

      if (project == null) {
        throw new SecurityException("No such Project");
      }

      if (!project.userCanRead(user)) {
        throw new SecurityException("Permission denied.");
      }

      model.put("formObj", project);
      model.put("project", project);
      model.put("projectFiles", populateProjectFiles(projectId));
      //model.put("projectRuns", requestManager.listAllRunsByProjectId(projectId));
      model.put("owners", LimsSecurityUtils.getPotentialOwners(user, project, securityManager.listAllUsers()));
      model.put("accessibleUsers", LimsSecurityUtils.getAccessibleUsers(user, project, securityManager.listAllUsers()));
      model.put("accessibleGroups", LimsSecurityUtils.getAccessibleGroups(user, project, securityManager.listAllGroups()));
      model.put("overviews", project.getOverviews());

      Map<Long, String> overviewMap = new HashMap<Long, String>();
      for (ProjectOverview po : project.getOverviews()) {
        //log.debug(po.getWatchers().toString());
        if (po.getWatchers().contains(user)) {
          overviewMap.put(po.getId(), user.getLoginName());
        }
      }
      model.put("overviewMap", overviewMap);

      return new ModelAndView("/pages/editProject.jsp", model);
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to show project", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("project") Project project,
                              ModelMap model, SessionStatus session, HttpServletRequest request) throws IOException {
    try {
      User user = securityManager
              .getUserByLoginName(SecurityContextHolder.getContext()
                      .getAuthentication().getName());
      if (!project.userCanWrite(user)) {
        throw new SecurityException("Permission denied.");
      }
      requestManager.saveProject(project);
      session.setComplete();
      model.clear();
      return "redirect:/miso/project/" + project.getProjectId();
    }
    catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save project", ex);
      }
      throw ex;
    }
  }
 }
