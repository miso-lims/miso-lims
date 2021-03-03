package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibaryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.service.ListLibraryAliquotViewService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ListLibraryAliquotViewDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultListLibraryAliquotViewService implements ListLibraryAliquotViewService {

  @Autowired
  private ListLibraryAliquotViewDao listLibraryAliquotViewDao;

  public void setListLibraryAliquotViewDao(ListLibraryAliquotViewDao dao) {
    this.listLibraryAliquotViewDao = dao;
  }

  @Override
  public ListLibaryAliquotView get(Long aliquotId) throws IOException {
    return listLibraryAliquotViewDao.get(aliquotId);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return listLibraryAliquotViewDao.count(errorHandler, filter);
  }

  @Override
  public List<ListLibaryAliquotView> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return listLibraryAliquotViewDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<ListLibaryAliquotView> listByIdList(List<Long> aliquotIds) throws IOException {
    return listLibraryAliquotViewDao.listByIdList(aliquotIds);
  }

}
