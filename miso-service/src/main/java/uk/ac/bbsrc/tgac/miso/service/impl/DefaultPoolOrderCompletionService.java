package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderCompletionDao;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service

public class DefaultPoolOrderCompletionService implements PoolOrderCompletionService {
  @Autowired
  private PoolOrderCompletionDao poolOrderCompletionDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return poolOrderCompletionDao.count(errorHandler, filter);
  }

  @Override
  public List<PoolOrderCompletion> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return authorizationManager.filterUnreadable(poolOrderCompletionDao.list(errorHandler, offset, limit, sortDir, sortCol, filter),
        x -> x.getPool());
  }

  @Override
  public List<PoolOrderCompletion> getByPoolId(Long poolId) throws IOException {
    return authorizationManager.filterUnreadable(poolOrderCompletionDao.list(0, 100, false, "remaining", PaginationFilter.pool(poolId)),
        x -> x.getPool());
  }
}
