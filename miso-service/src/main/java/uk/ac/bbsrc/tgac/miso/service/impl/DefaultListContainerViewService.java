package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListContainerView;
import uk.ac.bbsrc.tgac.miso.core.service.ListContainerViewService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ListContainerViewDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultListContainerViewService implements ListContainerViewService {

  @Autowired
  private ListContainerViewDao listContainerViewDao;

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return listContainerViewDao.count(errorHandler, filter);
  }

  @Override
  public List<ListContainerView> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return listContainerViewDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
