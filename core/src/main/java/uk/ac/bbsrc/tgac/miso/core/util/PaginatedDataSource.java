package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Retrieve a subset of items from a data source (Hibernate) one page at a time without fetching the
 * whole collection.
 */
public interface PaginatedDataSource<T> {
  /**
   * Count the total number of items in the underlying collection that match the restrictions set.
   */
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException;

  public default long count(PaginationFilter... filter) throws IOException {
    return count(x -> {
    }, filter);
  }

  /**
   * Retrieve a subset of the collection.
   * 
   * @param offset the index of the first element to retrieve
   * @param limit the maximum number of items to retrieve. The implementation may return less even if
   *        more are available on the next page. Limit of 0 indicates "no limit"
   * @param ascending the sorting direction (true for ascending, false for descending)
   */
  public List<T> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException;

  public default List<T> list(int offset, int limit, boolean ascending, String sortCol, PaginationFilter... filter)
      throws IOException {
    return list(x -> {
    }, offset, limit, ascending, sortCol, filter);
  }

}
