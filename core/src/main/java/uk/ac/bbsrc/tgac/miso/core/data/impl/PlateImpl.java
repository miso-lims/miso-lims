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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractPlate;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 13-Sep-2011
 * @since 0.1.1
 */
public class PlateImpl<T extends Plateable> extends AbstractPlate<LinkedList<T>, T> implements Serializable {
  public LinkedList<T> elements = new LinkedList<T>();
  private Set<Pool<Plate<LinkedList<T>, T>>> pools = new HashSet<Pool<Plate<LinkedList<T>, T>>>();

  private int size = 96;

  public PlateImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  public PlateImpl(int size) {
    setSecurityProfile(new SecurityProfile());
    this.size = size;
  }

  public PlateImpl(int size, User user) {
    setSecurityProfile(new SecurityProfile(user));
    this.size = size;
  }

  @Override
  public int getSize() {
    if (elements.isEmpty()) {
      return this.size;
    }
    return elements.size();
  }

  @Override
  public void setSize(int size) throws Exception {
    if (size != this.size) {
      throw new Exception("Unable to set size of a plate once it has been constructed");
    }
  }

  @Override
  public LinkedList<T> getElements() {
    return elements;
  }

  @Override
  public void setElements(LinkedList<T> elements) {
    this.elements = elements;
  }

  @Override
  public void addElement(T o) {
    elements.add(o);
  }

  @Override
  public Class getElementType() {
    return Plateable.class;
  }

  @Override
  public Collection<T> getInternalPoolableElements() {
    return getElements();
  }

  @Override
  public Set<Pool<Plate<LinkedList<T>, T>>> getPools() {
    return pools;
  }
}
