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

package uk.ac.bbsrc.tgac.miso.analysis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.analysis.manager.AnalysisRequestManager;
import uk.ac.bbsrc.tgac.miso.analysis.submission.PipelineRequest;
import uk.ac.bbsrc.tgac.miso.analysis.submission.TaskSubmissionRequest;
import uk.ac.ebi.fgpt.conan.service.exception.SubmissionException;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.service
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 02/11/11
 * @since 0.1.3
 */
public class AnalysisRequestService {
  protected static final Logger log = LoggerFactory.getLogger(AnalysisRequestService.class);

  @Autowired
  AnalysisRequestManager analysisRequestManager;

  public void setAnalysisRequestManager(AnalysisRequestManager analysisRequestManager) {
    this.analysisRequestManager = analysisRequestManager;
  }

  public String processRequest(Object request) {
    if (request instanceof TaskSubmissionRequest) {
      return submitAnalysisTask((TaskSubmissionRequest) request);
    } else if (request instanceof PipelineRequest) {
      return submitAnalysisPipeline((PipelineRequest) request);
    } else if (request instanceof JSONObject) {
      JSONObject j = (JSONObject) request;
      if (j.getString("query").toLowerCase().contains("task")) {
        return queryTasks(j);
      } else if (j.getString("query").toLowerCase().contains("pipeline")) {
        return queryPipelines(j);
      }
    }
    return "{'error':'Unsupported operation'}";
  }

  private String submitAnalysisTask(TaskSubmissionRequest request) {
    try {
      analysisRequestManager.generateAndSubmitTask(request);
      return "{'response':'Task submitted: " + request.getPipelineName() + "'}";
    } catch (SubmissionException e) {
      log.error("submit analysis task", e);
      return "{'error':'Task not submitted: " + e.getMessage() + "'}";
    }
  }

  private String submitAnalysisPipeline(PipelineRequest request) {
    analysisRequestManager.generateAndAddPipeline(request);
    return "{'response':'Pipeline  submitted: " + request.getName() + "'}";
  }

  private String queryTasks(JSONObject request) {
    return analysisRequestManager.queryTasks(request);
  }

  private String queryPipelines(JSONObject request) {
    return analysisRequestManager.queryPipelines(request);
  }
}
