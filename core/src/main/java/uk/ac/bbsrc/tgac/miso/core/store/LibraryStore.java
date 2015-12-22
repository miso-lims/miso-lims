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
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Libraries
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface LibraryStore extends Store<Library>, Remover<Library>, NamingSchemeAware<Library> {

  /**
   * Get a Library given a ID barcode
   * 
   * @param barcode
   *          of type String
   * @return Library
   * @throws IOException
   *           when
   */
  Library getByBarcode(String barcode) throws IOException;

  /**
   * List all Libraries that match a search criteria
   * 
   * @param query
   *          of type String
   * @return Collection<Library>
   * @throws IOException
   *           when
   */
  Collection<Library> listBySearch(String query) throws IOException;

  /**
   * Get a Library given a Library alias
   * 
   * @param alias
   *          of type String
   * @return Library
   * @throws IOException
   *           when
   */
  Library getByAlias(String alias) throws IOException;

  /**
   * List all Libraries generated from a Sample given a parent Sample ID
   * 
   * @param sampleId
   *          of type long
   * @return Collection<Library>
   * @throws IOException
   *           when
   */
  Collection<Library> listBySampleId(long sampleId) throws IOException;

  /**
   * List all Libraries that are related to a Project given a Project ID
   * 
   * @param projectId
   *          of type long
   * @return Collection<Library>
   * @throws IOException
   *           when
   */
  Collection<Library> listByProjectId(long projectId) throws IOException;

  /**
   * Get a LibraryType given a LibraryType ID
   * 
   * @param libraryTypeId
   *          of type long
   * @return LibraryType
   * @throws IOException
   *           when
   */
  LibraryType getLibraryTypeById(long libraryTypeId) throws IOException;

  /**
   * Get a LibraryType given a LibraryType description
   * 
   * @param description
   *          of type String
   * @return LibraryType
   * @throws IOException
   *           when
   */
  LibraryType getLibraryTypeByDescription(String description) throws IOException;

  /**
   * Get a LibraryType given a LibraryType description and platform
   * 
   * @param description
   *          of type String
   * @param platformType
   *          of type PlatformType
   * @return LibraryType
   * @throws IOException
   *           when
   */
  LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException;

  /**
   * Get a LibrarySelectionType given a LibrarySelectionType ID
   * 
   * @param librarySelectionTypeId
   *          of type long
   * @return LibrarySelectionType
   * @throws IOException
   *           when
   */
  LibrarySelectionType getLibrarySelectionTypeById(long librarySelectionTypeId) throws IOException;

  /**
   * Get a LibrarySelectionType given a LibrarySelectionType name
   * 
   * @param name
   *          of type String
   * @return LibrarySelectionType
   * @throws IOException
   *           when
   */
  LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException;

  /**
   * Get a LibraryStrategyType given a LibraryStrategyType ID
   * 
   * @param libraryStrategyTypeId
   *          of type long
   * @return LibraryStrategyType
   * @throws IOException
   *           when
   */
  LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException;

  /**
   * Get a LibraryStrategyType given a LibraryStrategyType name
   * 
   * @param name
   *          of type String
   * @return LibraryStrategyType
   * @throws IOException
   *           when
   */
  LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException;

  /**
   * Get a TagBarcode given a TagBarcode ID
   * 
   * @param tagBarcodeId
   *          of type long
   * @return TagBarcode
   * @throws IOException
   *           when
   */
  TagBarcode getTagBarcodeById(long tagBarcodeId) throws IOException;

  /**
   * List all LibraryTypes
   * 
   * @return Collection<LibraryType>
   * @throws IOException
   *           when
   */
  Collection<LibraryType> listAllLibraryTypes() throws IOException;

  /**
   * List all LibraryTypes available to a given platform
   * 
   * @param platformName
   *          of type String
   * @return Collection<LibraryType>
   * @throws IOException
   *           when
   */
  Collection<LibraryType> listLibraryTypesByPlatform(String platformName) throws IOException;

  /**
   * List all LibrarySelectionTypes
   * 
   * @return Collection<LibrarySelectionType>
   * @throws IOException
   *           when
   */
  Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException;

  /**
   * List all LibraryStrategyTypes
   * 
   * @return Collection<LibraryStrategyType>
   * @throws IOException
   *           when
   */
  Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException;

  /**
   * List all TagBarcodes
   * 
   * @return Collection<LibraryStrategyType>
   * @throws IOException
   *           when
   */
  Collection<TagBarcode> listAllTagBarcodes() throws IOException;

  /**
   * List all TagBarcodes by a given Platform
   * 
   * @return Collection<TagBarcode>
   * @throws IOException
   *           when
   */
  Collection<TagBarcode> listTagBarcodesByPlatform(String platformName) throws IOException;

  /**
   * List all TagBarcodes in a given strategy
   * 
   * @return Collection<TagBarcode>
   * @throws IOException
   *           when
   */
  Collection<TagBarcode> listTagBarcodesByStrategyName(String strategyName) throws IOException;

  /**
   * List all libraries related to a given LibraryDilution given a LibraryDilution ID
   * 
   * @param dilutionId
   *          of type long
   * @return Collection<Library>
   * @throws IOException
   *           when
   */
  Collection<Library> listByLibraryDilutionId(long dilutionId) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Library>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Library> listAllWithLimit(long limit) throws IOException;

  /**
   * Return the Library associated with a given positionId
   * 
   * @param positionId
   *          of type long
   * @return Boxable
   */
  Boxable getByPositionId(long positionId);

  /**
   * List all libraries associated with an identificationBarcode from given list of
   * identificationBarcodes (from scan)
   * @param barcodeList
   * @return Collection<Library>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Library> getByBarcodeList(List<String> barcodeList) throws IOException;
}
