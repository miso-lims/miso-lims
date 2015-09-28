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

package uk.ac.bbsrc.tgac.miso.core.test;

import com.eaglegenomics.simlims.core.User;
import junit.framework.TestCase;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultLibraryNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.util.FormUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 06-Sep-2011
 * @since 0.1.1
 */
public class FormUtilsTests {
  protected static final Logger log = LoggerFactory.getLogger(FormUtilsTests.class);

  private static File testSampleDeliveryFile;
  private static File testSampleBulkInputOdsFile;
  private static File testSampleBulkInputXlsFile;

  static {
    try {
      testSampleDeliveryFile = File.createTempFile("test-sampleDeliveryForm", ".odt");
      testSampleBulkInputOdsFile = File.createTempFile("test-sampleBulkInputOds", ".ods");
      testSampleBulkInputXlsFile = File.createTempFile("test-sampleBulkInputXls", ".xlsx");
    }
    catch (IOException e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testCreateSampleDeliveryForm() {
    try {
      uk.ac.bbsrc.tgac.miso.core.util.FormUtils.createSampleDeliveryForm(generateSamples(), testSampleDeliveryFile, true);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testConvertToPdf() {
    try {
      OdfTextDocument oDoc = uk.ac.bbsrc.tgac.miso.core.util.FormUtils.createSampleDeliveryForm(generateSamples(), testSampleDeliveryFile, false);
      uk.ac.bbsrc.tgac.miso.core.util.FormUtils.convertToPDF(oDoc);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testImportSampleDeliveryForm() {
    try {
      List<Sample> samples = uk.ac.bbsrc.tgac.miso.core.util.FormUtils.importSampleDeliveryForm(testSampleDeliveryFile);
      int numExpected = generateSamples().size();
      if (samples.size() != numExpected) {
        log.error("Expected samples in: " + numExpected + ". Number imported: " + samples.size());
        TestCase.fail();
      }
      else {
        log.info("Expected samples in: " + numExpected + ". Number imported: " + samples.size());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
    finally {
      testSampleDeliveryFile.delete();
    }
  }

  @Test
  public void testImportBulkInputODS() {
    try {
      InputStream in = FormUtilsTests.class.getClassLoader().getResourceAsStream("test-bulk_input.ods");
      LimsUtils.writeFile(in, testSampleBulkInputOdsFile);
      User u = new UserImpl();
      u.setLoginName("testBulkImportUser");
      List<Sample> samples = FormUtils.importSampleInputSpreadsheet(testSampleBulkInputOdsFile, u, new MockFormTestRequestManager(), new DefaultLibraryNamingScheme());
      log.info("Imported :: " + LimsUtils.join(samples, " | "));
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
    finally {
      testSampleBulkInputOdsFile.delete();
    }
  }

  @Test
  public void testImportBulkInputXLS() {
    try {
      InputStream in = FormUtilsTests.class.getClassLoader().getResourceAsStream("test-bulk_input.xlsx");
      LimsUtils.writeFile(in, testSampleBulkInputXlsFile);
      User u = new UserImpl();
      u.setLoginName("testBulkImportUser");
      List<Sample> samples = FormUtils.importSampleInputSpreadsheet(testSampleBulkInputXlsFile, u, new MockFormTestRequestManager(), new DefaultLibraryNamingScheme());
      log.info("Imported :: " + LimsUtils.join(samples, " | "));
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
    finally {
      testSampleBulkInputXlsFile.delete();
    }
  }

  private List<Sample> generateSamples() {
    List<Sample> samples = new ArrayList<Sample>();
    DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();

    for (int i = 1; i < 6; i++) {
      Sample s = dataObjectFactory.getSample();
      s.setId(i);
      s.setName("SAM"+i);
      s.setAlias("MI_S"+i+"_TestSample");
      s.setScientificName("F.bar");
      s.setIdentificationBarcode(s.getName() + "::" + s.getAlias());
      samples.add(s);
    }
    Collections.sort(samples);
    return samples;
  }
}
