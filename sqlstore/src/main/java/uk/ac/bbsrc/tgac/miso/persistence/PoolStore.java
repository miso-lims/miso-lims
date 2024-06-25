package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Pools
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface PoolStore extends SaveDao<Pool>, PaginatedDataSource<Pool> {

  /**
   * @param libraryId
   * @return a list all Pools that are related to a given
   *         {@link uk.ac.bbsrc.tgac.miso.core.data.Library} by means of that Library's
   *         {@link LibraryAliquot} objects
   * @throws IOException
   */
  List<Pool> listByLibraryId(long libraryId) throws IOException;

  List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException;

  /**
   * List the Pool associated with a given identificationBarcode
   * 
   * @param barcode of type String
   * @return Pool
   * @throws IOException
   */
  Pool getByBarcode(String barcode) throws IOException;

  Pool getByAlias(String alias) throws IOException;

  List<Pool> listByIdList(List<Long> poolIds);

  public long getPartitionCount(Pool pool);

}
