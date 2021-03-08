/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Pools
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PoolStore extends Store<Pool>, PaginatedDataSource<Pool> {

  /**
   * @param libraryId
   * @return a list all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Library} by means of that Library's
   *         {@link LibraryAliquot} objects
   * @throws IOException
   */
  List<Pool> listByLibraryId(long libraryId) throws IOException;

  List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException;

  /**
   * List the Pool associated with a given identificationBarcode
   * 
   * @param barcode
   *          of type String
   * @return Pool
   * @throws IOException
   *           when
   */
  Pool getByBarcode(String barcode) throws IOException;

  Pool getByAlias(String alias) throws IOException;

  List<Pool> listByIdList(List<Long> poolIds);

  public long getPartitionCount(Pool pool);

}
