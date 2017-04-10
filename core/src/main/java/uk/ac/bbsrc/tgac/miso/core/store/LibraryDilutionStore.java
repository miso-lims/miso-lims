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
import uk.ac.bbsrc.tgac.miso.core.util.DilutionPaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Dilutions
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface LibraryDilutionStore
    extends Store<LibraryDilution>, Remover<LibraryDilution>, PaginatedDataSource<LibraryDilution, DilutionPaginationFilter> {
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
   * Get a LibraryDilution by ID barcode
   *
   * @param barcode
   *          of type String
   * @return LibraryDilution
   * @throws IOException
   *           when
   */
  LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException;

  Collection<LibraryDilution> getByBarcodeList(List<String> barcodeList) throws IOException;
}
