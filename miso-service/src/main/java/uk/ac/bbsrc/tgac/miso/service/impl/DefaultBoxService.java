package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.springframework.transaction.support.TransactionTemplate;

import com.eaglegenomics.simlims.core.User;
import com.google.common.base.Functions;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.BoxStorageAmount;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.LocationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.box.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BarcodableReferenceService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxSizeService;
import uk.ac.bbsrc.tgac.miso.core.service.BoxUseService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.BoxStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultBoxService implements BoxService {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Autowired
  private BoxStore boxStore;
  @Autowired
  private StorageLocationService storageLocationService;
  @Autowired
  private BoxUseService boxUseService;
  @Autowired
  private BoxSizeService boxSizeService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private BarcodableReferenceService barcodableReferenceService;

  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Autowired
  private DeletionStore deletionStore;

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

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
    to.setUse(from.getUse());
    to.setSize(from.getSize());
    to.setStorageLocation(from.getStorageLocation());
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return boxStore.count(errorHandler, filter);
  }

  @Override
  public void discardAllContents(Box box) throws IOException {
    Box managed = get(box.getId());
    addBoxContentsChangeLog(managed,
        String.format("Discarded all box contents (%d items)", managed.getBoxPositions().size()));
    for (BoxPosition bp : managed.getBoxPositions().values()) {
      discardBoxable(bp.getBoxableId());
    }
    managed.getBoxPositions().clear();
    boxStore.save(managed);
  }

  @Override
  public void discardSingleItem(Box box, String position) throws IOException {
    Box managed = boxStore.get(box.getId());
    BoxPosition bp = managed.getBoxPositions().get(position);
    if (bp == null) {
      throw new IllegalArgumentException("No item in the specified box position");
    }
    Boxable target = boxStore.getBoxable(bp.getBoxableId());
    addBoxContentsChangeLog(managed,
        String.format("Discarded %s (%s) from %s", target.getAlias(), target.getName(), target.getBoxPosition()));
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
    return o;
  }

  @Override
  public List<Box> listByIdList(List<Long> idList) throws IOException {
    List<Box> boxes = boxStore.listByIdList(idList);
    return boxes;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }


  @Override
  public Box getByAlias(String alias) throws IOException {
    Box o = boxStore.getBoxByAlias(alias);
    return o;
  }

  @Override
  public List<BoxableView> getBoxContents(long id) throws IOException {
    return boxStore.getBoxContents(id);
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
  public List<Box> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return boxStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
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
  public long create(Box box) throws IOException {
    box.setChangeDetails(authorizationManager.getCurrentUser());
    if (box.getStorageLocation() != null) {
      box.setStorageLocation(storageLocationService.get(box.getStorageLocation().getId()));
    } else {
      box.setStorageLocation(null);
    }
    loadChildEntities(box);
    return saveNewBox(box);
  }

  @Override
  public long update(Box box) throws IOException {
    box.setChangeDetails(authorizationManager.getCurrentUser());
    if (box.getStorageLocation() != null) {
      box.setStorageLocation(storageLocationService.get(box.getStorageLocation().getId()));
    } else {
      box.setStorageLocation(null);
    }
    loadChildEntities(box);
    Box managed = boxStore.get(box.getId());
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
        .collect(Collectors.toMap(view -> new BoxableId(view.getEntityType(), view.getId()), Functions.identity()));

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
      addBoxableChangelog(oldOccupant, oldOccupant.getBoxAlias(), oldOccupant.getBoxName(),
          oldOccupant.getBoxPosition(), newPos, box.getLastModifier());
      if (oldOccupant.getBoxId() != null) {
        if (oldOccupant.getBoxId().longValue() == box.getId()) {
          // Moved within same box
          message.append(String.format("Relocated %s (%s) from %s to %s", oldOccupant.getAlias(), oldOccupant.getName(),
              oldOccupant.getBoxPosition(), entry.getKey()));
          movedWithinBox.add(newPos);
        } else {
          // Moved from a different box
          Box oldBox = get(oldOccupant.getBoxId());
          addBoxContentsChangeLog(oldBox,
              String.format("Removed %s (%s) from %s to %s (%s)", oldOccupant.getAlias(), oldOccupant.getName(),
                  oldOccupant.getBoxPosition(), managed.getAlias(), managed.getName()));
          message
              .append(String.format("Moved %s (%s) from %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(),
                  oldOccupant.getBoxAlias(), oldOccupant.getBoxName(), entry.getKey()));
          movedFromOtherBoxes.add(oldOccupant);
        }
      } else {
        message.append(
            String.format("Added %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(), entry.getKey()));
      }
    }

    // Process removals
    List<BoxableId> removedIds = new ArrayList<>();
    List<BoxableId> movedWithinBoxIds =
        movedWithinBox.stream().map(BoxPosition::getBoxableId).collect(Collectors.toList());
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
      if (!movedWithinBoxIds.contains(new BoxableId(v.getEntityType(), v.getId()))) {
        if (message.length() > 0) {
          message.append("\n");
        }
        addBoxableChangelog(v, v.getBoxAlias(), v.getBoxName(), v.getBoxPosition(), null, box.getLastModifier());
        message.append(String.format("Removed %s (%s) from %s", v.getAlias(), v.getName(), v.getBoxPosition()));
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
        managed.getBoxPositions().put(pos,
            new BoxPosition(managed, pos, box.getBoxPositions().get(pos).getBoxableId()));
      }
    }

    if (message.length() > 0) {
      addBoxContentsChangeLog(managed, message.toString());
    }
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    return boxStore.save(managed);
  }

  private void addBoxableChangelog(BoxableView boxable, String oldBoxAlias, String oldBoxName, String oldBoxPosition,
      BoxPosition newPosition, User user) throws IOException {
    if (newPosition == null) {
      addBoxableChangelog(boxable, oldBoxAlias, oldBoxName, oldBoxPosition, null, null, null, null);
    } else {
      addBoxableChangelog(boxable, oldBoxAlias, oldBoxName, oldBoxPosition, newPosition.getBox().getAlias(),
          newPosition.getBox().getName(), newPosition.getPosition(), user);
    }
  }

  private void addBoxableChangelog(BoxableView boxable, String oldBoxAlias, String oldBoxName, String oldBoxPosition,
      String newBoxAlias, String newBoxName, String newBoxPosition, User user) throws IOException {
    ChangeLog change = boxable.makeChangeLog();
    change.setColumnsChanged("box position");
    change.setSummary(
        "Box location: %s → %s".formatted(makeBoxableLocationString(oldBoxAlias, oldBoxName, oldBoxPosition),
            makeBoxableLocationString(newBoxAlias, newBoxName, newBoxPosition)));
    change.setUser(user);
    changeLogService.create(change);
  }

  private String makeBoxableLocationString(String boxAlias, String boxName, String boxPosition) {
    return boxPosition == null ? "n/a" : "%s (%s) %s".formatted(boxAlias, boxName, boxPosition);
  }

  @Override
  public long save(Box box) throws IOException {
    return box.isSaved() ? update(box) : create(box);
  }

  private void loadChildEntities(Box box) throws IOException {
    if (box.getSize() != null) {
      box.setSize(boxSizeService.get(box.getSize().getId()));
    }
    box.setUse(boxUseService.get(box.getUse().getId()));
    if (box.getStorageLocation() != null) {
      if (box.getStorageLocation().getId() > 0L) {
        box.setStorageLocation(storageLocationService.get(box.getStorageLocation().getId()));
      } else if (!LimsUtils.isStringEmptyOrNull(box.getStorageLocation().getIdentificationBarcode())) {
        box.setStorageLocation(
            storageLocationService.getByBarcode(box.getStorageLocation().getIdentificationBarcode()));
      }
    }
  }

  /**
   * Checks submitted data for validity, throwing a ValidationException containing all of the errors
   * if invalid
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

    validateBarcodeUniqueness(box, beforeChange, barcodableReferenceService, errors);

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

    if (beforeChange != null && isChanged(Box::getSize, box, beforeChange) && !box.getBoxPositions().isEmpty()) {
      errors.add(new ValidationError("sizeId", "Size can only be changed when the box is empty"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private long saveNewBox(Box box) throws IOException {
    try {
      box.setName(generateTemporaryName());
      box.setChangeDetails(authorizationManager.getCurrentUser());
      box.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(box.getIdentificationBarcode()));
      validateChange(box, null);
      boxStore.save(box);

      if (autoGenerateIdBarcodes) {
        box.setIdentificationBarcode(box.getName() + "::" + box.getAlias());
      }
      NamingScheme namingScheme = namingSchemeHolder.getPrimary();
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
            original.getStorageLocation().getFreezerDisplayLocation(),
            box.getStorageLocation().getFreezerDisplayLocation());
        ChangeLog change = getFreezer(box).createChangeLog(message, "", authorizationManager.getCurrentUser());
        changeLogService.create(change);
      }
    }
  }

  private StorageLocation getFreezer(Box box) {
    if (box.getStorageLocation() == null) {
      throw new IllegalArgumentException(
          String.format("%s (%s) does not have a storage location", box.getAlias(), box.getName()));
    }
    StorageLocation location = box.getStorageLocation().getFreezerLocation();
    if (location == null) {
      throw new IllegalArgumentException(
          String.format("Location %s does not have a parent freezer", box.getStorageLocation().getAlias()));
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
    String message = addition
        ? String.format("Added %s (%s) to %s", box.getAlias(), box.getName(),
            box.getStorageLocation().getFreezerDisplayLocation())
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

  public void setBoxUseService(BoxUseService boxUseService) {
    this.boxUseService = boxUseService;
  }

  public void setBoxSizeService(BoxSizeService boxSizeService) {
    this.boxSizeService = boxSizeService;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  public void prepareBoxableLocation(Boxable pendingBoxable, boolean existingDistributionTransfer) throws IOException {
    if (pendingBoxable.isDiscarded() || pendingBoxable.getDistributionTransfer() != null
        || existingDistributionTransfer) {
      pendingBoxable.removeFromBox();
      pendingBoxable.setVolume(BigDecimal.ZERO);
    }

    throwIfBoxPositionIsFilled(pendingBoxable);
    pendingBoxable.moveBoxPositionToPending();
  }

  @Override
  public void updateBoxableLocation(Boxable pendingBoxable) throws IOException {
    if (pendingBoxable.getBox() != null || pendingBoxable.getBoxPosition() != null) {
      throw new IllegalStateException(
          "Box position not moved to pending. prepareBoxableLocation should be called first.");
    }

    BoxableId boxableId = new BoxableId(pendingBoxable.getEntityType(), pendingBoxable.getId());
    Boxable managedOriginalBoxable = boxStore.getBoxable(boxableId);
    if (pendingBoxable.getPendingBoxId() == null && managedOriginalBoxable.getBox() == null) {
      return;
    }
    if ((pendingBoxable.getPendingBoxId() != null && managedOriginalBoxable.getBox() != null
        && pendingBoxable.getPendingBoxId().longValue() == managedOriginalBoxable.getBox().getId()
        && pendingBoxable.getPendingBoxPosition().equals(managedOriginalBoxable.getBoxPosition()))) {
      return;
    }
    if (managedOriginalBoxable.getBox() != null) {
      Box box = get(managedOriginalBoxable.getBox().getId());
      box.getBoxPositions().remove(managedOriginalBoxable.getBoxPosition());
      addBoxContentsChangeLog(managedOriginalBoxable.getBox(),
          String.format("Removed %s (%s) from %s", managedOriginalBoxable.getAlias(), managedOriginalBoxable.getName(),
              managedOriginalBoxable.getBoxPosition()));
      box.setChangeDetails(authorizationManager.getCurrentUser());
      boxStore.save(box);
    }
    if (pendingBoxable.getPendingBoxId() != null) {
      Box managedNewBox = boxStore.get(pendingBoxable.getPendingBoxId());
      addBoxContentsChangeLog(managedNewBox,
          String.format("Added %s (%s) to %s", managedOriginalBoxable.getAlias(), managedOriginalBoxable.getName(),
              pendingBoxable.getPendingBoxPosition()));
      managedNewBox.getBoxPositions().put(pendingBoxable.getPendingBoxPosition(),
          new BoxPosition(managedNewBox, pendingBoxable.getPendingBoxPosition(), pendingBoxable.getEntityType(),
              pendingBoxable.getId()));
      managedNewBox.setChangeDetails(authorizationManager.getCurrentUser());
      boxStore.save(managedNewBox);
    }
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  private void throwIfBoxPositionIsFilled(Boxable boxable) throws IOException {
    if (boxable.getBox() == null || boxable.getBoxPosition() == null) {
      return;
    }
    Box box = get(boxable.getBox().getId());
    BoxPosition bp = box.getBoxPositions().get(boxable.getBoxPosition());
    if (bp == null) {
      return;
    }
    if (!bp.getBoxableId().equals(new BoxableId(boxable.getEntityType(), boxable.getId()))) {
      throw new ValidationException(new ValidationError("boxPosition", "Position is not available"));
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
