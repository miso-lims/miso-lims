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

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.integration.AnalysisQueryService;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 15/11/11
 * @since 0.1.3
 */
@Ajaxified
public class TaskControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(TaskControllerHelperService.class);

  @Autowired
  private AnalysisQueryService analysisQueryService;

  public AnalysisQueryService getAnalysisQueryService() {
    return analysisQueryService;
  }

  public void setAnalysisQueryService(AnalysisQueryService analysisQueryService) {
    this.analysisQueryService = analysisQueryService;
  }

  public JSONObject getPipeline(HttpSession session, JSONObject json) {
    try {
      if (json.has("pipeline")) {
        JSONObject j = new JSONObject();
        j.put("pipeline", getAnalysisQueryService().getPipeline(json.getString("pipeline")));
        return j;
      }
      return JSONUtils.SimpleJSONError("No pipeline name specified");
    } catch (IntegrationException e) {
      return JSONUtils.SimpleJSONError("Cannot populate pipeline: " + e.getMessage());
    }
  }

  public JSONObject populateRunningTasks(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      j.put("runningTasks", getAnalysisQueryService().getRunningTasks());
      return j;
    } catch (IntegrationException e) {
      return JSONUtils.SimpleJSONError("Cannot populate running tasks: " + e.getMessage());
    }
  }

  public JSONObject populatePendingTasks(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      j.put("pendingTasks", getAnalysisQueryService().getPendingTasks());
      return j;
    } catch (IntegrationException e) {
      return JSONUtils.SimpleJSONError("Cannot populate pending tasks: " + e.getMessage());
    }
  }

  public JSONObject populateFailedTasks(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      j.put("failedTasks", getAnalysisQueryService().getFailedTasks());
      return j;
    } catch (IntegrationException e) {
      return JSONUtils.SimpleJSONError("Cannot populate running tasks: " + e.getMessage());
    }
  }

  public JSONObject populateCompletedTasks(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      j.put("completedTasks", getAnalysisQueryService().getCompletedTasks());
      return j;
    } catch (IntegrationException e) {
      return JSONUtils.SimpleJSONError("Cannot populate completed tasks: " + e.getMessage());
    }
  }

  public JSONObject populatePipelines(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      j.put("pipelines", getAnalysisQueryService().getPipelines());
      return j;
    } catch (IntegrationException e) {
      return JSONUtils.SimpleJSONError("Cannot populate pipelines: " + e.getMessage());
    }
  }

  public JSONObject submitJob(HttpSession session, JSONObject json) {
    try {
      JSONObject out = new JSONObject();
      JSONArray a = JSONArray.fromObject(json.getString("submit"));
      for (JSONObject j : (Iterable<JSONObject>) a) {
        out.put(j.getString("name"), j.getString("value"));
      }
      log.info("Submitting: " + out.toString());
      return getAnalysisQueryService().submitTask(out);
    } catch (IntegrationException e) {
      return JSONUtils.SimpleJSONError("Cannot populate pipelines: " + e.getMessage());
    }
  }
}
