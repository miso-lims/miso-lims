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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.impl.RunService;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class ProjectTreeControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ProjectTreeControllerHelperService.class);
  @Autowired
  private ProjectService projectService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private RunService runService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private StudyService studyService;

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  /**
   * starts with project listing list all the projects and subs define the number of Runs, Samples and Studies return as a JSONarray
   */
  public JSONObject listProjectTree(HttpSession session, JSONObject json) {
    try {
      Collection<Project> projects = projectService.listAllProjects();

      JSONObject miso = new JSONObject();
      JSONArray projectsArray = new JSONArray();
      for (Project p : projects) {
        JSONObject projectJSON = new JSONObject();
        projectJSON.put("name", p.getName());
        projectJSON.put("show", "PROJECT");
        projectJSON.put("id", p.getProjectId());
        projectJSON.put("description", p.getAlias());
        Collection<Sample> samples = sampleService.listByProjectId(p.getProjectId());
        Collection<Run> runs = runService.listByProjectId(p.getProjectId());
        Collection<Study> studies = studyService.listByProjectId(p.getProjectId());
        int subs = samples.size() + runs.size() + studies.size();
        projectJSON.put("subs", subs);
        projectsArray.add(projectJSON);
      }
      miso.put("name", "miso");
      miso.put("show", "MISO");
      miso.put("description", "");

      miso.put("children", projectsArray);
      return miso;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  /*
   * This method looks for sub parts of the Project and returns Names of the sub parts i.e. Runs, Samples, Studies etc
   */

  public JSONObject PROJECTsubs(HttpSession session, JSONObject json) {
    try {

      long projectId = json.getLong("id");

      JSONArray childArray = new JSONArray();

      Collection<Run> runs = runService.listByProjectId(projectId);
      if (runs.size() > 0) {
        JSONObject child = new JSONObject();
        child.put("name", "RUN");
        child.put("show", "RUN");
        child.put("subs", runs.size());
        childArray.add(child);
      }

      Collection<Sample> samples = sampleService.listByProjectId(projectId);

      if (samples.size() > 0) {
        JSONObject child = new JSONObject();
        child.put("name", "SAMPLE");
        child.put("show", "SAMPLE");
        child.put("subs", samples.size());
        childArray.add(child);
      }

      Collection<Study> studies = studyService.listByProjectId(projectId);

      if (studies.size() > 0) {
        JSONObject child = new JSONObject();
        child.put("name", "STUDY");
        child.put("show", "STUDY");
        child.put("subs", studies.size());
        childArray.add(child);
      }

      JSONObject miso = new JSONObject();

      miso.put("id", projectId);
      miso.put("children", childArray);

      return miso;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  /* This method returns list of Runs related to Project Id */

  public JSONObject RUNs(HttpSession session, JSONObject json) {
    try {

      long projectId = json.getLong("id");

      JSONArray runsArray = new JSONArray();

      Collection<Run> runs = runService.listByProjectId(projectId);

      JSONObject miso = new JSONObject();
      for (Run run : runs) {
        if (run.getHealth() == HealthType.Completed) {
          runsArray.add(JSONObject
              .fromObject("{'name': '" + run.getName() + "','description':'" + run.getAlias() + "','show':'\"RUN \"','color': '1'}"));
        } else {
          runsArray.add(JSONObject
              .fromObject("{'name': '" + run.getName() + "','description':'" + run.getAlias() + "','show':'\"RUN \"','color': '0'}"));
        }

      }
      miso.put("id", projectId);
      miso.put("children", runsArray);

      return miso;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  /* This method returns list of Samples related to Project Id */

  public JSONObject SAMPLEs(HttpSession session, JSONObject json) {
    try {

      long projectId = json.getLong("id");

      JSONArray samplesArray = new JSONArray();
      JSONObject miso = new JSONObject();

      Collection<Sample> samples = sampleService.listByProjectId(projectId);

      for (Sample sample : samples) {
        Collection<Library> libraries = libraryService.listBySampleId(sample.getId());
        if (libraries.size() == 0) {

          String sampleQC = "0";
          if (sample.getQcPassed() != null) {
            if (sample.getQcPassed()) {
              sampleQC = "1";
            }
          }
          samplesArray.add(JSONObject.fromObject(
              "{'name': '" + sample.getName() + "','description':'" + sample.getAlias() + "','subs':'\"0\"','color': '" + sampleQC + "'}"));
        } else {
          JSONObject subsampleJSON = new JSONObject();
          subsampleJSON.put("name", sample.getName());
          subsampleJSON.put("id", sample.getId());
          subsampleJSON.put("description", sample.getAlias());
          subsampleJSON.put("subs", libraries.size());
          subsampleJSON.put("show", "SAMPLE");

          samplesArray.add(subsampleJSON);
        }
      }

      miso.put("children", samplesArray);

      return miso;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  /* This method returns list of Libraries related to Sample */

  public JSONObject SAMPLEsubs(HttpSession session, JSONObject json) {
    try {

      long sampleId = json.getLong("id");

      JSONObject miso = new JSONObject();

      Collection<Library> libraries = libraryService.listBySampleId(sampleId);
      JSONArray librariesArray = new JSONArray();

      for (Library library : libraries) {
        if (library.getQCs().size() > 0) {
          librariesArray
              .add(JSONObject.fromObject("{'name': '" + library.getName() + "','description':'" + library.getAlias() + "','color': '1'}"));
        } else {
          librariesArray
              .add(JSONObject.fromObject("{'name': '" + library.getName() + "','description':'" + library.getAlias() + "','color': '0'}"));
        }
      }
      miso.put("id", sampleId);
      miso.put("children", librariesArray);
      return miso;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  /* This method returns list of Studies related to Project Id */

  public JSONObject STUDYs(HttpSession session, JSONObject json) {
    try {

      long projectId = json.getLong("id");

      JSONArray childArray = new JSONArray();

      Collection<Study> studies = studyService.listByProjectId(projectId);
      JSONObject studyJSON = new JSONObject();

      studyJSON.put("name", "Studies");
      studyJSON.put("description", "");
      for (Study study : studies) {
        JSONObject substudyJSON = new JSONObject();
        substudyJSON.put("name", study.getName());
        substudyJSON.put("description", study.getAlias());
        substudyJSON.put("id", study.getId());
        substudyJSON.put("show", "STUDY");

        Collection<Experiment> experiments = experimentService.listAllByStudyId(study.getId());
        substudyJSON.put("subs", experiments.size());
        childArray.add(substudyJSON);
      }

      JSONObject miso = new JSONObject();

      miso.put("children", childArray);

      return miso;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  /* This method returns list of Experiments related to Study */

  public JSONObject STUDYsubs(HttpSession session, JSONObject json) {
    try {

      long studyId = json.getLong("id");

      Collection<Experiment> experiments = experimentService.listAllByStudyId(studyId);
      JSONArray experimentsArray = new JSONArray();
      for (Experiment e : experiments) {
        experimentsArray.add(JSONObject.fromObject("{'name': '" + e.getName() + "','description':'" + e.getAlias() + "','color': '2'}"));
      }

      JSONObject miso = new JSONObject();
      miso.put("id", studyId);
      miso.put("children", experimentsArray);

      return miso;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

}
