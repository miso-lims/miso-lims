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

package uk.ac.bbsrc.tgac.miso.analysis.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.analysis.parameter.FlagParameter;
import uk.ac.bbsrc.tgac.miso.analysis.parameter.PathCreatingPathParameter;
import uk.ac.ebi.fgpt.conan.lsf.LSFProcess;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.tgac
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 14/10/11
 * @since 0.1.2
 */
@ServiceProvider
public class LsfGzipProcess extends AbstractTgacLsfProcess {
  private Logger log = LoggerFactory.getLogger(getClass());

  private final Collection<ConanParameter> parameters;
  private final PathCreatingPathParameter fileParameter;
  private final FlagParameter compressionParameter;

  public LsfGzipProcess() {
    setQueueName("lsf_testing");

    // set this to your own bsub path!
    setBsubPath("/path/to/bsub");

    fileParameter = new PathCreatingPathParameter("file");
    compressionParameter = new FlagParameter("compression");

    parameters = new ArrayList<ConanParameter>();
    parameters.add(fileParameter);
    parameters.add(compressionParameter);
  }

  @Override
  protected Logger getLog() {
    return log;
  }

  @Override
  protected String getComponentName() {
    return LSFProcess.UNSPECIFIED_COMPONENT_NAME;
  }

  @Override
  protected String getCommand(Map<ConanParameter, String> parameters) throws IllegalArgumentException {
    getLog().debug("Executing " + getName() + " with the following parameters: " + parameters.toString());
    return "gzip -" + parameters.get(compressionParameter) + " " + parameters.get(fileParameter);
  }

  @Override
  public String getName() {
    return "lsf_gzip";
  }

  @Override
  public Collection<ConanParameter> getParameters() {
    return parameters;
  }
}
