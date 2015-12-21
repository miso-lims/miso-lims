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

package uk.ac.bbsrc.tgac.miso.notification.service;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.notification.exception.InvalidRequestParameterException;
import uk.ac.bbsrc.tgac.miso.notification.manager.NotificationRequestManager;

/**
 * uk.ac.bbsrc.tgac.miso.notification.service
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 15/04/13
 * @since 0.2.0
 */
public class NotificationRequestService {
  protected static final Logger log = LoggerFactory.getLogger(NotificationRequestService.class);

  @Autowired
  NotificationRequestManager notificationRequestManager;

  public void setNotificationRequestManager(NotificationRequestManager notificationRequestManager) {
    this.notificationRequestManager = notificationRequestManager;
  }

  public Object transformPayload(byte[] payload) throws InvalidRequestParameterException {
    String s = new String(payload);
    JSONObject j = JSONObject.fromObject(s);

    if (j.has("query") && validateQueryJSON(j)) {
      return j;
    } else {
      throw new InvalidRequestParameterException("Incoming request must be of type 'query'");
    }
  }

  public String processRequest(Object request) {
    if (request instanceof JSONObject) {
      JSONObject j = (JSONObject) request;
      if (j.getString("query").toLowerCase().contains("progress")) {
        return queryRunProgress(j);
      }
      if (j.getString("query").toLowerCase().contains("status")) {
        return queryRunStatus(j);
      } else if (j.getString("query").toLowerCase().contains("info")) {
        return queryRunInfo(j);
      } else if (j.getString("query").toLowerCase().contains("parameters")) {
        return queryRunParameters(j);
      } else if (j.getString("query").toLowerCase().contains("interop")) {
        return queryInterOpMetrics(j);
      } else {
        return testService(j);
      }
    }
    return "{'error':'Unsupported operation'}";
  }

  private String queryRunProgress(JSONObject request) {
    try {
      return notificationRequestManager.queryRunProgress(request);
    } catch (Exception ise) {
      log.error("cannot retrieve run progress", ise);
      return "{\"error\":\"Cannot retrieve run status: " + ise.getMessage() + "\"}";
    }
  }

  private String queryRunStatus(JSONObject request) {
    try {
      return notificationRequestManager.queryRunStatus(request);
    } catch (Exception ise) {
      log.error("cannot retrieve run status", ise);
      return "{\"error\":\"Cannot retrieve run status: " + ise.getMessage() + "\"}";
    }
  }

  private String queryRunInfo(JSONObject request) {
    try {
      return notificationRequestManager.queryRunInfo(request);
    } catch (Exception ise) {
      log.error("cannot retrieve run info", ise);
      return "{\"error\":\"Cannot retrieve run information: " + ise.getMessage() + "\"}";
    }
  }

  private String queryRunParameters(JSONObject request) {
    try {
      return notificationRequestManager.queryRunParameters(request);
    } catch (Exception ise) {
      log.error("cannot retrieve run parameters", ise);
      return "{\"error\":\"Cannot retrieve run parameters: " + ise.getMessage() + "\"}";
    }
  }

  private String queryInterOpMetrics(JSONObject request) {
    try {
      return notificationRequestManager.queryInterOpMetrics(request);
    } catch (Exception ise) {
      log.error("cannot retrieve run interop metrics", ise);
      return "{\"error\":\"Cannot retrieve InterOp metrics: " + ise.getMessage() + "\"}";
    }
  }

  private String testService(JSONObject request) {
    log.warn("Cannot find service action for " + request.getString("query") + ". Has a service method been defined?");
    return "{'TEST':'" + request.getString("query") + "'}";
  }

  public boolean validateQueryJSON(JSONObject json) {
    return (json.has("query") && (!isStringEmptyOrNull(json.getString("query"))));
  }
}
