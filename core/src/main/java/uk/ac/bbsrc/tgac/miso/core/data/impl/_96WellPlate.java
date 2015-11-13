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
import java.util.Set;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;

/**
 * A concrete Plate implementation representing a 96-well plate, comprising 96 {@link Library} elements.
 * 
 * @author Rob Davey
 * @date 05-Sep-2011
 * @since 0.1.1
 */
public class _96WellPlate extends PlateImpl<Library> implements Plateable, Serializable {
  public static final int MAX_ELEMENTS = 96;

  public _96WellPlate() {
    super();
  }

  public _96WellPlate(User user) {
    super(MAX_ELEMENTS, user);
  }

  @Override
  public int getSize() {
    return MAX_ELEMENTS;
  }

  @Override
  public LinkedList<Library> getElements() {
    return elements;
  }

  @Override
  public void addElement(Library library) throws IllegalStateException {
    if (elements.size() < MAX_ELEMENTS) {
      elements.add(library);
    } else {
      throw new IllegalStateException("This 96 well plate already has 96 libraries");
    }
  }

  @Override
  public Class getElementType() {
    return Library.class;
  }

  @Override
  public <T> Set<Plate<LinkedList<T>, T>> getPlates() throws Exception {
    throw new Exception("96-well plates have no internal plate structure");
  }
}
