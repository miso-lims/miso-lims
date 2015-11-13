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

package uk.ac.bbsrc.tgac.miso.webapp.controller.d3graph;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.webapp.controller.EditProjectController;

/**
 * Created by IntelliJ IDEA. User: bianx Date: 27/09/11 Time: 15:02 To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/d3graph/project")
@SessionAttributes("project")
public class ProjectD3GraphController {
  protected static final Logger log = LoggerFactory.getLogger(EditProjectController.class);

  @Autowired
  private RequestManager requestManager;

  @RequestMapping(value = "{projectId}", method = RequestMethod.GET)
  public @ResponseBody JSONObject d3graphRest(@PathVariable Long projectId) throws IOException {

    try {
      Project p = requestManager.getProjectById(projectId);
      JSONObject projectJSON = new JSONObject();
      projectJSON.put("name", p.getName());
      projectJSON.put("show", "PROJECT");
      projectJSON.put("description", p.getAlias());
      JSONArray projectChildrenArray = new JSONArray();
      Collection<Sample> samples = requestManager.listAllSamplesByProjectId(p.getProjectId());
      Collection<Run> runs = requestManager.listAllRunsByProjectId(p.getProjectId());
      Collection<Study> studies = requestManager.listAllStudiesByProjectId(p.getProjectId());

      JSONObject runJSON = new JSONObject();
      JSONArray runsArray = new JSONArray();

      runJSON.put("name", "Runs");
      runJSON.put("description", "");
      for (Run run : runs) {
        if (run.getStatus() != null && run.getStatus().getHealth() != null && run.getStatus().getHealth().getKey().equals("Completed")) {
          runsArray.add(JSONObject.fromObject("{'name': '" + run.getName() + "','description':'" + run.getAlias() + "','color': '1'}"));
        } else {
          runsArray.add(JSONObject.fromObject("{'name': '" + run.getName() + "','description':'" + run.getAlias() + "','color': '0'}"));
        }
      }
      runJSON.put("children", runsArray);
      if (runsArray.size() > 0) {
        projectChildrenArray.add(runJSON);
      }

      JSONObject studyJSON = new JSONObject();
      JSONArray studiesArray = new JSONArray();

      studyJSON.put("name", "Studies");
      studyJSON.put("description", "");
      for (Study study : studies) {

        JSONObject substudyJSON = new JSONObject();
        JSONArray substudiesArray = new JSONArray();
        substudyJSON.put("name", study.getName());
        substudyJSON.put("description", study.getAlias());
        Collection<Experiment> experiments = requestManager.listAllExperimentsByStudyId(study.getId());
        if (experiments.size() > 0) {
          JSONObject experimentJSON = new JSONObject();
          JSONArray experimentsArray = new JSONArray();
          experimentJSON.put("name", "experiment");
          experimentJSON.put("description", "");
          for (Experiment e : experiments) {
            experimentsArray
                .add(JSONObject.fromObject("{'name': '" + e.getName() + "','description':'" + e.getAlias() + "','color': '2'}"));
          }
          experimentJSON.put("children", experimentsArray);
          substudiesArray.add(experimentJSON);
        }
        if (substudiesArray.size() > 0) {
          substudyJSON.put("children", substudiesArray);
        }
        studiesArray.add(substudyJSON);
      }
      studyJSON.put("children", studiesArray);

      if (studiesArray.size() > 0) {
        projectChildrenArray.add(studyJSON);
      }

      JSONObject sampleJSON = new JSONObject();
      JSONArray samplesArray = new JSONArray();

      sampleJSON.put("name", "Samples");
      sampleJSON.put("description", "");
      for (Sample sample : samples) {
        Collection<Library> libraries = requestManager.listAllLibrariesBySampleId(sample.getId());
        if (libraries.size() == 0) {

          String sampleQC = "0";
          if (sample.getQcPassed() != null) {
            if (sample.getQcPassed()) {
              sampleQC = "1";
            }
          }
          samplesArray.add(JSONObject
              .fromObject("{'name': '" + sample.getName() + "','description':'" + sample.getAlias() + "','color': '" + sampleQC + "'}"));
        } else {
          JSONObject libraryJSON = new JSONObject();
          JSONArray librariesArray = new JSONArray();

          libraryJSON.put("name", "Libraries");

          for (Library library : libraries) {
            if (library.getLibraryQCs().size() > 0) {
              librariesArray.add(
                  JSONObject.fromObject("{'name': '" + library.getName() + "','description':'" + library.getAlias() + "','color': '1'}"));
            } else {
              librariesArray.add(
                  JSONObject.fromObject("{'name': '" + library.getName() + "','description':'" + library.getAlias() + "','color': '0'}"));
            }
          }
          libraryJSON.put("children", librariesArray);

          JSONObject subsampleJSON = new JSONObject();
          subsampleJSON.put("name", sample.getName());
          subsampleJSON.put("description", sample.getAlias());
          subsampleJSON.put("children", librariesArray);
          samplesArray.add(subsampleJSON);
        }
      }
      sampleJSON.put("children", samplesArray);
      if (samplesArray.size() > 0) {
        projectChildrenArray.add(sampleJSON);
      }

      projectJSON.put("children", projectChildrenArray);
      return projectJSON;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  @RequestMapping(method = RequestMethod.GET)
  public @ResponseBody JSONObject graphd3Rest() throws IOException {
    try {
      Collection<Project> projects = requestManager.listAllProjects();

      JSONObject miso = new JSONObject();
      JSONArray projectsArray = new JSONArray();
      for (Project p : projects) {

        JSONObject projectJSON = d3graphRest(p.getProjectId());
        JSONArray projectChildrenArray = (JSONArray) projectJSON.get("children");
        if (projectChildrenArray.size() > 0) {
          projectsArray.add(projectJSON);
        }
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

}
