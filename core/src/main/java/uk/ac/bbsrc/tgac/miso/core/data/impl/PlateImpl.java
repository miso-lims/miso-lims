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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractPlate;

import java.util.Collection;
import java.util.LinkedList;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 13-Sep-2011
 * @since 0.1.1
 */
public class PlateImpl<T> extends AbstractPlate<LinkedList<T>, T> {
  public LinkedList<T> elements = new LinkedList<T>();

  public PlateImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  public PlateImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public int getSize() {
    return elements.size();
  }

  @Override
  public LinkedList<T> getElements() {
    return elements;
  }

  @Override
  public void addElement(T o) {
    elements.add(o);
  }

  @Override
  public Class getElementType() {
    return Object.class;
  }

  @Override
  public Collection<T> getInternalPoolableElements() {
    return getElements();
  }
}
