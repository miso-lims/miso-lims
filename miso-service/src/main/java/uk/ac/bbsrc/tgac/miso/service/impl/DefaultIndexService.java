package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.store.IndexStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultIndexService implements IndexService {
  @Autowired
  private IndexStore indexStore;

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return indexStore.count(errorHandler, filter);
  }

  @Override
  public Index getIndexById(long id) {
    return indexStore.getIndexById(id);
  }

  @Override
  public Collection<IndexFamily> getIndexFamilies() {
    return indexStore.getIndexFamilies();
  }

  @Override
  public Collection<IndexFamily> getIndexFamiliesByPlatform(PlatformType platformType) {
    return indexStore.getIndexFamiliesByPlatform(platformType);
  }

  @Override
  public IndexFamily getIndexFamilyByName(String name) {
    return indexStore.getIndexFamilyByName(name);
  }

  @Override
  public List<Index> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol, PaginationFilter... filter)
      throws IOException {
    return indexStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  public void setIndexStore(IndexStore indexStore) {
    this.indexStore = indexStore;
  }

}
