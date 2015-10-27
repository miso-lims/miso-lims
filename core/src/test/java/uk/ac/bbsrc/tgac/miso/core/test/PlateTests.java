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

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl._384WellPlate;
import uk.ac.bbsrc.tgac.miso.core.data.impl._96WellPlate;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.plate.Default384WellPlateConversionStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.plate.PlateConversionStrategy;

/**
 * uk.ac.bbsrc.tgac.miso.core.test
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 13-Sep-2011
 * @since 0.1.1
 */
public class PlateTests {
  protected static final Logger log = LoggerFactory.getLogger(PlateTests.class);
  private DataObjectFactory dataObjectFactory;

  @Before
  public void setUp() {
    dataObjectFactory = new TgacDataObjectFactory();
  }

  @Test
  public void testConstructPlates() {
    uk.ac.bbsrc.tgac.miso.core.data.Plate p = dataObjectFactory.getPlateOfSize(96);
    log.info(p.getElementType().getName());
  }

  @Test
  public void test384WellPlateConversion() {
    log.info("Generating plates and libraries");
    List<_96WellPlate> plates = new LinkedList<_96WellPlate>();
    for (int p = 1; p < 5; p++) {
      _96WellPlate plate = new _96WellPlate();

      for (int i = 1; i < 97; i++) {
        Library l = new LibraryImpl();
        l.setAlias("P" + p + "_L" + i + "_TestLib");
        plate.addElement(l);
      }
      plates.add(plate);
    }

    _384WellPlate finalPlate = new _384WellPlate();
    finalPlate.setName("Test 384 Plate");

    PlateConversionStrategy<_96WellPlate> pcs = new Default384WellPlateConversionStrategy();
    finalPlate.setConversionStrategy(pcs);

    log.info("Doing conversion from 4x 96-well to 1x 384-well");
    finalPlate.setElementsAndDoConversion(plates);

    System.out.println(finalPlate.toString());
  }

  @After
  public void tearDown() {
    dataObjectFactory = null;
  }
}
