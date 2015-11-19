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

package uk.ac.bbsrc.tgac.miso.task.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.ebi.fgpt.conan.lsf.AbstractLSFProcess;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;

/**
 * uk.ac.bbsrc.tgac.miso.task.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 24-Jun-2011
 * @since 0.0.3
 */
@ServiceProvider
public class TestLsfTask extends AbstractLSFProcess {
  private final Collection<ConanParameter> parameters;

  private Logger log = LoggerFactory.getLogger(getClass());

  public TestLsfTask() {
    parameters = new ArrayList<ConanParameter>();
    parameters.add(new ConanParameter() {
      @Override
      public String getName() {
        return "foo";
      }

      @Override
      public String getDescription() {
        return "bar";
      }

      @Override
      public boolean isBoolean() {
        return false;
      }

      @Override
      public boolean validateParameterValue(String s) {
        return s != null;
      }
    });
  }

  @Override
  protected Logger getLog() {
    return log;
  }

  @Override
  public String getName() {
    return "Test";
  }

  @Override
  public Collection<ConanParameter> getParameters() {
    return parameters;
  }

  @Override
  protected String getComponentName() {
    return "TestComponent";
  }

  @Override
  protected String getCommand(Map<ConanParameter, String> parameters) {
    getLog().debug("Executing " + getName() + " with the following parameters: " + parameters.toString());
    return "grep -c \"#\" /etc/profile";
  }

  @Override
  protected String getLSFOutputFilePath(Map<ConanParameter, String> parameters) {
    // files to write output to
    final File outputDir = new File("/tmp", ".miso");

    // lsf output file
    return new File(outputDir, "test.lsfoutput.txt").getAbsolutePath();
  }
}
