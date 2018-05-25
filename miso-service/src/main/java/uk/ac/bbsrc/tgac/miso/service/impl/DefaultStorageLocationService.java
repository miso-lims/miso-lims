package uk.ac.bbsrc.tgac.miso.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.store.StorageLocationStore;
import uk.ac.bbsrc.tgac.miso.service.StorageLocationService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultStorageLocationService implements StorageLocationService {

  @Autowired
  private StorageLocationStore storageLocationStore;

  public StorageLocationStore getStorageLocationStore() {
    return storageLocationStore;
  }

  public void setStorageLocationStore(StorageLocationStore storageLocationStore) {
    this.storageLocationStore = storageLocationStore;
  }

  @Override
  public StorageLocation get(long id) {
    return storageLocationStore.get(id);
  }

  @Override
  public StorageLocation getByBarcode(String barcode) {
    return storageLocationStore.getByBarcode(barcode);
  }

  @Override
  public List<StorageLocation> listFreezers() {
    return storageLocationStore.listFreezers();
  }

}
