package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.validateUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.store.StorageLocationStore;
import uk.ac.bbsrc.tgac.miso.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultStorageLocationService implements StorageLocationService {

  @Autowired
  private StorageLocationStore storageLocationStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  public StorageLocationStore getStorageLocationStore() {
    return storageLocationStore;
  }

  public void setStorageLocationStore(StorageLocationStore storageLocationStore) {
    this.storageLocationStore = storageLocationStore;
  }

  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
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
  public StorageLocation getFreezerForBarcodedStorageLocation(String barcode) {
    for (StorageLocation current = getByBarcode(barcode); current != null; current = current.getParentLocation()) {
      if (current.getLocationUnit() == LocationUnit.FREEZER) {
        return current;
      }
    }
    return null;
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
  public long createRoom(StorageLocation room) throws IOException {
    if (room.getLocationUnit() != LocationUnit.ROOM) {
      throw new IllegalArgumentException("Location is not a room");
    }
    room.setChangeDetails(authorizationManager.getCurrentUser());
    if (!room.isSaved()) {
      return storageLocationStore.save(room);
    } else {
      throw new IllegalArgumentException("Can not yet update rooms");
    }
  }

  @Override
  public long saveFreezer(StorageLocation freezer) throws IOException {
    if (freezer.getLocationUnit() != LocationUnit.FREEZER) {
      throw new IllegalArgumentException("Location is not a freezer");
    }
    loadChildEntities(freezer);
    if (!freezer.isSaved()) {
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

  private long create(StorageLocation location) throws IOException {
    location.setParentLocation(storageLocationStore.get(location.getParentLocation().getId()));
    validateChange(location, null);
    createParentIfNecessary(location);
    location.setChangeDetails(authorizationManager.getCurrentUser());
    return storageLocationStore.save(location);
  }

  private long update(StorageLocation location) throws IOException {
    StorageLocation managed = get(location.getId());
    location.setParentLocation(storageLocationStore.get(location.getParentLocation().getId()));
    validateChange(location, managed);
    createParentIfNecessary(location);
    applyChanges(location, managed);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
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

    if (storage.getIdentificationBarcode() != null
        && (beforeChange == null || !storage.getIdentificationBarcode().equals(beforeChange.getIdentificationBarcode()))
        && storageLocationStore.getByBarcode(storage.getIdentificationBarcode()) != null) {
      errors.add(new ValidationError("identificationBarcode",
          String.format("There is already a storage location with this barcode (%s)", storage.getIdentificationBarcode())));
    }

    validateLocationUnitRelationships(storage, errors);
    validateUrl("mapUrl", storage.getMapUrl(), true, errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateLocationUnitRelationships(StorageLocation storage, List<ValidationError> errors) {
    if (storage.getParentLocation() != null
        && !storage.getLocationUnit().getAcceptableParents().contains(storage.getParentLocation().getLocationUnit())) {
      errors.add(new ValidationError(String.format("%s is not an appropriate container for %s",
          storage.getParentLocation().getLocationUnit().getDisplayName(), storage.getLocationUnit().getDisplayName())));
      for (StorageLocation child : storage.getChildLocations()) {
        validateLocationUnitRelationships(child, errors);
      }
    }
  }

  private void createParentIfNecessary(StorageLocation freezer) throws IOException {
    if (freezer.getParentLocation() != null && !freezer.getParentLocation().isSaved()) {
      freezer.getParentLocation().setChangeDetails(authorizationManager.getCurrentUser());
      long parentId = storageLocationStore.save(freezer.getParentLocation());
      freezer.setParentLocation(storageLocationStore.get(parentId));
    }
  }

  private void applyChanges(StorageLocation from, StorageLocation to) {
    to.setAlias(from.getAlias());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
    to.setParentLocation(from.getParentLocation());
    to.setMapUrl(from.getMapUrl());
    to.setProbeId(from.getProbeId());
  }

  @Override
  public long addFreezerStorage(StorageLocation storage) throws IOException {
    validateChange(storage, null);
    loadChildEntities(storage);
    storage.setChangeDetails(authorizationManager.getCurrentUser());
    long savedId = storageLocationStore.save(storage);
    StorageLocation[] childLocations = storage.getChildLocations().toArray(new StorageLocation[storage.getChildLocations().size()]);
    for (StorageLocation child : childLocations) {
      addFreezerStorage(child);
    }

    return savedId;
  }

  @Override
  public long updateStorageComponent(StorageLocation location) throws IOException {
    return update(location);
  }

}
