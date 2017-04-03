package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.List;

/**
 * Retrieve a subset of items from a data source (Hibernate) one page at a time without fetching the whole collection.
 */
public interface PaginatedDataSource<T, Filter extends PaginationFilter> {
  /**
   * Count the total number of items in the underlying collection that match the restrictions set.
   */
  public long count(Filter filter) throws IOException;

  /**
   * Retrieve a subset of the collection.
   * 
   * @param offset the index of the first element to retrieve
   * @param limit the maximum number of items to retrieve. The implementation may return less even if more are available on the next page.
   * @param sortDir the sorting direction (true for ascending, false for descending)
   */
  public List<T> list(Filter filter, int offset, int limit, boolean sortDir, String sortCol)
      throws IOException;

}
