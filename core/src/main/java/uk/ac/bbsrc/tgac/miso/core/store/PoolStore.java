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
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Pools
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PoolStore extends Store<Pool>, Remover<Pool>,
    NamingSchemeAware<Pool> {
  /**
   * Get a Pool given a barcode and its platform
   *
   * @param barcode
   *          of type String
   * @param platformType
   *          of type PlatformType
   * @return Pool
   * @throws IOException
   *           when
   */
  Pool getPoolByBarcode(String barcode, PlatformType platformType) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Sample} by means of that Sample's
   * {@link uk.ac.bbsrc.tgac.miso.core.data.Library} objects
   *
   * @param sampleId
   *          of type long
   * @return List<Pool<? extends Poolable<?,?>>
   * @throws IOException
   *           when
   */
  Collection<Pool> listBySampleId(long sampleId) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Library} by means of that Library's
   * {@link uk.ac.bbsrc.tgac.miso.core.data.Dilution} objects
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
   *          of type PlatformType
   * @return List<Pool<? extends Poolable<?,?>>
   * @throws IOException
   *           when
   */
  List<Pool> listAllByPlatform(PlatformType platformType) throws IOException;

  /**
   * Search Pools for name or alias matching query string.
   *
   * @param query
   *          a string which represents all or part of a Pool name or alias.
   * @return Collection<Pool<? extends Poolable<?,?>>> all Pools matching the query.
   */
  public Collection<Pool> listBySearch(String query);

  /**
   * List all Pools up to a maximum limit.
   *
   * @param limit
   *          the maximum number of results to return.
   * @return Collection<Pool<? extends Poolable<?,?>>> a limited number of Pools.
   */
  public List<Pool> listAllPoolsWithLimit(int limit) throws IOException;

  /**
   * List all Pools that are for a given {@link PlatformType} and have a name, alias, or identificationBarcode matching a query String
   *
   * @param platformType
   *          the PlatformType to find Pools for. Must not be null
   * @param query
   *          the name, alias, or identificationBarcode to search for. Null will be replaced with empty String
   * @return List<Pool<? extends Poolable<?,?>> all Pools matching the specified PlatformType and query String
   * @throws IOException
   */

  List<Pool> listAllByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  /**
   * List "ready to run" Pools that are for a given {@link PlatformType}
   *
   * @param platformType
   *          of type PlatformType
   * @return List<Pool<? extends Poolable<?,?>>
   * @throws IOException
   *           when
   */
  List<Pool> listReadyByPlatform(PlatformType platformType) throws IOException;

  /**
   * List "ready to run" Pools that are for a given {@link PlatformType} that match a search query String
   *
   * @param platformType
   *          the PlatformType to find Pools for. Must not be null
   * @param query
   *          the name, alias, or identificationBarcode to search for. Null will be replaced with empty String
   * @return List<Pool<? extends Poolable<?,?>> all Pools matching the specified Platform and query String
   * @throws IOException
   */
  List<Pool> listReadyByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  /**
   * Get any Pool related to an Experiment given an Experiment ID
   *
   * @param e
   *          of type Experiment
   * @return Pool
   * @throws IOException
   *           when
   */
  Pool getPoolByExperiment(Experiment e) throws IOException;

  /**
   * List the Pool associated with the given positionId
   * 
   * @param positionId
   *          of type long
   * @return Boxable
   * @throws IOException
   *           when
   */
  Boxable getByPositionId(long positionId);

  /**
   * List all Samples associated with identificationBarcodes from the given identificationBarcode list
   *
   * @return Collection<Sample
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Pool> getByBarcodeList(List<String> barcodeList) throws IOException;

  /**
   * List the Pool associated with a given identificationBarcode
   * 
   * @param barcode
   *          of type String
   * @return Pool
   * @throws IOException
   *           when
   */
  Pool getByBarcode(String barcode);

  /**
   * @return a map containing all column names and max lengths from the Pool table
   * @throws IOException
   */
  public Map<String, Integer> getPoolColumnSizes() throws IOException;

  /**
   * 
   * @param offset of type int
   * @param resultsPerPage of type int
   * @param querystr of type String
   * @param sortDir of type String
   * @param platform o type PlatformType
   * @return a list of pools for given platform of size resultsPerPage which match the querystr
   * @throws IOException
   */
  List<Pool> listBySearchOffsetAndNumResultsAndPlatform(int offset, int resultsPerPage, String querystr,
      String sortDir, String sortCol, PlatformType platform) throws IOException;

  /**
   * 
   * @param offset of type int
   * @param limit of type int
   * @param sortDir of type String
   * @param platform of type PlatformType
   * @return a list of pools for given platform of size limit
   * @throws IOException
   */
  List<Pool> listByOffsetAndNumResults(int offset, int limit, String sortDir, String sortCol,
      PlatformType platform) throws IOException;

  /**
   * 
   * @param platformName of type String
   * @param querystr of type String
   * @return a count of how many pools for given platform match the querystr
   * @throws IOException
   */
  long countPoolsBySearch(PlatformType platform, String querystr) throws IOException;

  /**
   * 
   * @param platform of type PlatformType
   * @return a count of pools for a given platform
   * @throws IOException
   */
  long countPoolsByPlatform(PlatformType platform) throws IOException;
}
