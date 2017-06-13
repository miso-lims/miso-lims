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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Pools
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PoolStore extends Store<Pool>, Remover<Pool>, PaginatedDataSource<Pool> {

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Library} by means of that Library's
   * {@link LibraryDilution} objects
   *
   * @param libraryId
   *          of type long
   * @return List<Pool<? extends Poolable<?,?>>
   * @throws IOException
   *           when
   */
  Collection<Pool> listByLibraryId(long libraryId) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Project}
   *
   * @param projectId
   *          of type long
   * @return List<Pool<? extends Poolable<?,?>>
   * @throws IOException
   *           when
   */
  Collection<Pool> listByProjectId(long projectId) throws IOException;

  /**
   * List all Pools that are for a given {@link PlatformType}
   *
   * @param platformType
   *          of type PlatformType (null for wildcard)
   * @param query the search term to use (null for wildcard)
   * @param limit the number of results to return (null for all)
   * @param ready if true limit to only pools
   * @return List<Pool<? extends Poolable<?,?>>
   * @throws IOException
   *           when
   */
  List<Pool> listAllByCriteria(PlatformType platformType, String query, Integer limit, boolean ready) throws IOException;

  /**
   * List all Samples associated with identificationBarcodes from the given identificationBarcode list
   *
   * @return Collection<Sample
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Pool> getByBarcodeList(Collection<String> barcodeList) throws IOException;

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

  /**
   * @return a map containing all column names and max lengths from the Pool table
   * @throws IOException
   */
  public Map<String, Integer> getPoolColumnSizes() throws IOException;

  void removeWatcher(Pool pool, User watcher);

  void addWatcher(Pool pool, User watcher);
}
