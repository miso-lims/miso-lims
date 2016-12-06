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

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Dilutions
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface LibraryDilutionStore extends Store<LibraryDilution>, Remover<LibraryDilution>, NamingSchemeAware<LibraryDilution> {
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
  Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformtype) throws IOException;

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
   * List all LibraryDilutions for a particular PlatformType that match a given search string
   *
   * @param query
   *          the name or identificationBarcode to search for
   * @param platformType
   *          the PlatformType to include LibraryDilutions for
   * @return a Collection of LibraryDilutions matching the PlatformType and search String
   * @throws IOException
   */
  Collection<LibraryDilution> listAllLibraryDilutionsBySearchAndPlatform(String query, PlatformType platformType) throws IOException;

  Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) throws IOException;

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
  Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException;

  /**
   * Get a Dilution by ID barcode, given a PlatformType
   *
   * @param barcode
   *          of type String
   * @param platformType
   *          of type PlatformType
   * @return Dilution
   * @throws IOException
   *           when
   */
  LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException;

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
  LibraryDilution getLibraryDilutionByIdAndPlatform(long dilutionId, PlatformType platformType) throws IOException;

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
   * Retrieve all Library Dilutions up to a set maximum number of records
   *
   * @param limit
   *          maximum number of records to return. Negative values will result in no limit
   * @throws IOException
   */
  Collection<LibraryDilution> listAllWithLimit(long limit) throws IOException;

  /**
   *
   * @param offset
   *          of type int
   * @param limit
   *          of type int
   * @param querystr
   *          of type String
   * @param sortDir
   *          of type String
   * @param platform
   *          of type PlatformType
   * @return a list of LibraryDilutions for given platform of size resultsPerPage which match the querystr
   * @throws IOException
   */
  List<LibraryDilution> listBySearchOffsetAndNumResultsAndPlatform(int offset, int limit, String querystr, String sortDir, String sortCol,
      PlatformType platform) throws IOException;

  /**
   * @param platform
   * @return
   * @throws IOException
   */
  public int countByPlatform(PlatformType platform) throws IOException;

  /**
   * @param search
   * @param platform
   * @return
   * @throws IOException
   */
  public Integer countAllBySearchAndPlatform(String search, PlatformType platform) throws IOException;
}
