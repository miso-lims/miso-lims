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

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Defines a DAO interface for storing Pools
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PoolStore extends Store<Pool<? extends Poolable>>, Remover<Pool>, NamingSchemeAware<Pool> {
  /**
   * Get a Pool given a Pool ID
   *
   * @param poolId of type long
   * @return Pool
   * @throws IOException when
   */
  Pool<? extends Poolable> getPoolById(long poolId) throws IOException;

  /**
   * Save a Pool
   *
   * @param pool of type Pool
   * @return long
   * @throws IOException when
   */
  long save(Pool<? extends Poolable> pool) throws IOException;

  /**
   * Get a Pool given a barcode and its platform
   *
   * @param barcode of type String
   * @param platformType of type PlatformType
   * @return Pool
   * @throws IOException when
   */
  Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Library} by means of that
   * Library's {@link uk.ac.bbsrc.tgac.miso.core.data.Dilution} objects
   *
   * @param libraryId of type long
   * @return List<IlluminaPool>
   * @throws IOException when
   */
  Collection<Pool<? extends Poolable>> listByLibraryId(long libraryId) throws IOException;

  /**
   * Get an IlluminaPool given an identification barcode
   *
   * @param barcode of type String
   * @return IlluminaPool
   * @throws IOException when
   */
  Pool<? extends Poolable> getIlluminaPoolByBarcode(String barcode) throws IOException;

  /**
   * Get an IlluminaPool given an IlluminaPool ID
   *
   * @param poolId of type long
   * @return IlluminaPool
   * @throws IOException when
   */
  Pool<? extends Poolable> getIlluminaPoolById(long poolId) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Project}
   *
   * @param projectId of type long
   * @return List<Pool<? extends Poolable>
   * @throws IOException when
   */
  Collection<Pool<? extends Poolable>> listByProjectId(long projectId) throws IOException;

  /**
   * List all Pools that are for a given {@link PlatformType}
   *
   * @param platformType of type PlatformType
   * @return List<Pool<? extends Poolable>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listAllByPlatform(PlatformType platformType) throws IOException;

  /**
   * List all Pools that are for a given {@link PlatformType} that match a search query String
   *
   * @param platformType of type PlatformType
   * @param query of type String
   * @return List<Pool<? extends Poolable>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listAllByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  /**
   * List "ready to run" Pools that are for a given {@link PlatformType}
   *
   * @param platformType of type PlatformType
   * @return List<Pool<? extends Poolable>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listReadyByPlatform(PlatformType platformType) throws IOException;

  /**
   * List "ready to run" Pools that are for a given {@link PlatformType} that match a search query String
   *
   * @param platformType of type PlatformType
   * @param query of type String
   * @return List<Pool<? extends Poolable>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listReadyByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  /**
   * List all IlluminaPools
   *
   * @return List<IlluminaPool>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listAllIlluminaPools() throws IOException;

  /**
   * List all ready to run IlluminaPools
   *
   * @return List<IlluminaPool>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listReadyIlluminaPools() throws IOException;

  /**
   * Save an IlluminaPool
   *
   * @param pool of type IlluminaPool
   * @return long
   * @throws IOException when
   */
  long saveIlluminaPool(IlluminaPool pool) throws IOException;

  /**
   * Get a LS454Pool given an identification barcode
   *
   * @param barcode of type String
   * @return LS454Pool
   * @throws IOException when
   */
  Pool<? extends Poolable> get454PoolByBarcode(String barcode) throws IOException;

  /**
   * Get a LS454Pool given a LS454Pool ID
   *
   * @param poolId of type long
   * @return LS454Pool
   * @throws IOException when
   */
  Pool<? extends Poolable> get454PoolById(long poolId) throws IOException;

  /**
   * List all LS454Pools
   *
   * @return List<LS454Pool>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listAll454Pools() throws IOException;

  /**
   * List all LS454Pools
   *
   * @return List<LS454Pool>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listReady454Pools() throws IOException;

  /**
   * Save a LS454Pool
   *
   * @param pool of type LS454Pool
   * @return long
   * @throws IOException when
   */
  long save454Pool(LS454Pool pool) throws IOException;

  /**
   * Get a SolidPool given an identification barcode
   *
   * @param barcode of type String
   * @return SolidPool
   * @throws IOException when
   */
  Pool<? extends Poolable> getSolidPoolByBarcode(String barcode) throws IOException;

  /**
   * Get a SolidPool given a SolidPool ID
   *
   * @param poolId of type long
   * @return SolidPool
   * @throws IOException when
   */
  Pool<? extends Poolable> getSolidPoolById(long poolId) throws IOException;

  /**
   * List all SolidPools
   * @return List<SolidPool>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listAllSolidPools() throws IOException;

  /**
   * List all SolidPools
   * @return List<SolidPool>
   * @throws IOException when
   */
  List<Pool<? extends Poolable>> listReadySolidPools() throws IOException;
  
  /**
   * Save a SolidPool
   *
   * @param pool of type SolidPool
   * @return long
   * @throws IOException when
   */
  long saveSolidPool(SolidPool pool) throws IOException;

  /**
   * Get any Pool related to an Experiment given an Experiment ID
   *
   * @param e of type Experiment
   * @return Pool
   * @throws IOException when
   */
  Pool<? extends Poolable> getPoolByExperiment(Experiment e) throws IOException;
}
