package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.store.StorageLocationStore;
import uk.ac.bbsrc.tgac.miso.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;

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
  public List<StorageLocation> listRooms() {
    return storageLocationStore.listRooms();
  }

  @Override
  public List<StorageLocation> listFreezers() {
    return storageLocationStore.listFreezers();
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return storageLocationStore.getColumnSizes();
  }

  @Override
  public long createRoom(StorageLocation room) {
    if (room.getLocationUnit() != LocationUnit.ROOM) {
      throw new IllegalArgumentException("Location is not a room");
    }
    if (room.getId() == StorageLocation.UNSAVED_ID) {
      return storageLocationStore.save(room);
    } else {
      throw new IllegalArgumentException("Can not yet update rooms");
    }
  }

  @Override
  public long saveFreezer(StorageLocation freezer) {
    if (freezer.getLocationUnit() != LocationUnit.FREEZER) {
      throw new IllegalArgumentException("Location is not a freezer");
    }
    loadChildEntities(freezer);
    if (freezer.getId() == StorageLocation.UNSAVED_ID) {
      return create(freezer);
    } else {
      return update(freezer);
    }
  }

  private void loadChildEntities(StorageLocation storage) {
    if (storage.getParentLocation() != null && storage.getParentLocation().isSaved()) {
      storage.setParentLocation(get(storage.getParentLocation().getId()));
    }
  }

  private long create(StorageLocation freezer) {
    validateChange(freezer, null);
    createParentIfNecessary(freezer);
    return storageLocationStore.save(freezer);
  }

  private long update(StorageLocation freezer) {
    StorageLocation managed = get(freezer.getId());
    validateChange(freezer, managed);
    createParentIfNecessary(freezer);
    applyChanges(freezer, managed);
    return storageLocationStore.save(managed);
  }

  /**
   * Checks submitted data for validity, throwing a ValidationException containing all of the errors if invalid
   * 
   * @param storage submitted StorageLocation to validate
   * @param beforeChange the already-persisted StorageLocation before changes
   */
  private void validateChange(StorageLocation storage, StorageLocation beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    // TODO: validate all LocationUnit relationships

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void createParentIfNecessary(StorageLocation freezer) {
    if (freezer.getParentLocation() != null && !freezer.getParentLocation().isSaved()) {
      long parentId = storageLocationStore.save(freezer.getParentLocation());
      freezer.setParentLocation(storageLocationStore.get(parentId));
    }
  }

  private void applyChanges(StorageLocation from, StorageLocation to) {
    to.setAlias(from.getAlias());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setParentLocation(from.getParentLocation());
  }

  @Override
  public long addFreezerStorage(StorageLocation storage) {
    validateChange(storage, null);
    loadChildEntities(storage);
    long savedId = storageLocationStore.save(storage);
    storage.getChildLocations().forEach(this::addFreezerStorage);
    return savedId;
  }

}
