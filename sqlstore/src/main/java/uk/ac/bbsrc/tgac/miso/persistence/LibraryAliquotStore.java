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

  List<LibraryAliquot> listByIdList(Collection<Long> idList) throws IOException;

  List<LibraryAliquot> listByPoolIds(Collection<Long> poolIds) throws IOException;

  /**
   * Get a LibraryAliquot by ID barcode
   *
   * @param barcode
   * @return the matching LibraryAliquot
   * @throws IOException
   */
  LibraryAliquot getByBarcode(String barcode) throws IOException;

  long getUsageByPoolOrders(LibraryAliquot aliquot) throws IOException;

  long getUsageByChildAliquots(LibraryAliquot parent) throws IOException;

}
