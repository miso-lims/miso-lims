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

import java.util.List;

import net.sourceforge.fluxion.spi.Spi;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;

/**
 * An interface that defines how to convert between Lists of {@link Plate} objects. This is usually employed when moving from one Plate well
 * configuration to another, e.g. between 4 96-well plates to a single 384-well plate. Libraries are often in different wells, or pooled,
 * upon Plate preparation, so a conversion is necessary to track this change in placement.
 * 
 * @author Rob Davey
 * @date 22/05/12
 * @since 0.1.6
 */
@Spi
public interface PlateConversionStrategy<T extends Plate> {
  /**
   * Convert the List of {@link Plate} objects
   * 
   * @param plates
   * @return the converted list of Plates
   */
  List<T> convert(List<T> plates);
}