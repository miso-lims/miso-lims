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
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Library Aliquots
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface LibraryAliquotStore extends Store<LibraryAliquot>, PaginatedDataSource<LibraryAliquot> {

  /**
   * @param libraryId
   * @return list of all LibraryAliquots by a given parent library ID
   * @throws IOException
   */
  List<LibraryAliquot> listByLibraryId(long libraryId) throws IOException;

  List<LibraryAliquot> listByIdList(List<Long> idList) throws IOException;

  /**
   * Get a LibraryAliquot by ID barcode
   *
   * @param barcode
   * @return the matching LibraryAliquot
   * @throws IOException
   */
  LibraryAliquot getByBarcode(String barcode) throws IOException;

  Collection<LibraryAliquot> getByBarcodeList(Collection<String> barcodeList) throws IOException;
}
