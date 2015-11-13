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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.type._384WellPlatePosition;

/**
 * A concrete Plate implementation representing a Simple 384-well plate, comprising 384 {@link uk.ac.bbsrc.tgac.miso.core.data.Library}s.
 * This implementation is notably simpler than the default _384WellPlate, which comprises 4 96-well plates in each quarter.
 * 
 * @author Rob Davey
 * @date 05-Dec-2012
 * @since 0.1.9
 */
@JsonIgnoreProperties({ "securityProfile", "internalPoolableElements", "size", "elementType" })
public class Simple384WellPlate extends PlateImpl<Library> implements Serializable {
  public static final int MAX_ELEMENTS = 384;

  public Simple384WellPlate() {
    super();

  }

  public Simple384WellPlate(User user) {
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
  public void setElements(LinkedList<Library> elements) {
    if (elements != null && elements.size() == MAX_ELEMENTS) {
      this.elements = elements;
    }
  }

  @Override
  public void addElement(Library library) throws IllegalStateException {
    if (elements != null && elements.size() < MAX_ELEMENTS) {
      elements.add(library);
    } else {
      throw new IllegalStateException("This 384 well plate already has 384 libraries");
    }
  }

  @Override
  public Class getElementType() {
    return Library.class;
  }

  public void setElement(int pos, Library library) throws IllegalArgumentException {
    if (elements != null && pos > 0 && pos <= MAX_ELEMENTS) {
      elements.set(pos, library);
    } else {
      throw new IllegalArgumentException("Element position must be between 1 and " + MAX_ELEMENTS);
    }
  }

  public void setElement(String pos, Library library) {
    _384WellPlatePosition pp = _384WellPlatePosition.valueOf(pos);
    if (pp != null) {
      elements.set(_384WellPlatePosition.getPositionMap().get(pp), library);
    } else {
      throw new IllegalArgumentException("Element position must be between A-P and 1-24");
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PLATE ").append(this.getName()).append("\n");
    int splitcount = 1;
    for (Library l : elements) {
      if (splitcount == 1) {
        sb.append("|");
      }
      sb.append(l.getAlias());
      if (splitcount == elements.size()) {
        sb.append("|\n");
      } else {
        sb.append(",");
      }
      splitcount++;
    }
    return sb.toString();
  }
}
