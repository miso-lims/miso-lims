package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderCompletionService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderCompletionDao;

@Transactional(rollbackFor = Exception.class)
@Service

public class DefaultSequencingOrderCompletionService implements SequencingOrderCompletionService {
  @Autowired
  private SequencingOrderCompletionDao sequencingOrderCompletionDao;

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return sequencingOrderCompletionDao.count(errorHandler, filter);
  }

  @Override
  public List<SequencingOrderCompletion> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return sequencingOrderCompletionDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<SequencingOrderCompletion> listByPoolId(Long poolId) throws IOException {
    return sequencingOrderCompletionDao.list(0, 100, false, "remaining", PaginationFilter.pool(poolId));
  }
}
