package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderSummaryView;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderSummaryViewService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderSummaryViewDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingOrderSummaryViewService implements SequencingOrderSummaryViewService {

  @Autowired
  private SequencingOrderSummaryViewDao sequencingOrderSummaryViewDao;

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return sequencingOrderSummaryViewDao.count(errorHandler, filter);
  }

  @Override
  public List<SequencingOrderSummaryView> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return sequencingOrderSummaryViewDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<SequencingOrderSummaryView> listByPoolId(Long poolId) throws IOException {
    return sequencingOrderSummaryViewDao.list(0, 0, false, "lastUpdated", PaginationFilter.pool(poolId));
  }

}
