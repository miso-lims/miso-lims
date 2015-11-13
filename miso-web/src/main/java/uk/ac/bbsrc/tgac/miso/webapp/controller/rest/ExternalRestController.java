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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller.rest
 * <p/>
 * Info
 * 
 * @author bianx
 */
@Controller
@RequestMapping("/rest/external")
@SessionAttributes("external")
public class ExternalRestController {
  protected static final Logger log = LoggerFactory.getLogger(ExternalRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "projects", method = RequestMethod.GET)
  public @ResponseBody String jsonRest() throws IOException {
    StringBuilder sb = new StringBuilder();
    Collection<Project> lp = requestManager.listAllProjects();
    int pi = 0;
    for (Project p : lp) {
      pi++;
      sb.append(jsonRestProjectList(p.getProjectId()));
      if (pi < lp.size()) {
        sb.append(",");
      }
    }
    return "[" + sb.toString() + "]";
  }

  public String jsonRestProjectList(Long projectId) throws IOException {
    StringBuilder sb = new StringBuilder();

    Project p = requestManager.getProjectById(projectId);
    sb.append("'id':'" + projectId + "'");
    sb.append(",");
    sb.append("'name':'" + p.getName() + "'");
    sb.append(",");
    sb.append("'alias':'" + p.getAlias() + "'");

    return "{" + sb.toString() + "}";
  }

  @RequestMapping(value = "project/{projectId}", method = RequestMethod.GET)
  public @ResponseBody String jsonRestProject(@PathVariable Long projectId, ModelMap model) throws IOException {
    StringBuilder sb = new StringBuilder();

    Project p = requestManager.getProjectById(projectId);
    sb.append("'id':'" + projectId + "'");
    sb.append(",");
    sb.append("'name':'" + p.getName() + "'");
    sb.append(",");
    sb.append("'alias':'" + p.getAlias() + "'");
    sb.append(",");
    sb.append("'progress':'" + p.getProgress().name() + "'");
    sb.append(",");
    sb.append("'description':'" + p.getDescription() + "'");
    sb.append(",");

    sb.append("'overviews':[");
    if (p.getOverviews().size() > 0) {
      int oi = 0;
      for (ProjectOverview overview : p.getOverviews()) {
        oi++;
        sb.append("{");
        sb.append("'allSampleQcPassed':" + overview.getAllSampleQcPassed());
        sb.append(",");
        sb.append("'libraryPreparationComplete':" + overview.getLibraryPreparationComplete());
        sb.append(",");
        sb.append("'allLibrariesQcPassed':" + overview.getAllLibrariesQcPassed());
        sb.append(",");
        sb.append("'allPoolsConstructed':" + overview.getAllPoolsConstructed());
        sb.append(",");
        sb.append("'allRunsCompleted':" + overview.getAllRunsCompleted());
        sb.append(",");
        sb.append("'primaryAnalysisCompleted':" + overview.getPrimaryAnalysisCompleted());
        sb.append("}");
        if (oi < p.getOverviews().size()) {
          sb.append(",");
        }
      }
    }
    sb.append("]");
    sb.append(",");

    sb.append("'samples':[");
    Collection<Sample> samples = requestManager.listAllSamplesByProjectId(projectId);
    if (samples.size() > 0) {
      int si = 0;
      for (Sample sample : samples) {
        si++;
        String sampleQubit = "not available";
        if (requestManager.listAllSampleQCsBySampleId(sample.getId()).size() > 0) {
          ArrayList<SampleQC> sampleQcList = new ArrayList(requestManager.listAllSampleQCsBySampleId(sample.getId()));
          SampleQC lastQc = sampleQcList.get(sampleQcList.size() - 1);
          sampleQubit = (lastQc.getResults() != null ? lastQc.getResults().toString() : "");
        }
        sb.append("{");
        sb.append("'alias':'" + sample.getAlias() + "'");
        sb.append(",");
        sb.append("'qcPassed':'" + (sample.getQcPassed() != null ? sample.getQcPassed().toString() : "") + "'");
        sb.append(",");

        sb.append("'receivedDate':'"
            + (sample.getReceivedDate() != null ? LimsUtils.getDateAsString(sample.getReceivedDate()) : "not available") + "'");
        sb.append(",");
        sb.append("'sampleType':'" + (sample.getSampleType() != null ? sample.getSampleType() : "") + "'");
        sb.append(",");
        sb.append("'sampleQubit':'" + sampleQubit + "'");
        sb.append("}");

        if (si < samples.size()) {
          sb.append(",");
        }

      }
    }
    sb.append("]");
    sb.append(",");

    sb.append("'runs':[");
    Collection<Run> runs = requestManager.listAllRunsByProjectId(projectId);
    if (runs.size() > 0) {
      int ri = 0;
      for (Run run : runs) {
        ri++;
        if (!run.getStatus().getHealth().getKey().equals("Failed")) {
          ArrayList<String> runSamples = new ArrayList();
          Collection<SequencerPartitionContainer<SequencerPoolPartition>> spcs = requestManager
              .listSequencerPartitionContainersByRunId(run.getId());
          if (spcs.size() > 0) {
            for (SequencerPartitionContainer<SequencerPoolPartition> spc : spcs) {

              if (spc.getPartitions().size() > 0) {
                for (SequencerPoolPartition spp : spc.getPartitions()) {
                  if (spp.getPool() != null) {
                    if (spp.getPool().getDilutions().size() > 0) {
                      for (Dilution dilution : spp.getPool().getDilutions()) {
                        Sample sample = dilution.getLibrary().getSample();
                        if (sample.getProject().equals(p)) {
                          runSamples.add(sample.getAlias());
                        }
                      }
                    }
                  }
                }
              }
            }
          }

          sb.append("{");
          sb.append("'name':'" + run.getName() + "'");
          sb.append(",");
          sb.append("'status':'"
              + (run.getStatus() != null && run.getStatus().getHealth() != null ? run.getStatus().getHealth().getKey() : "") + "'");
          sb.append(",");
          sb.append("'startDate':'"
              + (run.getStatus() != null && run.getStatus().getStartDate() != null ? run.getStatus().getStartDate().toString() : "") + "'");
          sb.append(",");
          sb.append("'completionDate':'" + (run.getStatus() != null && run.getStatus().getCompletionDate() != null
              ? run.getStatus().getCompletionDate().toString() : "") + "'");
          sb.append(",");
          sb.append("'platformType':'" + (run.getPlatformType() != null ? run.getPlatformType().getKey() : "") + "'");
          sb.append(",");
          sb.append("'samples':[");
          if (runSamples.size() > 0) {
            int rsi = 0;
            for (String alias : runSamples) {
              rsi++;
              sb.append("{'sampleAlias':'" + alias + "'}");
              if (rsi < runSamples.size()) {
                sb.append(",");
              }
            }

          }
          sb.append("]");
          sb.append("}");
          if (ri < runs.size()) {
            sb.append(",");
          }
        }
      }
    }
    sb.append("]");

    return "{" + sb.toString() + "}";
  }
}
