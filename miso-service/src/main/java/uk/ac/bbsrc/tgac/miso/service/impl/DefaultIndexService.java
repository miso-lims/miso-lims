package uk.ac.bbsrc.tgac.miso.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.store.IndexStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultIndexService implements IndexService {
  @Autowired
  private IndexStore indexStore;

  public void setIndexStore(IndexStore indexStore) {
    this.indexStore = indexStore;
  }

  public DefaultIndexService() {
    System.out.println(this.hashCode());
  }

  @Override
  public IndexFamily getIndexFamilyByName(String name) {
    return indexStore.getIndexFamilyByName(name);
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
  public Index getIndexById(long id) {
    return indexStore.getIndexById(id);
  }

  @Override
  public Collection<Index> listAllIndices(PlatformType platformType) {
    return indexStore.listAllIndices(platformType);
  }

}
