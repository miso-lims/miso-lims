package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ServiceRecordService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLabelService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationMapService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLocationStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultStorageLocationService implements StorageLocationService {

  @Autowired
  private StorageLocationStore storageLocationStore;
  @Autowired
  private StorageLocationMapService mapService;
  @Autowired
  private StorageLabelService storageLabelService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private ServiceRecordService serviceRecordService;

  @Autowired
  private DeletionStore deletionStore;

  public StorageLocationStore getStorageLocationStore() {
    return storageLocationStore;
  }

  public void setStorageLocationStore(StorageLocationStore storageLocationStore) {
    this.storageLocationStore = storageLocationStore;
  }

  @Override
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

  private void loadChildEntities(StorageLocation storage) throws IOException {
    if (storage.getParentLocation() != null && storage.getParentLocation().isSaved()) {
      storage.setParentLocation(get(storage.getParentLocation().getId()));
    }
    ValidationUtils.loadChildEntity(storage::setMap, storage.getMap(), mapService, "map");
    ValidationUtils.loadChildEntity(storage::setLabel, storage.getLabel(), storageLabelService, "label");
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

  public long addServiceRecord(ServiceRecord record, StorageLocation location) throws ValidationException, IOException {
    StorageLocation managedLocation = get(location.getId());

    if (location.getRetired()) {
      throw new ValidationException("Cannot add service records to a retired storage location!");
    }

    long recordId = serviceRecordService.create(record);
    ServiceRecord managedRecord = serviceRecordService.get(recordId);
    managedLocation.getServiceRecords().add(managedRecord);
    storageLocationStore.save(managedLocation);

    return managedRecord.getId();
  }

  public StorageLocation getByServiceRecord(ServiceRecord record) throws IOException {
    return storageLocationStore.getByServiceRecord(record);
  }

  /**
   * Checks submitted data for validity, throwing a ValidationException containing all of the errors
   * if invalid
   * 
   * @param storage submitted StorageLocation to validate
   * @param beforeChange the already-persisted StorageLocation before changes
   * @throws IOException
   */
  private void validateChange(StorageLocation storage, StorageLocation beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (storage.getIdentificationBarcode() != null
        && (beforeChange == null || !storage.getIdentificationBarcode().equals(beforeChange.getIdentificationBarcode()))
        && storageLocationStore.getByBarcode(storage.getIdentificationBarcode()) != null) {
      errors.add(new ValidationError("identificationBarcode",
          String.format("There is already a storage location with this barcode (%s)",
              storage.getIdentificationBarcode())));
    }
    if (ValidationUtils.isSetAndChanged(StorageLocation::getProbeId, storage, beforeChange)
        && storageLocationStore.getByProbeId(storage.getProbeId()) != null) {
      errors.add(ValidationError.forDuplicate("Freezer", "probeId", "probe ID"));
    }

    validateLocationUnitRelationships(storage, errors);

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
    to.setProbeId(from.getProbeId());
    to.setRetired(from.getRetired());
    to.setMap(from.getMap());
    to.setMapAnchor(from.getMapAnchor());
    to.setLabel(from.getLabel());
  }

  @Override
  public long addFreezerStorage(StorageLocation storage) throws IOException {
    validateChange(storage, null);
    loadChildEntities(storage);
    storage.setChangeDetails(authorizationManager.getCurrentUser());
    long savedId = storageLocationStore.save(storage);
    StorageLocation[] childLocations =
        storage.getChildLocations().toArray(new StorageLocation[storage.getChildLocations().size()]);
    for (StorageLocation child : childLocations) {
      addFreezerStorage(child);
    }

    return savedId;
  }

  @Override
  public long updateStorageComponent(StorageLocation location) throws IOException {
    return update(location);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(StorageLocation object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  private static final Set<LocationUnit> DELETABLE_FROM = Collections
      .unmodifiableSet(Sets.newHashSet(LocationUnit.ROOM, LocationUnit.FREEZER, LocationUnit.SHELF));

  @Override
  public ValidationResult validateDeletion(StorageLocation object) throws IOException {
    ValidationResult result = new ValidationResult();
    if (object.getLocationUnit() == LocationUnit.ROOM) {
      if (!object.getChildLocations().isEmpty()) {
        result.addError(new ValidationError(
            String.format("Room %s contains %d %s", object.getAlias(), object.getChildLocations().size(),
                Pluralizer.freezers(object.getChildLocations().size()))));
      }
    } else {
      if (object.getParentLocation() != null
          && !DELETABLE_FROM.contains(object.getParentLocation().getLocationUnit())) {
        result.addError(new ValidationError("This storage unit cannot be deleted directly"));
      } else {
        int boxCount = object.countBoxes();
        if (boxCount > 0) {
          result.addError(
              new ValidationError(String.format("%s %s contains %d %s", object.getLocationUnit().getDisplayName(),
                  object.getAlias(), boxCount, Pluralizer.boxes(boxCount))));
        }
      }
    }
    return result;
  }

}
