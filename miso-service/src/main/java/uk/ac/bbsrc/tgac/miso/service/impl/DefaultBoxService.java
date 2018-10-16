package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.validateBarcodeUniqueness;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Functions;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.BoxStorageAmount;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizedPaginatedDataSource;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultBoxService implements BoxService, AuthorizedPaginatedDataSource<Box> {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Autowired
  private StorageLocationService storageLocationService;

  @Autowired
  private BoxStore boxStore;

  @Autowired
  private ChangeLogService changeLogService;

  @Autowired
  private NamingScheme namingScheme;

  @Autowired
  private SecurityProfileStore securityProfileStore;

  @Autowired
  private DeletionStore deletionStore;

  private void addBoxContentsChangeLog(Box box, String message) throws IOException {
    BoxChangeLog changeLog = new BoxChangeLog();
    changeLog.setBox(box);
    changeLog.setTime(new Date());
    changeLog.setColumnsChanged("contents");
    changeLog.setUser(authorizationManager.getCurrentUser());
    changeLog.setSummary(message);
    changeLogService.create(changeLog);
  }

  private void applyChanges(Box from, Box to) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(from.getIdentificationBarcode()));
    to.setLocationBarcode(from.getLocationBarcode());
    to.setUse(boxStore.getUseById(from.getUse().getId()));
    to.setStorageLocation(from.getStorageLocation());
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return boxStore.count(errorHandler, filter);
  }

  @Override
  public void discardAllContents(Box box) throws IOException {
    Box managed = get(box.getId());
    authorizationManager.throwIfNotWritable(managed);
    addBoxContentsChangeLog(managed, String.format("Discarded all box contents (%d items)", managed.getBoxPositions().size()));
    for (BoxPosition bp : managed.getBoxPositions().values()) {
      discardBoxable(bp.getBoxableId());
    }
    managed.getBoxPositions().clear();
    boxStore.save(managed);
  }

  @Override
  public void discardSingleItem(Box box, String position) throws IOException {
    Box managed = boxStore.get(box.getId());
    authorizationManager.throwIfNotWritable(managed);
    BoxPosition bp = managed.getBoxPositions().get(position);
    if (bp == null) {
      throw new IllegalArgumentException("No item in the specified box position");
    }
    Boxable target = boxStore.getBoxable(bp.getBoxableId());
    addBoxContentsChangeLog(managed, String.format("Discarded %s (%s)", target.getAlias(), target.getName()));
    discardBoxable(bp.getBoxableId());
    managed.getBoxPositions().remove(position);
    boxStore.save(managed);
  }

  private void discardBoxable(BoxableId id) throws IOException {
    Boxable target = boxStore.getBoxable(id);
    discardBoxable(target);
  }

  private void discardBoxable(Boxable target) throws IOException {
    target.setDiscarded(true);
    target.setLastModified(new Date());
    target.setLastModifier(authorizationManager.getCurrentUser());
    boxStore.saveBoxable(target);
  }

  @Override
  public Box get(long boxId) throws IOException {
    Box o = boxStore.get(boxId);
    authorizationManager.throwIfNotReadable(o);
    return o;
  }

  @Override
  public List<Box> listByIdList(List<Long> idList) throws IOException {
    List<Box> boxes = boxStore.getByIdList(idList);
    for (Box box : boxes) {
      authorizationManager.throwIfNotReadable(box);
    }
    return boxes;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public PaginatedDataSource<Box> getBackingPaginationSource() {
    return boxStore;
  }

  @Override
  public Box getByAlias(String alias) throws IOException {
    Box o = boxStore.getBoxByAlias(alias);
    authorizationManager.throwIfNotReadable(o);
    return o;
  }

  @Override
  public List<BoxableView> getBoxContents(long id) throws IOException {
    return boxStore.getBoxContents(id);
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return boxStore.getBoxColumnSizes();
  }

  @Override
  public BoxableView getBoxableView(BoxableId id) throws IOException {
    return boxStore.getBoxableView(id);
  }

  @Override
  public List<BoxableView> getBoxableViewsBySearch(String search) {
    return boxStore.getBoxableViewsBySearch(search);
  }

  @Override
  public Collection<BoxableView> getViewsFromBarcodeList(Collection<String> barcodeList) throws IOException {
    return boxStore.getBoxableViewsByBarcodeList(barcodeList);
  }

  @Override
  public List<Box> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol, PaginationFilter... filter)
      throws IOException {
    return authorizationManager.filterUnreadable(boxStore.list(errorHandler, offset, limit, sortDir, sortCol, filter));
  }

  @Override
  public List<Box> getBySearch(String search) {
    return boxStore.getBySearch(search);
  }

  @Override
  public List<Box> getByPartialSearch(String search, boolean onlyMatchBeginning) {
    return boxStore.getByPartialSearch(search, onlyMatchBeginning);
  }

  @Override
  public Collection<BoxSize> listSizes() throws IOException {
    return boxStore.listAllBoxSizes();
  }

  @Override
  public BoxSize getSize(long id) throws IOException {
    return boxStore.getSizeById(id);
  }

  @Override
  public Collection<BoxUse> listUses() throws IOException {
    return boxStore.listAllBoxUses();
  }

  @Override
  public BoxUse getUse(long id) throws IOException {
    return boxStore.getUseById(id);
  }

  @Override
  public long save(Box box) throws IOException {
    box.setChangeDetails(authorizationManager.getCurrentUser());
    if (box.getStorageLocation() != null) {
      box.setStorageLocation(storageLocationService.get(box.getStorageLocation().getId()));
    } else {
      box.setStorageLocation(null);
    }
    loadChildEntities(box);
    if (!box.isSaved()) {
      return saveNewBox(box);
    } else {
      Box managed = boxStore.get(box.getId());
      authorizationManager.throwIfNotWritable(managed);
      logStorageChange(box, managed);
      validateChange(box, managed);
      applyChanges(box, managed);
      StringBuilder message = new StringBuilder();

      // get persisted version of new box contents before change
      List<BoxableId> ids = box.getBoxPositions().values()
          .stream()
          .map(BoxPosition::getBoxableId)
          .collect(Collectors.toList());
      Map<BoxableId, BoxableView> oldOccupants = boxStore.getBoxableViewsByIdList(ids)
          .stream()
          .collect(Collectors.toMap(BoxableView::getId, Functions.identity()));

      // Process additions/moves
      Set<BoxPosition> movedWithinBox = Sets.newHashSet();
      List<BoxableView> movedFromOtherBoxes = new ArrayList<>();
      for (Map.Entry<String, BoxPosition> entry : box.getBoxPositions().entrySet()) {
        BoxPosition managedPos = managed.getBoxPositions().get(entry.getKey());
        BoxPosition newPos = entry.getValue();

        if (managedPos != null && newPos.getBoxableId().equals(managedPos.getBoxableId())) {
          // Unchanged
          continue;
        }
        if (message.length() > 0) {
          message.append("\n");
        }

        BoxableView oldOccupant = oldOccupants.get(newPos.getBoxableId());
        if (oldOccupant.getBoxId() != null) {
          if (oldOccupant.getBoxId().longValue() == box.getId()) {
            // Moved within same box
            message.append(String.format("Relocated %s (%s) from %s to %s", oldOccupant.getAlias(), oldOccupant.getName(),
                oldOccupant.getBoxPosition(), entry.getKey()));
            movedWithinBox.add(newPos);
          } else {
            // Moved from a different box
            message.append(String.format("Moved %s (%s) from %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(),
                oldOccupant.getBoxAlias(), oldOccupant.getBoxName(), entry.getKey()));
            movedFromOtherBoxes.add(oldOccupant);
          }
        } else {
          message.append(String.format("Added %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(), entry.getKey()));
        }
      }

      // Process removals
      List<BoxableId> removedIds = new ArrayList<>();
      List<BoxableId> movedWithinBoxIds = movedWithinBox.stream().map(BoxPosition::getBoxableId).collect(Collectors.toList());
      for (Map.Entry<String, BoxPosition> entry : managed.getBoxPositions().entrySet()) {
        if (box.getBoxPositions().keySet().contains(entry.getKey())
            && box.getBoxPositions().get(entry.getKey()).getBoxableId().equals(entry.getValue().getBoxableId())) {
          // Already handled. Only checking for removals at this point
          continue;
        }
        removedIds.add(entry.getValue().getBoxableId());
      }
      List<BoxableView> removed = boxStore.getBoxableViewsByIdList(removedIds);
      for (BoxableView v : removed) {
        if (!movedWithinBoxIds.contains(v.getId())) {
          if (message.length() > 0) {
            message.append("\n");
          }
          message.append(String.format("Removed %s (%s)", v.getAlias(), v.getName()));
        }
      }

      for (BoxableView v : movedFromOtherBoxes) {
        boxStore.removeBoxableFromBox(v);
      }
      
      movedWithinBox.forEach(bp -> managed.getBoxPositions().remove(bp.getPosition()));
      removed.forEach(boxable -> managed.getBoxPositions().remove(boxable.getBoxPosition()));
      if (!movedWithinBox.isEmpty() || !removed.isEmpty()) {
        boxStore.save(managed);
      }

      for (String pos : box.getBoxPositions().keySet()) {
        if (!managed.getBoxPositions().containsKey(pos)
            || !managed.getBoxPositions().get(pos).getBoxableId().equals(box.getBoxPositions().get(pos).getBoxableId())) {
          managed.getBoxPositions().put(pos, new BoxPosition(managed, pos, box.getBoxPositions().get(pos).getBoxableId()));
        }
      }

      if (message.length() > 0) {
        addBoxContentsChangeLog(managed, message.toString());
      }
      managed.setChangeDetails(authorizationManager.getCurrentUser());
      return boxStore.save(managed);
    }
  }

  private void loadChildEntities(Box box) throws IOException {
    if (box.getSize() != null) {
      box.setSize(getSize(box.getSize().getId()));
    }
    box.setUse(getUse(box.getUse().getId()));
    if (box.getStorageLocation() != null) {
      if (box.getStorageLocation().getId() > 0L) {
        box.setStorageLocation(storageLocationService.get(box.getStorageLocation().getId()));
      } else if (!LimsUtils.isStringEmptyOrNull(box.getStorageLocation().getIdentificationBarcode())) {
        box.setStorageLocation(storageLocationService.getByBarcode(box.getStorageLocation().getIdentificationBarcode()));
      }
    }
  }

  /**
   * Checks submitted data for validity, throwing a ValidationException containing all of the errors if invalid
   * 
   * @param box submitted Box to validate
   * @param beforeChange the already-persisted Box before changes
   * @throws IOException
   */
  private void validateChange(Box box, Box beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (isStringEmptyOrNull(box.getAlias())) {
      errors.add(new ValidationError("alias", "Alias cannot be blank"));
    } else if (beforeChange == null || !box.getAlias().equals(beforeChange.getAlias())) {
      Box existing = boxStore.getBoxByAlias(box.getAlias());
      if (existing != null && existing.getId() != box.getId()) {
        errors.add(new ValidationError("alias", "There is already a box with this alias"));
      }
    }

    validateBarcodeUniqueness(box, beforeChange, boxStore::getBoxByBarcode, errors, "box");

    if (box.getStorageLocation() != null) {
      if (box.getStorageLocation().getLocationUnit().getBoxStorageAmount() == BoxStorageAmount.NONE) {
        errors.add(new ValidationError("storageLocation", "Invalid box location"));
      } else if (box.getStorageLocation().getLocationUnit().getBoxStorageAmount() == BoxStorageAmount.SINGLE) {
        if (beforeChange == null || beforeChange.getStorageLocation() == null
            || beforeChange.getStorageLocation().getId() != box.getStorageLocation().getId()) {
          box.getStorageLocation().getBoxes().forEach(b -> {
            if (b.getId() != box.getId()) {
              errors.add(new ValidationError("storageLocation", "Location already occupied"));
            }
          });
        }
      }
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private long saveNewBox(Box box) throws IOException {
    authorizationManager.throwIfNotWritable(box);
    try {
      box.setName(generateTemporaryName());
      box.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(box.getSecurityProfile())));
      box.setChangeDetails(authorizationManager.getCurrentUser());
      box.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(box.getIdentificationBarcode()));
      validateChange(box, null);
      boxStore.save(box);

      if (autoGenerateIdBarcodes) {
        box.setIdentificationBarcode(box.getName() + "::" + box.getAlias());
      }
      box.setName(namingScheme.generateNameFor(box));
      validateNameOrThrow(box, namingScheme);

      if (box.getStorageLocation() != null) {
        addStorageChangeLog(getFreezer(box), box, true);
      }

      return boxStore.save(box);
    } catch (MisoNamingException e) {
      throw new IOException("Invalid name for box", e);
    }
  }

  private void logStorageChange(Box box, Box original) throws IOException {
    if (original.getStorageLocation() == null && box.getStorageLocation() != null) {
      addStorageChangeLog(getFreezer(box), box, true);
    } else if (original.getStorageLocation() != null && box.getStorageLocation() == null) {
      addStorageChangeLog(getFreezer(original), box, false);
    } else if (original.getStorageLocation() != null && box.getStorageLocation() != null) {
      if (getFreezer(original).getId() != getFreezer(box).getId()) {
        addStorageChangeLog(getFreezer(original), box, false);
        addStorageChangeLog(getFreezer(box), box, true);
      } else if (original.getStorageLocation().getId() != box.getStorageLocation().getId()) {
        String message = String.format("Relocated %s (%s) from %s to %s", box.getAlias(), box.getName(),
            original.getStorageLocation().getFreezerDisplayLocation(), box.getStorageLocation().getFreezerDisplayLocation());
        ChangeLog change = getFreezer(box).createChangeLog(message, "", authorizationManager.getCurrentUser());
        changeLogService.create(change);
      }
    }
  }

  private StorageLocation getFreezer(Box box) {
    if (box.getStorageLocation() == null) {
      throw new IllegalArgumentException(String.format("%s (%s) does not have a storage location", box.getAlias(), box.getName()));
    }
    StorageLocation location = box.getStorageLocation().getFreezerLocation();
    if (location == null) {
      throw new IllegalArgumentException(String.format("Location %s does not have a parent freezer", box.getStorageLocation().getAlias()));
    }
    return location;
  }

  /**
   * Add a changelog entry to record a Box added to or removed from a StorageLocation
   * 
   * @param location the StorageLocation that box was added to or removed from
   * @param box the Box that was added or removed
   * @param addition true for addition; false for removal
   * @throws IOException
   */
  private void addStorageChangeLog(StorageLocation location, Box box, boolean addition) throws IOException {
    if (location.getLocationUnit() != LocationUnit.FREEZER) {
      throw new IllegalArgumentException(String.format("%s is not a freezer", location.getAlias()));
    }
    String message = addition ?
        String.format("Added %s (%s) to %s", box.getAlias(), box.getName(), box.getStorageLocation().getFreezerDisplayLocation())
        : (String.format("Removed %s (%s)", box.getAlias(), box.getName()));
    ChangeLog change = location.createChangeLog(message, "", authorizationManager.getCurrentUser());
    changeLogService.create(change);
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setAutoGenerateIdBarcodes(Boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  public void setBoxStore(BoxStore boxStore) {
    this.boxStore = boxStore;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  @Override
  public void updateBoxableLocation(Boxable boxable) throws IOException {
    BoxableId boxableId = new BoxableId(boxable.getEntityType(), boxable.getId());
    Boxable managedOriginal = boxStore.getBoxable(boxableId);
    if (boxable.getBox() == null && managedOriginal.getBox() == null) {
      return;
    }
    if ((boxable.getBox() != null && managedOriginal.getBox() != null && boxable.getBox().getId() == managedOriginal.getBox().getId()
        && boxable.getBoxPosition().equals(managedOriginal.getBoxPosition()))) {
      // Note: for new items, boxable.box.boxPosition[boxable.boxPosition] may not match boxable.boxPosition because it doesn't (and we
      // don't want it to) cascade update. boxable.boxPosition stays as it is because Hibernate won't overwrite the changes you've made to
      // the object with the current session. boxable.box should be a refreshed object with the correct, persisted boxPositions though.
      BoxPosition temp = managedOriginal.getBox().getBoxPositions().get(managedOriginal.getBoxPosition());
      if (temp != null && temp.getBoxableId().equals(boxableId)) {
        return;
      }
    }
    if (managedOriginal.getBox() != null) {
      Box box = get(managedOriginal.getBox().getId());
      box.getBoxPositions().remove(managedOriginal.getBoxPosition());
      addBoxContentsChangeLog(managedOriginal.getBox(),
          String.format("Removed %s (%s)", managedOriginal.getAlias(), managedOriginal.getName()));
      box.setChangeDetails(authorizationManager.getCurrentUser());
      boxStore.save(box);
    }
    if (boxable.getBox() != null) {
      Box managedNew = boxStore.get(boxable.getBox().getId());
      addBoxContentsChangeLog(managedNew,
          String.format("Added %s (%s) to %s", boxable.getAlias(), boxable.getName(), boxable.getBoxPosition()));
      managedNew.getBoxPositions().put(boxable.getBoxPosition(),
          new BoxPosition(managedNew, boxable.getBoxPosition(), boxable.getEntityType(), boxable.getId()));
      managedNew.setChangeDetails(authorizationManager.getCurrentUser());
      boxStore.save(managedNew);
    }
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void throwIfBoxPositionIsFilled(Boxable boxable) throws IOException {
    if (boxable.getBox() == null || boxable.getBoxPosition() == null) return;
    Box box = get(boxable.getBox().getId());
    BoxPosition bp = box.getBoxPositions().get(boxable.getBoxPosition());
    if (bp == null) return;
    if (!bp.getBoxableId().equals(new BoxableId(boxable.getEntityType(), boxable.getId()))) {
      throw new IllegalArgumentException("Box position '" + boxable.getBoxPosition() + "' in box '" + box.getAlias() + "' is not empty");
    }
  }

  @Override
  public void beforeDelete(Box object) throws IOException {
    object.getBoxPositions().clear();
    save(object);
  }

  @Override
  public void authorizeDeletion(Box object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

}
