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

package uk.ac.bbsrc.tgac.miso.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationUtils;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service.task
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 09/11/11
 * @since 0.1.3
 */
public class AnalysisQueryService {
  protected static final Logger log = LoggerFactory.getLogger(AnalysisQueryService.class);

  private String analysisServerHost;
  private int analysisServerPort;

  public void setAnalysisServerHost(String analysisServerHost) {
    this.analysisServerHost = analysisServerHost;
  }

  public void setAnalysisServerPort(int analysisServerPort) {
    this.analysisServerPort = analysisServerPort;
  }

  public JSONArray getTask(String taskId) throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getTask");
    JSONObject params = new JSONObject();
    params.put("name", taskId);
    q1.put("params", params);
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONArray r = JSONArray.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.getJSONObject(0).has("error")) {
          String error = r.getJSONObject(0).getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        }

        return JSONArray.fromObject(response);
      }
    }
    throw new IntegrationException("No such task.");
  }

  public JSONArray getTasks() throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getTasks");
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONArray r = JSONArray.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.getJSONObject(0).has("error")) {
          String error = r.getJSONObject(0).getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        }

        return JSONArray.fromObject(response);
      }
    }
    return JSONArray.fromObject("[]");
  }

  public JSONArray getPendingTasks() throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getPendingTasks");
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONArray r = JSONArray.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.getJSONObject(0).has("error")) {
          String error = r.getJSONObject(0).getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        } else {
          JSONArray n = new JSONArray();
          for (JSONObject task : (Iterable<JSONObject>) r) {
            if (!task.getString("statusMessage").contains("Failed")) {
              n.add(task);
            }
          }
          return n;
        }
      }
    }
    return JSONArray.fromObject("[]");
  }

  public JSONArray getFailedTasks() throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getPendingTasks");
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONArray r = JSONArray.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.getJSONObject(0).has("error")) {
          String error = r.getJSONObject(0).getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        } else {
          JSONArray n = new JSONArray();
          for (JSONObject task : (Iterable<JSONObject>) r) {
            if (task.getString("statusMessage").contains("Failed")) {
              n.add(task);
            }
          }
          return n;
        }
      }
    }
    return JSONArray.fromObject("[]");
  }

  public JSONArray getRunningTasks() throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getRunningTasks");
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONArray r = JSONArray.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.getJSONObject(0).has("error")) {
          String error = r.getJSONObject(0).getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        }

        return JSONArray.fromObject(response);
      }
    }
    return JSONArray.fromObject("[]");
  }

  public JSONArray getCompletedTasks() throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getCompletedTasks");
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONArray r = JSONArray.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.getJSONObject(0).has("error")) {
          String error = r.getJSONObject(0).getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        }

        return JSONArray.fromObject(response);
      }
    }
    return JSONArray.fromObject("[]");
  }

  public JSONObject getPipeline(String pipelineName) throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getPipeline");
    JSONObject params = new JSONObject();
    params.put("name", pipelineName);
    q1.put("params", params);
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONObject r = JSONObject.fromObject(response);
      if (!r.isEmpty()) {
        if (r.has("error")) {
          String error = r.getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        }

        return r;
      }
    }
    throw new IntegrationException("No such pipeline.");
  }

  public JSONArray getPipelines() throws IntegrationException {
    JSONObject q1 = new JSONObject();
    q1.put("query", "getPipelines");
    String query = q1.toString();

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), query);
    if (!"".equals(response)) {
      JSONArray r = JSONArray.fromObject(response);
      if (!r.isEmpty()) {
        if (r.size() == 1 && r.getJSONObject(0).has("error")) {
          String error = r.getJSONObject(0).getString("error");
          log.error(error);
          throw new IntegrationException("Analysis query returned an error: " + error);
        }

        return JSONArray.fromObject(response);
      }
    }
    return JSONArray.fromObject("[]");
  }

  public JSONObject submitTask(JSONObject json) throws IntegrationException {
    JSONObject task = new JSONObject();

    JSONObject j = new JSONObject();

    if (json.has("priority")) {
      j.put("priority", json.get("priority"));
    } else {
      j.put("priority", "MEDIUM");
    }

    j.put("pipeline", json.get("pipeline"));
    j.put("params", json);

    task.put("submit", j);

    String response = IntegrationUtils.sendMessage(IntegrationUtils.prepareSocket(analysisServerHost, analysisServerPort), task.toString());
    if (!"".equals(response)) {
      JSONObject r = JSONObject.fromObject(response);
      if (r.has("error")) {
        String error = r.getString("error");
        log.error(error);
        throw new IntegrationException("Analysis query returned an error: " + error);
      }
      return r;
    }
    throw new IntegrationException("Cannot submit task.");
  }
}
