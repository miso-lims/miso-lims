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

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Dilutions
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface DilutionStore extends Store<Dilution>, Remover<Dilution>, NamingSchemeAware<Dilution> {
  /**
   * List all LibraryDilutions by a given parent library ID
   * 
   * @param libraryId
   *          of type long
   * @return Collection<LibraryDilution>
   * @throws IOException
   *           when
   */
  Collection<LibraryDilution> listByLibraryId(long libraryId) throws IOException;

  /**
   * List all Dilutions prepared for a given PlatformType
   * 
   * @param platformtype
   *          of type PlatformType
   * @return List<? extends Dilution>
   * @throws IOException
   *           when
   */
  List<? extends Dilution> listAllDilutionsByPlatform(PlatformType platformtype) throws IOException;

  /**
   * List all Dilutions prepared for a given PlatformType that are part of a given Pool
   * 
   * @param platformType
   *          of type PlatformType
   * @param poolId
   *          of type long
   * @return Collection<? extends Dilution>
   * @throws IOException
   *           when
   */
  Collection<? extends Dilution> listAllDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException;

  /**
   * List all LibraryDilutions prepared for a given PlatformType
   * 
   * @param platformType
   *          of type PlatformType
   * @return Collection<LibraryDilution>
   * @throws IOException
   *           when
   */
  Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException;

  /**
   * List all LibraryDilutions that are related to a given Project
   * 
   * @param projectId
   *          of type long
   * @return Collection<LibraryDilution>
   * @throws IOException
   *           when
   */
  Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException;

  /**
   * List all LibraryDilutions that match a given search string
   * 
   * @param query
   *          of type String
   * @param platformType
   *          of type PlatformType
   * @return Collection<LibraryDilution>
   * @throws IOException
   *           when
   */
  Collection<LibraryDilution> listAllLibraryDilutionsBySearch(String query, PlatformType platformType) throws IOException;

  /**
   * List all emPCRDilutions prepared for a given PlatformType
   * 
   * @param platformType
   *          of type PlatformType
   * @return Collection<emPCRDilution>
   * @throws IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutionsByPlatform(PlatformType platformType) throws IOException;

  /**
   * List all emPCRDilutions related to a given Project
   * 
   * @param projectId
   *          of type long
   * @return Collection<emPCRDilution>
   * @throws IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutionsByProjectId(long projectId) throws IOException;

  /**
   * List all emPCRDilutions that match a given search string
   * 
   * @param query
   *          of type String
   * @param platformType
   *          of type PlatformType
   * @return Collection<emPCRDilution>
   * @throws IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutionsBySearch(String query, PlatformType platformType) throws IOException;

  /**
   * List all emPCRDilutions prepared for a given PlatformType within a given Project
   * 
   * @param projectId
   *          of type long
   * @param platformType
   *          of type PlatformType
   * @return Collection<Dilution>
   * @throws IOException
   *           when
   */
  Collection<? extends Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;

  /**
   * List all LibraryDilutions
   * 
   * @return Collection<LibraryDilution>
   * @throws IOException
   *           when
   */
  Collection<LibraryDilution> listAllLibraryDilutions() throws IOException;

  /**
   * List all emPCRDilutions
   * 
   * @return Collection<emPCRDilution>
   * @throws IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutions() throws IOException;

  /**
   * List all emPCRDilutions by a given parent EmPCR ID
   * 
   * @param pcrId
   *          of type long
   * @return Collection<emPCRDilution>
   * @throws IOException
   *           when
   */
  Collection<emPCRDilution> listAllByEmPCRId(long pcrId) throws IOException;

  /**
   * List all LibraryDilutions in a given IlluminaPool
   * 
   * @param poolId
   *          of type long
   * @return Collection<LibraryDilution>
   * @throws IOException
   *           when
   */
  Collection<LibraryDilution> listByIlluminaPoolId(long poolId) throws IOException;

  /**
   * List all LibraryDilutions in a given 454 Pool
   * 
   * @param poolId
   *          of type long
   * @return Collection<emPCRDilution>
   * @throws IOException
   *           when
   */
  Collection<emPCRDilution> listByLS454PoolId(long poolId) throws IOException;

  /**
   * List all LibraryDilutions in a given SolidPool
   * 
   * @param poolId
   *          of type long
   * @return Collection<emPCRDilution>
   * @throws IOException
   *           when
   */
  Collection<emPCRDilution> listBySolidPoolId(long poolId) throws IOException;

  /**
   * List all LibraryDilutions in a given EmPCRool
   * 
   * @param poolId
   *          of type long
   * @return Collection<LibraryDilution>
   * @throws IOException
   *           when
   */
  Collection<LibraryDilution> listByEmPCRPoolId(long poolId) throws IOException;

  /**
   * Get a Dilution by ID, given a PlatformType
   * 
   * @param dilutionId
   *          of type long
   * @param platformType
   *          of type PlatformType
   * @return Dilution
   * @throws IOException
   *           when
   */
  Dilution getDilutionByIdAndPlatform(long dilutionId, PlatformType platformType) throws IOException;

  /**
   * Get a LibraryDilution by ID
   * 
   * @param dilutionId
   *          of type long
   * @return LibraryDilution
   * @throws IOException
   *           when
   */
  LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException;

  /**
   * Get a LibraryDilution by ID barcode
   * 
   * @param barcode
   *          of type String
   * @return LibraryDilution
   * @throws IOException
   *           when
   */
  LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException;

  /**
   * Save a LibraryDilution
   * 
   * @param dilution
   *          of type LibraryDilution
   * @return long
   * @throws IOException
   *           when
   */
  long saveLibraryDilution(LibraryDilution dilution) throws IOException;

  /**
   * Get an emPCRDilution by ID
   * 
   * @param dilutionId
   *          of type long
   * @return emPCRDilution
   * @throws IOException
   *           when
   */
  emPCRDilution getEmPCRDilutionById(long dilutionId) throws IOException;

  /**
   * Get an emPCRDilution by ID barcode
   * 
   * @param barcode
   *          of type String
   * @return emPCRDilution
   * @throws IOException
   *           when
   */
  emPCRDilution getEmPCRDilutionByBarcode(String barcode) throws IOException;

  /**
   * Save an emPCRDilution
   * 
   * @param dilution
   *          of type emPCRDilution
   * @return long
   * @throws IOException
   *           when
   */
  long saveEmPCRDilution(emPCRDilution dilution) throws IOException;
}
