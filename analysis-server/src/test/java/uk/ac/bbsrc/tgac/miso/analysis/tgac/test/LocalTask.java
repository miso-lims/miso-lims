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

package uk.ac.bbsrc.tgac.miso.analysis.tgac.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.analysis.tgac.test.process.LocalGunzipProcess;
import uk.ac.bbsrc.tgac.miso.analysis.tgac.test.process.LocalGzipProcess;
import uk.ac.bbsrc.tgac.miso.analysis.util.ProcessUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.ebi.fgpt.conan.model.ConanParameter;
import uk.ac.ebi.fgpt.conan.model.ConanProcess;
import uk.ac.ebi.fgpt.conan.service.exception.ProcessExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * uk.ac.bbsrc.tgac.miso.analysis.tgac
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27/10/11
 * @since 0.1.2
 */
public class LocalTask extends TestCase {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  private static File testFile = null;

  static {
    try {
      testFile = File.createTempFile("sequence", ".txt");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGzip() {
    try {
      InputStream in = LocalTask.class.getResourceAsStream("/sequence.txt");
      LimsUtils.writeFile(in, testFile);
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }

    ConanProcess process = new LocalGzipProcess();
    Map<ConanParameter, String> processParams = new HashMap<ConanParameter, String>();
    Map<String, String> inputParams = new HashMap<String, String>();
    inputParams.put("compression", "9");
    inputParams.put("file", testFile.getAbsolutePath());
    ProcessUtils.extractConanParameters(processParams, inputParams, process);

    try {
      process.execute(processParams);
    } catch (ProcessExecutionException e) {
      e.printStackTrace();
      fail();
    } catch (InterruptedException e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testGunzip() {
    File file = new File(testFile.getAbsolutePath() + ".gz");

    ConanProcess process = new LocalGunzipProcess();
    Map<ConanParameter, String> processParams = new HashMap<ConanParameter, String>();
    Map<String, String> inputParams = new HashMap<String, String>();
    inputParams.put("file", file.getAbsolutePath());
    ProcessUtils.extractConanParameters(processParams, inputParams, process);

    try {
      process.execute(processParams);
    } catch (ProcessExecutionException e) {
      e.printStackTrace();
      fail();
    } catch (InterruptedException e) {
      e.printStackTrace();
      fail();
    }
  }
}
