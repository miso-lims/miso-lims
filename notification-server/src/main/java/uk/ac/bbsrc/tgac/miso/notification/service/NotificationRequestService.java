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

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.notification.exception.InvalidRequestParameterException;
import uk.ac.bbsrc.tgac.miso.notification.manager.NotificationRequestManager;
import uk.ac.bbsrc.tgac.miso.tools.run.RunFolderScanner;

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
    }
    else {
      throw new InvalidRequestParameterException("Incoming request must be of type 'query'");
    }
  }

  public String processRequest(Object request) {
    if (request instanceof JSONObject) {
      JSONObject j = (JSONObject)request;
      if (j.getString("query").toLowerCase().contains("status")) {
        return queryRunStatus(j);
      }
      else if (j.getString("query").toLowerCase().contains("info")) {
        return queryRunInfo(j);
      }
      else if (j.getString("query").toLowerCase().contains("parameters")) {
        return queryRunParameters(j);
      }
      else {
        return testService(j);
      }
    }
    return "{'error':'Unsupported operation'}";
  }

  private String queryRunStatus(JSONObject request) {
    return notificationRequestManager.queryRunStatus(request);
  }

  private String queryRunInfo(JSONObject request) {
    return notificationRequestManager.queryRunInfo(request);
  }

  private String queryRunParameters(JSONObject request) {
    return notificationRequestManager.queryRunParameters(request);
  }

  private String testService(JSONObject request) {
    return "{'TEST':'"+request.getString("query")+"'}";
  }

  public boolean validateQueryJSON(JSONObject json) {
    return (json.has("query") && (json.getString("query") != null && !"".equals(json.getString("query"))));
  }
}


