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

import java.util.Map;

import uk.ac.bbsrc.tgac.miso.analysis.parameter.Optionable;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;
import uk.ac.ebi.fgpt.conan.model.ConanProcess;
import uk.ac.ebi.fgpt.conan.service.DefaultTaskService;
import uk.ac.ebi.fgpt.conan.service.exception.MissingRequiredParameterException;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.service
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 28/03/12
 * @since 0.1.6
 */
public class AnalysisTaskService extends DefaultTaskService {

  @Override
  public void extractConanParameters(Map<ConanParameter, String> parameters, Map<String, String> inputValues, ConanProcess process) {
    for (ConanParameter param : process.getParameters()) {
      if ((param instanceof Optionable && ((Optionable) param).isOptional()) || inputValues.get(param.getName()) != null) {
        if (!parameters.containsKey(param)) {
          parameters.put(param, inputValues.get(param.getName()));
        }
      } else {
        throw new MissingRequiredParameterException(
            "Required parameter '" + param.getName() + "' not supplied, " + "required for process '" + process.getName() + "'");
      }
    }

    /*
     * //finally, map in any parameters that need to be used, but not saved List<String> paramnames = new ArrayList<String>(); for
     * (ConanParameter cp : parameters.keySet()) { paramnames.add(cp.getName()); }
     * 
     * for (String transientParam : inputValues.keySet()) { DefaultProcessParameter dpp = new DefaultProcessParameter(transientParam); if
     * (!paramnames.contains(transientParam)) { getLog().info("Adding "+transientParam); parameters.put(dpp,
     * inputValues.get(transientParam)); } }
     */
  }
}