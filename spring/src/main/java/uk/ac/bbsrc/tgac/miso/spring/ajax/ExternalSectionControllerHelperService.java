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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Created by IntelliJ IDEA. User: bianx Date: 02/11/11 Time: 15:59 To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class ExternalSectionControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(DashboardHelperService.class);
  @Autowired
  private uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public JSONObject listProjects(HttpSession session, JSONObject json) {
    try {
      StringBuilder b = new StringBuilder();
      Collection<Project> projectCollection = requestManager.listAllProjects();

      for (Project p : projectCollection) {
        if (p.getSecurityProfile() == null) {
          log.info("Null project SP: " + p.getProjectId() + " -> " + p.toString());
        }
      }

      if (projectCollection.isEmpty()) {
        b.append("You have no project.");
      } else {
        List<Project> projects = new ArrayList<>(projectCollection);
        Collections.sort(projects);
        for (Project p : projects) {
          b.append("<a class=\"dashboardresult\" onclick=\"showProjectStatus(" + p.getProjectId()
              + ");\" href=\"javascript:void(0);\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + p.getName() + "</b><br/>");
          b.append("Alias: <b>" + p.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject projectStatus(HttpSession session, JSONObject json) {
    JSONObject jsonObject = new JSONObject();
    try {
      Long projectId = json.getLong("projectId");
      StringBuilder projectSb = new StringBuilder();
      StringBuilder sampleQcSb = new StringBuilder();
      Project project = requestManager.getProjectById(projectId);
      projectSb.append("<div class='report'>");
      projectSb.append("<b>Project Name: </b> " + project.getName());
      projectSb.append("<br/><br/>");
      projectSb.append("<b>Project Alias: </b> " + project.getAlias());
      projectSb.append("<br/><br/>");
      projectSb.append("<b>Project Description: </b> " + project.getDescription());
      projectSb.append("<br/><br/>");
      projectSb.append("<b>Progress: </b> " + project.getProgress().name());
      projectSb.append("<br/><br/>");
      if (project.getOverviews().size() > 0) {
        for (ProjectOverview overview : project.getOverviews()) {
          projectSb.append("<div><ol id=\"progress\">\n" + "            <li class=\"sample-qc-step\">\n");
          projectSb.append("<div class=\"");
          if (overview.getAllSampleQcPassed() && overview.getLibraryPreparationComplete()) {
            projectSb.append("left mid-progress-done");
          } else if (overview.getAllSampleQcPassed()) {
            projectSb.append("left-progress-done");
          } else {
            projectSb.append("left");
          }
          projectSb.append("\">\n");
          projectSb.append("                <span>Sample QCs</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
              + "            <li class=\"lib-prep-step\">\n");
          projectSb.append("<div class=\"");
          if (overview.getLibraryPreparationComplete() && overview.getAllLibrariesQcPassed()) {
            projectSb.append("mid-progress-done");
          } else if (overview.getLibraryPreparationComplete()) {
            projectSb.append("left-progress-done");
          } else {
            projectSb.append("");
          }
          projectSb.append("\">\n");
          projectSb.append("                <span>Libraries prepared</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
              + "            <li class=\"lib-qc-step\">\n");
          projectSb.append("<div class=\"");
          if (overview.getAllLibrariesQcPassed() && overview.getAllPoolsConstructed()) {
            projectSb.append("mid-progress-done");
          } else if (overview.getAllLibrariesQcPassed()) {
            projectSb.append("left-progress-done");
          } else {
            projectSb.append("");
          }
          projectSb.append("\">\n");
          projectSb.append("                <span>Library QCs</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
              + "            <li class=\"pools-step\">\n");
          projectSb.append("<div class=\"");
          if (overview.getAllPoolsConstructed() && overview.getAllRunsCompleted()) {
            projectSb.append("mid-progress-done");
          } else if (overview.getAllPoolsConstructed()) {
            projectSb.append("left-progress-done");
          } else {
            projectSb.append("");
          }
          projectSb.append("\">\n");
          projectSb.append("                <span>Pools Constructed</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
              + "            <li class=\"runs-step\">\n");
          projectSb.append("<div class=\"");
          if (overview.getAllRunsCompleted() && overview.getPrimaryAnalysisCompleted()) {
            projectSb.append("mid-progress-done");
          } else if (overview.getAllRunsCompleted()) {
            projectSb.append("left-progress-done");
          } else {
            projectSb.append("");
          }
          projectSb.append("\">\n");
          projectSb.append("                <span>Runs Completed</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
              + "            <li class=\"primary-analysis-step\">\n");
          projectSb.append("<div class=\"");
          if (overview.getPrimaryAnalysisCompleted()) {
            projectSb.append("right mid-progress-done");
          } else {
            projectSb.append("right");
          }
          projectSb.append("\">\n");
          projectSb.append("                <span>Primary Analysis</span>\n" + "              </div>\n" + "            </li>\n"
              + "          </ol></div>\n" + "          <p style=\"clear:both\"/>");
        }
      }
      Collection<Sample> samples = requestManager.listAllSamplesByProjectId(projectId);
      if (samples.size() > 0) {
        int sampleQCPassed = 0;
        for (Sample sample : samples) {
          Boolean passed = sample.getQcPassed();
          String passStr;
          if (passed == null) {
            passStr = "Unknown";
          } else if (passed) {
            passStr = passed.toString();
            sampleQCPassed++;
          } else {
            passStr = passed.toString();
          }
        }
        sampleQcSb.append("Sample QC Passed: " + sampleQCPassed + " out of " + samples.size() + ".<br/><br/>");
      }

      jsonObject.put("projectJson", projectSb.toString());
      jsonObject.put("sampleQcJson", sampleQcSb.toString());
      return jsonObject;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject listSamplesDataTable(HttpSession session, JSONObject json) {
    try {
      Long projectId = json.getLong("projectId");
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Sample sample : requestManager.listAllSamplesByProjectId(projectId)) {
        String sampleQubit = "not available";
        if (requestManager.listAllSampleQCsBySampleId(sample.getId()).size() > 0) {
          ArrayList<SampleQC> sampleQcList = new ArrayList<>(requestManager.listAllSampleQCsBySampleId(sample.getId()));
          SampleQC lastQc = sampleQcList.get(sampleQcList.size() - 1);
          sampleQubit = (lastQc.getResults() != null ? lastQc.getResults().toString() + " ng/µl" : "not available");
        }
        jsonArray.add("['" + (sample.getAlias() != null ? sample.getAlias() : "") + "','"
            + (sample.getSampleType() != null ? sample.getSampleType() : "") + "','"
            + (sample.getQcPassed() != null ? sample.getQcPassed().toString() : "") + "','" + sampleQubit + "','"
            + (sample.getReceivedDate() != null ? sample.getReceivedDate().toString() : "not available") + "']");

      }
      j.put("array", jsonArray);
      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject listRunsDataTable(HttpSession session, JSONObject json) {
    try {
      Long projectId = json.getLong("projectId");
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Run run : requestManager.listAllRunsByProjectId(projectId)) {
        if (!run.getStatus().getHealth().getKey().equals("Failed")) {

          StringBuilder sb = new StringBuilder();
          Collection<SequencerPartitionContainer> spcs = requestManager
              .listSequencerPartitionContainersByRunId(run.getId());
          if (spcs.size() > 0) {
            sb.append("<ul>");
            for (SequencerPartitionContainer spc : spcs) {

              if (spc.getPartitions().size() > 0) {
                for (Partition spp : spc.getPartitions()) {
                  if (spp.getPool() != null) {
                    if (spp.getPool().getPoolableElementViews().size() > 0) {
                      for (PoolableElementView dilution : spp.getPool().getPoolableElementViews()) {
                        if (dilution.getProjectId().equals(projectId)) {
                          sb.append("<li>");
                          sb.append(dilution.getSampleAlias());
                          sb.append("</li>");
                        }
                      }
                    }
                  }
                }
              }
            }
            sb.append("</ul>");
          }
          jsonArray.add("['" + run.getName() + "','"
              + (run.getStatus() != null && run.getStatus().getHealth() != null ? run.getStatus().getHealth().getKey() : "") + "','"
              + (run.getStatus() != null && run.getStatus().getStartDate() != null
                  ? LimsUtils.getDateAsString(run.getStatus().getStartDate()) : "")
              + "','"
              + (run.getStatus() != null && run.getStatus().getCompletionDate() != null
                  ? LimsUtils.getDateAsString(run.getStatus().getCompletionDate()) : "")
              + "','" + (run.getPlatformType() != null ? run.getPlatformType().getKey() : "") + "','" + sb.toString() + "']");

        }
      }
      j.put("array", jsonArray);
      return j;
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }
}
