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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Libraries
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface LibraryStore extends Store<Library>, PaginatedDataSource<Library> {

  /**
   * Get a Library given a ID barcode
   *
   * @param barcode of type String
   * @return Library
   * @throws IOException when
   */
  Library getByBarcode(String barcode) throws IOException;

  /**
   * List all Libraries that match a search criteria
   *
   * @param query of type String
   * @return Collection<Library>
   * @throws IOException when
   */
  List<Library> listBySearch(String query) throws IOException;

  /**
   * Get all Library with a given Library alias
   *
   * @param alias of type String
   * @return all libraries with the given alias
   * @throws IOException
   */
  List<Library> listByAlias(String alias) throws IOException;

  /**
   * List all Libraries generated from a Sample given a parent Sample ID
   *
   * @param sampleId of type long
   * @return Collection<Library>
   * @throws IOException when
   */
  List<Library> listBySampleId(long sampleId) throws IOException;

  /**
   * List all Libraries that are related to a Project given a Project ID
   *
   * @param projectId of type long
   * @return Collection<Library>
   * @throws IOException when
   */
  List<Library> listByProjectId(long projectId) throws IOException;

  /**
   * List all Libraries associated with ids from the given id list
   *
   * @return Collection<Library>
   * @throws IOException when the objects cannot be retrieved or read
   */
  List<Library> getByIdList(List<Long> idList) throws IOException;

  /**
   * Get a LibraryType given a LibraryType ID
   *
   * @param libraryTypeId of type long
   * @return LibraryType
   * @throws IOException when
   */
  LibraryType getLibraryTypeById(long libraryTypeId) throws IOException;

  /**
   * Get a LibraryType given a LibraryType description and platform
   *
   * @param description of type String
   * @param platformType of type PlatformType
   * @return LibraryType
   * @throws IOException when
   */
  LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException;

  /**
   * List all LibraryTypes
   *
   * @return Collection<LibraryType>
   * @throws IOException when
   */
  Collection<LibraryType> listAllLibraryTypes() throws IOException;

  /**
   * List all LibraryTypes available to a given platform
   *
   * @param platform PlatformType
   * @return Collection<LibraryType>
   * @throws IOException when
   */
  Collection<LibraryType> listLibraryTypesByPlatform(PlatformType platform) throws IOException;

  /**
   * Return the Library associated with a given positionId
   *
   * @param positionId of type long
   * @return Boxable
   */
  Boxable getByPositionId(long positionId);

  /**
   * List all libraries associated with an identificationBarcode from given list of identificationBarcodes (from scan)
   *
   * @param barcodeList
   * @return Collection<Library>
   * @throws IOException when the objects cannot be retrieved
   */
  List<Library> getByBarcodeList(Collection<String> barcodeList) throws IOException;

  /**
   *
   * @param querystr of type String
   * @return a count of how many libraries match the querystr
   * @throws IOException
   */
  long countLibrariesBySearch(String querystr) throws IOException;

  EntityReference getAdjacentLibrary(Library library, boolean before) throws IOException;

  List<Library> searchByCreationDate(Date from, Date to) throws IOException;

  /**
   * Retrieves a single Library by preMigrationId (DetailedLibrary only)
   * 
   * @param preMigrationId preMigration ID of Library
   * @throws IOException
   */
  Library getByPreMigrationId(Long preMigrationId) throws IOException;

  List<LibrarySpikeIn> listSpikeIns() throws IOException;

  LibrarySpikeIn getSpikeIn(long spikeInId) throws IOException;

}