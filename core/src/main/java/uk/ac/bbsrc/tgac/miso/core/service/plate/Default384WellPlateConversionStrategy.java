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

package uk.ac.bbsrc.tgac.miso.core.service.plate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl._96WellPlate;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * A concrete implementation of a {@link PlateConversionStrategy} that takes a set of 4 96-well plates, and converts them into a specific
 * configuration for a single 384-well plate.
 * 
 * <p>
 * In this case, a 384 well plate has wells from A01 to P24, A-P being rows, 01-24 being columns. Well A01 will have 96-well Plate Q1 A01 in
 * it, well A02 will have 96-well Plate Q2 A01, B01 Q3A01. B02 Q4A01, etc. So effectively within each 4-well quadrant the top left well is
 * from 96-well plate Q1, Q2 is top right, Q3 is bottom left and Q4 is bottom right.
 * 
 * @author Rob Davey
 * @date 22/05/12
 * @since 0.1.6
 */
@ServiceProvider
public class Default384WellPlateConversionStrategy implements PlateConversionStrategy<_96WellPlate> {
  protected static final Logger log = LoggerFactory.getLogger(Default384WellPlateConversionStrategy.class);

  @Override
  public List<_96WellPlate> convert(List<_96WellPlate> plates) {
    if (plates.size() != 4) {
      throw new IllegalStateException("Cannot convert more or less than 4 96 well plates into a single 384 well plate");
    } else {
      Map<Integer, _96WellPlate> convertedPlateMap = new HashMap<Integer, _96WellPlate>();
      for (int i = 0; i < plates.size(); i++) {
        User owner = plates.get(i).getSecurityProfile().getOwner();
        _96WellPlate p = new _96WellPlate(owner);
        convertedPlateMap.put((i + 1), p);
      }

      // _96WellPlates are in a 12x8 (A01-A12,B01-B12 etc) configuration
      // So get blocks of 12 libraries and add them to the relevant new plates
      Library[][] convertedPlateArray = new Library[16][24];
      int row = 0;
      for (int p = 0; p < plates.size(); p++) {
        LinkedList<Library> elements = plates.get(p).getElements();
        // loop over the 8 input plate rows comprising 12 libraries each
        int originalPlateRow = 0;
        for (List<Library> splits : LimsUtils.partition(elements, 12)) {
          int newrow = 0;
          if (p < 2) {
            newrow = ((originalPlateRow + 1) * 2) - 1;
            for (int i = 0; i < splits.size(); i++) {
              int newpos = ((i + 1) * 2) - 1;
              if ((p % 2) != 0) {
                newpos = ((i + 1) * 2);
              }
              convertedPlateArray[newrow - 1][newpos - 1] = splits.get(i);
            }
          } else {
            newrow = ((originalPlateRow + 1) * 2);
            for (int i = 0; i < splits.size(); i++) {
              int newpos = ((i + 1) * 2) - 1;
              if ((p % 2) != 0) {
                newpos = ((i + 1) * 2);
              }
              convertedPlateArray[newrow - 1][newpos - 1] = splits.get(i);
            }
          }
          originalPlateRow++;
        }
        row++;
      }

      // convert the 16x24 array back to four 8x12s
      for (int s = 0; s < convertedPlateArray.length; s++) {
        for (int t = 0; t < convertedPlateArray[s].length; t++) {
          if (s < 8 && t < 12) {
            // q1
            convertedPlateMap.get(1).getElements().add(convertedPlateArray[s][t]);
          } else if (s < 8 && t > 11) {
            // q2
            convertedPlateMap.get(2).getElements().add(convertedPlateArray[s][t]);
          } else if (s > 7 && t < 12) {
            // q3
            convertedPlateMap.get(3).getElements().add(convertedPlateArray[s][t]);
          } else if (s > 7 && t > 11) {
            // q4
            convertedPlateMap.get(4).getElements().add(convertedPlateArray[s][t]);
          }
        }
      }
      return new LinkedList<_96WellPlate>(convertedPlateMap.values());
    }
  }
}
