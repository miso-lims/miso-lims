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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import com.eaglegenomics.simlims.core.manager.*;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.manager.*;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bianx
 * Date: 02/11/11
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class ExternalSectionControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(DashboardHelperService.class);
  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;
  @Autowired
  private uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager;


  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

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

      if (projectCollection == null) {
        b.append("You have no project.");
      }
      else {
        List<Project> projects = new ArrayList<Project>(projectCollection);
        Collections.sort(projects);
        for (Project p : projects) {
          b.append("<a class=\"dashboardresult\" onclick=\"showProjectStatus(" + p.getProjectId() + ");\" href=\"javascript:void(0);\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
          b.append("Name: <b>" + p.getName() + "</b><br/>");
          b.append("Alias: <b>" + p.getAlias() + "</b><br/>");
          b.append("</div></a>");
        }
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject projectStatus(HttpSession session, JSONObject json) {
    try {
      Long projectId = json.getLong("projectId");
      StringBuilder sb = new StringBuilder();
      Project project = requestManager.getProjectById(projectId);
      sb.append("<div class='report'>");
      sb.append("<b>Project Name: </b> " + project.getName());
      sb.append("<br/><br/>");
      sb.append("<b>Project Alias: </b> " + project.getAlias());
      sb.append("<br/><br/>");
      sb.append("<b>Project Description: </b> " + project.getDescription());
      sb.append("<br/><br/>");
      sb.append("<b>Progress: </b> " + project.getProgress().name());
      sb.append("<br/><br/>");
      if (project.getOverviews().size() > 0) {
        for (ProjectOverview overview : project.getOverviews()) {
          sb.append("<div><ol id=\"progress\">\n" +
                    "            <li class=\"sample-qc-step\">\n");
          sb.append("<div class=\"");
          if (overview.getAllSampleQcPassed() && overview.getLibraryPreparationComplete()) {
            sb.append("left mid-progress-done");
          }
          else if (overview.getAllSampleQcPassed()) {
            sb.append("left-progress-done");
          }
          else {
            sb.append("left");
          }
          sb.append("\">\n");
          sb.append("                <span>Sample QCs</span>\n" +
                    "              </div>\n" +
                    "            </li>\n" +
                    "\n" +
                    "            <li class=\"lib-prep-step\">\n");
          sb.append("<div class=\"");
          if (overview.getLibraryPreparationComplete() && overview.getAllLibrariesQcPassed()) {
            sb.append("mid-progress-done");
          }
          else if (overview.getLibraryPreparationComplete()) {
            sb.append("left-progress-done");
          }
          else {
            sb.append("");
          }
          sb.append("\">\n");
          sb.append("                <span>Libraries prepared</span>\n" +
                    "              </div>\n" +
                    "            </li>\n" +
                    "\n" +
                    "            <li class=\"lib-qc-step\">\n");
          sb.append("<div class=\"");
          if (overview.getAllLibrariesQcPassed() && overview.getAllPoolsConstructed()) {
            sb.append("mid-progress-done");
          }
          else if (overview.getAllLibrariesQcPassed()) {
            sb.append("left-progress-done");
          }
          else {
            sb.append("");
          }
          sb.append("\">\n");
          sb.append("                <span>Library QCs</span>\n" +
                    "              </div>\n" +
                    "            </li>\n" +
                    "\n" +
                    "            <li class=\"pools-step\">\n");
          sb.append("<div class=\"");
          if (overview.getAllPoolsConstructed() && overview.getAllRunsCompleted()) {
            sb.append("mid-progress-done");
          }
          else if (overview.getAllPoolsConstructed()) {
            sb.append("left-progress-done");
          }
          else {
            sb.append("");
          }
          sb.append("\">\n");
          sb.append("                <span>Pools Constructed</span>\n" +
                    "              </div>\n" +
                    "            </li>\n" +
                    "\n" +
                    "            <li class=\"runs-step\">\n");
          sb.append("<div class=\"");
          if (overview.getAllRunsCompleted() && overview.getPrimaryAnalysisCompleted()) {
            sb.append("mid-progress-done");
          }
          else if (overview.getAllRunsCompleted()) {
            sb.append("left-progress-done");
          }
          else {
            sb.append("");
          }
          sb.append("\">\n");
          sb.append("                <span>Runs Completed</span>\n" +
                    "              </div>\n" +
                    "            </li>\n" +
                    "\n" +
                    "            <li class=\"primary-analysis-step\">\n");
          sb.append("<div class=\"");
          if (overview.getPrimaryAnalysisCompleted()) {
            sb.append("right mid-progress-done");
          }
          else {
            sb.append("right");
          }
          sb.append("\">\n");
          sb.append("                <span>Primary Analysis</span>\n" +
                    "              </div>\n" +
                    "            </li>\n" +
                    "          </ol></div>\n" +
                    "          <p style=\"clear:both\"/>");
        }
      }
      Collection<Sample> samples = requestManager.listAllSamplesByProjectId(projectId);
      if (samples.size() > 0) {
        int sampleQCPassed = 0;
        sb.append("<table class=\"list\">\n" +
                  "            <thead>\n" +
                  "            <tr>\n" +
                  "                <th>Sample Name</th>\n" +
                  "                <th>Sample Alias</th>\n" +
                  "                <th>Type</th>\n" +
                  "                <th>QC Passed</th>\n" +
                  "            </tr>\n" +
                  "            </thead>\n" +
                  "            <tbody>");
        for (Sample sample : samples) {
          sb.append("<tr>\n" +
                    "                    <td><b>" + sample.getName() + "</b></td>\n" +
                    "                    <td>" + sample.getAlias() + "</td>\n" +
                    "                    <td>" + sample.getSampleType() + "</td>\n" +
                    "                    <td>" + sample.getQcPassed().toString() + "</td>\n" +
                    "                </tr>");
          if (sample.getQcPassed()) {
            sampleQCPassed++;
          }
        }
        sb.append("</tbody></table>");
        sb.append("Sample QC Passed: " + sampleQCPassed + " out of " + samples.size() + ".<br/><br/>");
      }

      else {
        sb.append("<b>Sample:</b> None.<br/><br/>");
      }

      Collection<Library> libraries = requestManager.listAllLibrariesByProjectId(projectId);
      if (libraries.size() > 0) {
        int libraryQCPassed = 0;
        sb.append("<table class=\"list\">\n" +
                  "            <thead>\n" +
                  "            <tr>\n" +
                  "                <th>Library Name</th>\n" +
                  "                <th>Library Alias</th>\n" +
                  "                <th>Type</th>\n" +
                  "                <th>QC</th>\n" +
                  "            </tr>\n" +
                  "            </thead>\n" +
                  "            <tbody>");
        for (Library library : libraries) {
          sb.append("<tr>\n" +
                    "                    <td><b>" + library.getName() + "</b></td>\n" +
                    "                    <td>" + library.getAlias() + "</td>\n" +
                    "                    <td>" + library.getLibraryType() + "</td>\n" +
                    "                    <td>" + library.getQcPassed().toString() + "</td>\n" +
                    "                </tr>");
          if (library.getQcPassed()) {
            libraryQCPassed++;
          }
        }
        sb.append("</tbody>\n" +
                  "        </table>");
        sb.append("Library QC Passed: " + libraryQCPassed + " out of " + libraries.size() + ".<br/><br/>");
      }

      else {
        sb.append("<b>Library:</b> None.<br/><br/>");
      }


      Collection<Run> runs = requestManager.listAllRunsByProjectId(projectId);
      if (runs.size() > 0) {
        int runQCPassed = 0;
        sb.append("<table class=\"list\">\n" +
                  "            <thead>\n" +
                  "            <tr>\n" +
                  "                <th>Run Name</th>\n" +
                  "                <th>Run Alias</th>\n" +
                  "                <th>Status</th>\n" +
                  "            </tr>\n" +
                  "            </thead>\n" +
                  "            <tbody>");
        for (Run run : runs) {
          sb.append("<tr>\n" +
                    "                    <td><b>" + run.getName() + "</b></td>\n" +
                    "                    <td>" + run.getAlias() + "</td>\n" +
                    "                    <td>" + run.getStatus().getHealth().getKey() + "</td>\n" +
                    "                </tr>");
          if (run.getRunQCs().size() > 0) {
            runQCPassed++;
          }
        }
        sb.append("</tbody>\n" +
                  "        </table>");
        sb.append("Run QC Passed: " + runQCPassed + " out of " + runs.size() + ".<br/><br/>");
      }

      else {
        sb.append("<b>Run:</b> None.");
      }

      return JSONUtils.JSONObjectResponse("html", sb.toString());
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }
}
