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

import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

/**
 * Defines a DAO interface for storing LibraryQCs
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PoolQcStore extends Store<PoolQC>, Remover<PoolQC> {
  /**
   * List all PoolQCs performed on a Pool given a parent Pool ID
   * 
   * @param poolId
   *          of type long
   * @return Collection<PoolQC>
   * @throws java.io.IOException
   *           when
   */
  Collection<PoolQC> listByPoolId(long poolId) throws IOException;

  /**
   * Get the QcType descriptor for a given type ID
   * 
   * @param qcTypeId
   * @return the QcType with the given ID, or null if none exists
   * @throws java.io.IOException
   */
  QcType getPoolQcTypeById(long qcTypeId) throws IOException;

  /**
   * Get the QcType descriptor for a given type name
   * 
   * @param qcName
   * @return the QcType with the given name, or null if none exists
   * @throws java.io.IOException
   */
  QcType getPoolQcTypeByName(String qcName) throws IOException;

  /**
   * Get all QcType descriptors for {@link uk.ac.bbsrc.tgac.miso.core.data.Pool} objects
   * 
   * @return Collection<QcType>
   * @throws java.io.IOException
   */
  Collection<QcType> listAllPoolQcTypes() throws IOException;
}
