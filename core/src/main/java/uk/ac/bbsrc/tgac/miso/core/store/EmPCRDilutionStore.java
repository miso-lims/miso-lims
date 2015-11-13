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

import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Dilutions
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface EmPCRDilutionStore extends Store<emPCRDilution>, Remover<emPCRDilution>, NamingSchemeAware<emPCRDilution> {
  /**
   * List all Dilutions prepared for a given PlatformType and search query
   * 
   * @param query
   *          of type String
   * @param platformtype
   *          of type PlatformType
   * @return Collection<? extends Dilution>
   * @throws java.io.IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutionsByPlatformAndSearch(String query, PlatformType platformtype) throws IOException;

  /**
   * List all Dilutions prepared for a given PlatformType that are part of a given Pool
   * 
   * @param platformType
   *          of type PlatformType
   * @param poolId
   *          of type long
   * @return Collection<? extends Dilution>
   * @throws java.io.IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException;

  /**
   * List all emPCRDilutions prepared for a given PlatformType
   * 
   * @param platformType
   *          of type PlatformType
   * @return Collection<emPCRDilution>
   * @throws java.io.IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutionsByPlatform(PlatformType platformType) throws IOException;

  /**
   * List all emPCRDilutions related to a given Project
   * 
   * @param projectId
   *          of type long
   * @return Collection<emPCRDilution>
   * @throws java.io.IOException
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
   * @throws java.io.IOException
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
   * @throws java.io.IOException
   *           when
   */
  Collection<emPCRDilution> listAllEmPcrDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;

  /**
   * List all emPCRDilutions by a given parent EmPCR ID
   * 
   * @param pcrId
   *          of type long
   * @return Collection<emPCRDilution>
   * @throws java.io.IOException
   *           when
   */
  Collection<emPCRDilution> listAllByEmPCRId(long pcrId) throws IOException;

  /**
   * Get a Dilution by ID barcode, given a PlatformType
   * 
   * @param barcode
   *          of type String
   * @return Dilution
   * @throws java.io.IOException
   *           when
   */
  emPCRDilution getEmPcrDilutionByBarcode(String barcode) throws IOException;

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
  emPCRDilution getEmPcrDilutionByIdAndPlatform(long dilutionId, PlatformType platformType) throws IOException;

  /**
   * Get a Dilution by ID barcode, given a PlatformType
   * 
   * @param barcode
   *          of type String
   * @param platformType
   *          of type PlatformType
   * @return Dilution
   * @throws java.io.IOException
   *           when
   */
  emPCRDilution getEmPcrDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;
}
