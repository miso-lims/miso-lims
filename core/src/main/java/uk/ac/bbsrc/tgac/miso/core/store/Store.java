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
import java.util.Collection;

/**
 * Defines a DAO interface
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface Store<T> {
  /**
   * Save a persistable object of a given type T
   * 
   * @param t
   *          of type T
   * @return long
   * @throws IOException
   *           when the object cannot be saved
   */
  public long save(T t) throws IOException;

  /**
   * Get a persisted object of a given type T
   * 
   * @param id
   *          of type long
   * @return T
   * @throws IOException
   *           when the object cannot be retrieved
   */
  public T get(long id) throws IOException;

  /**
   * Retrieve an object from an underlying data store given an ID
   * <p/>
   * This method intends to retrieve objects in an 'ignorant' fashion, i.e. will not populate parent or child objects that could lead to a
   * circular dependency
   * 
   * @param id
   *          of type long
   * @return T
   * @throws IOException
   *           when
   */
  public T lazyGet(long id) throws IOException;

  /**
   * List all persisted objects of a given type T
   * 
   * @return Collection<T>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  public Collection<T> listAll() throws IOException;

  /**
   * Count all persisted objects
   * 
   * @return number of persisted objects
   * @throws IOException
   *           when the count of objects cannot be retrieved
   */
  public int count() throws IOException;
}
