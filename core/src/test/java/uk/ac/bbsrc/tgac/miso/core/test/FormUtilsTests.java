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

import junit.framework.TestCase;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

import java.io.File;
import java.io.IOException;
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

  private static File testFile;

  static {
    try {
      testFile = File.createTempFile("test-sampleDeliveryForm", ".odt");
    }
    catch (IOException e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testCreateSampleDeliveryForm() {
    try {
      uk.ac.bbsrc.tgac.miso.core.util.FormUtils.createSampleDeliveryForm(generateSamples(), testFile);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testConvertToPdf() {
    try {
      OdfTextDocument oDoc = uk.ac.bbsrc.tgac.miso.core.util.FormUtils.createSampleDeliveryForm(generateSamples(), testFile);
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
      List<Sample> samples = uk.ac.bbsrc.tgac.miso.core.util.FormUtils.importSampleDeliveryForm(testFile);
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
  }

  private List<Sample> generateSamples() {
    List<Sample> samples = new ArrayList<Sample>();
    DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();

    for (int i = 1; i < 6; i++) {
      Sample s = dataObjectFactory.getSample();
      s.setSampleId(new Long(i));
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
