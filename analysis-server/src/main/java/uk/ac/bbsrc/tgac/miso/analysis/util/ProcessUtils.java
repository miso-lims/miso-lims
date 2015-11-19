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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Map;

import net.sf.json.JSONObject;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;
import uk.ac.ebi.fgpt.conan.model.ConanProcess;
import uk.ac.ebi.fgpt.conan.service.exception.MissingRequiredParameterException;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.util
 * <p/>
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27/10/11
 * @since version
 */
public class ProcessUtils {
  public static void extractConanParameters(Map<ConanParameter, String> parameters, Map<String, String> inputValues, ConanProcess process) {
    for (ConanParameter param : process.getParameters()) {
      // validate our request by checking we have this param value supplied
      if (inputValues.get(param.getName()) == null) {
        throw new MissingRequiredParameterException(
            "Required parameter '" + param.getName() + "' not supplied, " + "required for process '" + process.getName() + "'");
      } else {
        if (!parameters.containsKey(param)) {
          parameters.put(param, inputValues.get(param.getName()));
        }
      }
    }
  }

  public static boolean validateTaskRequestJSON(JSONObject json) {
    if (json.has("submit")) {
      JSONObject s = json.getJSONObject("submit");
      return (s.has("params") && s.has("priority") && s.has("pipeline"));
    }
    return false;
  }

  public static boolean validateQueryJSON(JSONObject json) {
    return (json.has("query") && isStringEmptyOrNull(json.getString("query")));
  }
}
