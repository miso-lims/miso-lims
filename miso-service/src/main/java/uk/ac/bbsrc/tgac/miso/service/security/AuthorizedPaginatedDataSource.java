package uk.ac.bbsrc.tgac.miso.service.security;

import java.io.IOException;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

/**
 * Filters a data source based on read access.
 */
@Transactional(rollbackFor = Exception.class)
public interface AuthorizedPaginatedDataSource<T extends SecurableByProfile, Filter extends PaginationFilter>
    extends PaginatedDataSource<T, Filter> {

  @Override
  public default long count(Filter filter) throws IOException {
    return getBackingPaginationSource().count(filter);
  }

  abstract AuthorizationManager getAuthorizationManager();


  abstract PaginatedDataSource<T, Filter> getBackingPaginationSource();

  @Override
  public default List<T> list(Filter filter, int offset, int limit, boolean sortDir, String sortCol) throws IOException {
    return getAuthorizationManager().filterUnreadable(getBackingPaginationSource().list(filter, offset, limit, sortDir, sortCol));
  }

}
