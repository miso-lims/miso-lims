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

package uk.ac.bbsrc.tgac.miso.analysis.tgac.test.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.analysis.parameter.PathCreatingPathParameter;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;
import uk.ac.ebi.fgpt.conan.model.ConanProcess;
import uk.ac.ebi.fgpt.conan.service.exception.ProcessExecutionException;
import uk.ac.ebi.fgpt.conan.utils.CommandExecutionException;
import uk.ac.ebi.fgpt.conan.utils.ProcessRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.tgac
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14/10/11
 * @since 0.1.2
 */
public class LocalGunzipProcess implements ConanProcess {
  private Logger log = LoggerFactory.getLogger(getClass());

  private final Collection<ConanParameter> parameters;
  private final PathCreatingPathParameter fileParameter;

  public LocalGunzipProcess() {
    fileParameter = new PathCreatingPathParameter("file");

    parameters = new ArrayList<ConanParameter>();
    parameters.add(fileParameter);
  }

  protected Logger getLog() {
    return log;
  }

  @Override
  public boolean execute(Map<ConanParameter, String> parameters)
      throws ProcessExecutionException, IllegalArgumentException, InterruptedException {
    getLog().info("Executing " + getName() + " with the following parameters: " + parameters.toString());
    String command = "gunzip " + parameters.get(fileParameter);
    try {
      getLog().info("Issuing command: [" + command + "]");
      ProcessRunner runner = new ProcessRunner();
      runner.redirectStderr(true);
      String[] output = runner.runCommmand(command);
      if (output.length > 0) {
        getLog().info("Response from command [" + command + "]: " + output.length + " lines, first line was " + output[0]);
      }
    } catch (IOException e) {
      log.error("gunzip", e);
      return false;
    } catch (CommandExecutionException e) {
      log.error("gunzip", e);
      return false;
    }
    return true;
  }

  @Override
  public String getName() {
    return "local_gunzip";
  }

  @Override
  public Collection<ConanParameter> getParameters() {
    return parameters;
  }
}
