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

package uk.ac.bbsrc.tgac.miso.analysis.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.analysis.exception.InvalidRequestParameterException;
import uk.ac.bbsrc.tgac.miso.analysis.submission.TaskSubmissionRequest;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03/11/11
 * @since 0.1.3
 */
public class PayloadTransformer {
  protected static final Logger log = LoggerFactory.getLogger(PayloadTransformer.class);

  public Object transform(byte[] payload) throws InvalidRequestParameterException {
    String s = new String(payload);
    JSONObject j = JSONObject.fromObject(s);

    if (j.has("submit")) {
      return generateTaskSubmissionRequest(j);
    } else if (j.has("query")) {
      return generateQuery(j);
    } else {
      throw new InvalidRequestParameterException("Incoming request must be of type 'submit', 'tasks', or 'pipelines'");
    }
  }

  private TaskSubmissionRequest generateTaskSubmissionRequest(JSONObject j) throws InvalidRequestParameterException {
    if (ProcessUtils.validateTaskRequestJSON(j)) {
      JSONObject s = j.getJSONObject("submit");
      Map<String, String> params = new HashMap<String, String>();
      JSONObject jparams = s.getJSONObject("params");
      for (Object key : jparams.keySet()) {
        String k = (String) key;
        params.put(k, jparams.getString(k));
      }
      return new TaskSubmissionRequest(s.getString("priority"), s.getString("pipeline"), params);
    } else {
      throw new InvalidRequestParameterException(
          "Invalid parameters for task creation. Cannot create task submission request: " + j.toString());
    }
  }

  private JSONObject generateQuery(JSONObject j) throws InvalidRequestParameterException {
    if (ProcessUtils.validateQueryJSON(j)) {
      return j;
    } else {
      throw new InvalidRequestParameterException("Invalid query. Cannot perform query: " + j.toString());
    }
  }
}
