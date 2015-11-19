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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.fgpt.conan.model.ConanParameter;
import uk.ac.ebi.fgpt.conan.model.ConanProcess;
import uk.ac.ebi.fgpt.conan.service.exception.ProcessExecutionException;

/**
 * uk.ac.bbsrc.tgac.miso.task.lsf
 * <p/>
 * Test LSF process task
 * 
 * @author Rob Davey
 * @date 23-Jun-2011
 * @since 0.0.3
 */
public class TestLsfProcess implements ConanProcess {
  protected static final Logger log = LoggerFactory.getLogger(TestLsfProcess.class);

  public static void main(String[] args) {
    log.info("Setting up TestLsfProcess");
    TestLsfProcess tlsfp = new TestLsfProcess();
    try {
      Map<ConanParameter, String> testParams = new HashMap<ConanParameter, String>();
      testParams.put(new ConanParameter() {
        @Override
        public String getName() {
          return "foo";
        }

        @Override
        public String getDescription() {
          return "foo";
        }

        @Override
        public boolean isBoolean() {
          return false;
        }

        @Override
        public boolean validateParameterValue(String s) {
          return s != null;
        }
      }, "foo");

      testParams.put(new ConanParameter() {
        @Override
        public String getName() {
          return "bar";
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
      }, "bar");

      log.info("Parameters set. Executing...");
      boolean foo = tlsfp.execute(testParams);
      log.info("Result: " + foo);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ProcessExecutionException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean execute(Map<ConanParameter, String> parameters)
      throws IllegalArgumentException, ProcessExecutionException, InterruptedException {
    return new TestLsfTask().execute(parameters);
  }

  @Override
  public String getName() {
    return "TestLsfProcess";
  }

  @Override
  public Collection<ConanParameter> getParameters() {
    return new ArrayList<ConanParameter>();
  }
}
