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

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Pools
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PoolStore
    extends Store<Pool<? extends Poolable>>, Remover<Pool<? extends Poolable>>, NamingSchemeAware<Pool<? extends Poolable>> {
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
  Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Sample} by means of that Sample's
   * {@link uk.ac.bbsrc.tgac.miso.core.data.Library} objects
   * 
   * @param sampleId
   *          of type long
   * @return List<Pool<? extends Poolable>
   * @throws IOException
   *           when
   */
  Collection<Pool<? extends Poolable>> listBySampleId(long sampleId) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Library} by means of that Library's
   * {@link uk.ac.bbsrc.tgac.miso.core.data.Dilution} objects
   * 
   * @param libraryId
   *          of type long
   * @return List<Pool<? extends Poolable>
   * @throws IOException
   *           when
   */
  Collection<Pool<? extends Poolable>> listByLibraryId(long libraryId) throws IOException;

  /**
   * List all Pools that are related to a given {@link uk.ac.bbsrc.tgac.miso.core.data.Project}
   * 
   * @param projectId
   *          of type long
   * @return List<Pool<? extends Poolable>
   * @throws IOException
   *           when
   */
  Collection<Pool<? extends Poolable>> listByProjectId(long projectId) throws IOException;

  /**
   * List all Pools that are for a given {@link PlatformType}
   * 
   * @param platformType
   *          of type PlatformType
   * @return List<Pool<? extends Poolable>
   * @throws IOException
   *           when
   */
  List<Pool<? extends Poolable>> listAllByPlatform(PlatformType platformType) throws IOException;

  /**
   * List all Pools that are for a given {@link PlatformType} that match a search query String
   * 
   * @param platformType
   *          of type PlatformType
   * @param query
   *          of type String
   * @return List<Pool<? extends Poolable>
   * @throws IOException
   *           when
   */
  List<Pool<? extends Poolable>> listAllByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  /**
   * List "ready to run" Pools that are for a given {@link PlatformType}
   * 
   * @param platformType
   *          of type PlatformType
   * @return List<Pool<? extends Poolable>
   * @throws IOException
   *           when
   */
  List<Pool<? extends Poolable>> listReadyByPlatform(PlatformType platformType) throws IOException;

  /**
   * List "ready to run" Pools that are for a given {@link PlatformType} that match a search query String
   * 
   * @param platformType
   *          of type PlatformType
   * @param query
   *          of type String
   * @return List<Pool<? extends Poolable>
   * @throws IOException
   *           when
   */
  List<Pool<? extends Poolable>> listReadyByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  /**
   * Get any Pool related to an Experiment given an Experiment ID
   * 
   * @param e
   *          of type Experiment
   * @return Pool
   * @throws IOException
   *           when
   */
  Pool<? extends Poolable> getPoolByExperiment(Experiment e) throws IOException;
  
  /**
   * List the Pool associated with the given positionId
   * @param positionId
   *          of type long
   * @return Boxable
   * @throws IOException
   *           when
   */
  Boxable getByPositionId(long positionId);

  /**
   * List all Samples associated with identificationBarcodes from the given 
   * identificationBarcode list
   * 
   * @return Collection<Sample
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Pool<? extends Poolable>> getByBarcodeList(List<String> barcodeList) throws IOException;

  /**
   * List the Pool associated with a given identificationBarcode
   * @param barcode 
   *          of type String
   * @return Pool
   * @throws IOException
   *           when
   */
  Pool<? extends Poolable> getByBarcode(String barcode);
}
