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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.service.plate.Default384WellPlateConversionStrategy;
import uk.ac.bbsrc.tgac.miso.core.service.plate.PlateConversionStrategy;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * A concrete Plate implementation representing a 384-well plate, comprising 4 quartile {@link _96WellPlate}s.
 * 
 * @author Rob Davey
 * @date 05-Sep-2011
 * @since 0.1.1
 */
public class _384WellPlate extends PlateImpl<_96WellPlate> implements Serializable {
  public static final int MAX_ELEMENTS = 4;
  public PlateConversionStrategy<_96WellPlate> plateConversionStrategy = new Default384WellPlateConversionStrategy();

  public _384WellPlate() {
    super();
  }

  public _384WellPlate(User user) {
    super(MAX_ELEMENTS, user);
  }

  public void setConversionStrategy(PlateConversionStrategy<_96WellPlate> plateConversionStrategy) {
    this.plateConversionStrategy = plateConversionStrategy;
  }

  @Override
  public int getSize() {
    return 384;
  }

  @Override
  public LinkedList<_96WellPlate> getElements() {
    return elements;
  }

  @Override
  public void addElement(_96WellPlate plate) throws IllegalStateException {
    if (elements.size() < MAX_ELEMENTS) {
      plate.setName("Q" + (elements.size() + 1));
      elements.add(plate);
    } else {
      throw new IllegalStateException("This 384 well plate already has 4 96 well plates");
    }
  }

  @Override
  public Class getElementType() {
    return _96WellPlate.class;
  }

  public void setElement(int quarter, _96WellPlate plate) {
    elements.set(quarter, plate);
    plate.setName("Q" + quarter);
  }

  public void setElementsAndDoConversion(List<_96WellPlate> plates) throws IllegalStateException {
    if (plates.size() != MAX_ELEMENTS) {
      throw new IllegalStateException(
          "Number of plates to add cannot be below or exceed the number of max elements (" + MAX_ELEMENTS + ")");
    } else {
      for (_96WellPlate plate : this.plateConversionStrategy.convert(plates)) {
        addElement(plate);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PLATE " + this.getName() + "\n");
    for (_96WellPlate plate : elements) {
      sb.append("INNER PLATE " + plate.getName() + "\n");
      for (List<Library> splits : LimsUtils.partition(plate.getElements(), 12)) {
        int splitcount = 1;
        for (Library l : splits) {
          if (splitcount == 1) {
            sb.append("|");
          }
          sb.append(l.getAlias());
          if (splitcount == splits.size()) {
            sb.append("|\n");
          } else {
            sb.append(",");
          }
          splitcount++;
        }
      }
    }
    return sb.toString();
  }
}