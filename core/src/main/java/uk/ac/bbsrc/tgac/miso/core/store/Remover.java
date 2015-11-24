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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

/**
 * Defines a contract whereby an implementing class is able to remove a given object T from a store
 * 
 * @author Rob Davey
 * @date 09-May-2011
 * @since 0.0.3
 */
public interface Remover<T extends Deletable> {
  /**
   * Remove object of given type T
   * 
   * @param t
   *          of type T
   * @return boolean true if removed successfully
   * @throws java.io.IOException
   *           when the object cannot be removed
   */
  public boolean remove(T t) throws IOException;
}
