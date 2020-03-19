package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListTransferView;
import uk.ac.bbsrc.tgac.miso.core.service.ListTransferViewService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ListTransferViewDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultListTransferViewService implements ListTransferViewService {

  @Autowired
  private ListTransferViewDao listTransferViewDao;

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return listTransferViewDao.count(errorHandler, filter);
  }

  @Override
  public List<ListTransferView> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return listTransferViewDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
