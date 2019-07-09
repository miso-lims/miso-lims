package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.service.ListPoolViewService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ListPoolViewDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultListPoolViewService implements ListPoolViewService {

  @Autowired
  private ListPoolViewDao listPoolViewDao;

  public ListPoolViewDao getListPoolViewDao() {
    return listPoolViewDao;
  }

  public void setListPoolViewDao(ListPoolViewDao listPoolViewDao) {
    this.listPoolViewDao = listPoolViewDao;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return listPoolViewDao.count(errorHandler, filter);
  }

  @Override
  public List<ListPoolView> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return listPoolViewDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
