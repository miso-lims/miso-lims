package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractBox;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation.BoxStorageAmount;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.BoxChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
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
  private ChangeLogStore changeLogStore;

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
    changeLogStore.create(changeLog);
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
    User currentUser = authorizationManager.getCurrentUser();
    boxStore.discardAllContents(managed, currentUser);
  }

  @Override
  public void discardSingleItem(Box box, String position) throws IOException {
    authorizationManager.throwIfNotWritable(box);
    User currentUser = authorizationManager.getCurrentUser();
    boxStore.discardSingleItem(box, position, currentUser);
  }

  @Override
  public Box get(long boxId) throws IOException {
    Box o = boxStore.get(boxId);
    authorizationManager.throwIfNotReadable(o);
    return o;
  }

  private Box getDetached(long boxId) throws IOException {
    Box o = boxStore.getDetached(boxId);
    authorizationManager.throwIfNotReadable(o);
    return o;
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
  public Box getByBarcode(String barcode) throws IOException {
    Box o = boxStore.getByBarcode(barcode);
    authorizationManager.throwIfNotReadable(o);
    return o;
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
  public Collection<BoxSize> listSizes() throws IOException {
    return boxStore.listAllBoxSizes();
  }

  @Override
  public Collection<BoxUse> listUses() throws IOException {
    return boxStore.listAllBoxUses();
  }

  @Override
  public long save(Box box) throws IOException {
    setChangeDetails(box);
    if (box.getStorageLocation() != null) {
      box.setStorageLocation(storageLocationService.get(box.getStorageLocation().getId()));
    }
    if (box.getId() == AbstractBox.UNSAVED_ID) {
      return saveNewBox(box);
    } else {
      Box original = boxStore.get(box.getId());
      authorizationManager.throwIfNotWritable(original);
      applyChanges(box, original);
      validateChange(box, original);
      StringBuilder message = new StringBuilder();

      // get persisted version of new box contents before change
      List<BoxableId> ids = box.getBoxables().values()
          .stream()
          .map(b -> b.getId())
          .collect(Collectors.toList());
      Map<BoxableId, BoxableView> oldOccupants = boxStore.getBoxableViewsByIdList(ids)
          .stream()
          .collect(Collectors.toMap(BoxableView::getId, b -> b));

      // Process additions/moves
      Set<BoxableId> handled = Sets.newHashSet();
      for (Map.Entry<String, BoxableView> entry : box.getBoxables().entrySet()) {
        BoxableView previousOccupant = original.getBoxable(entry.getKey());
        BoxableView newOccupant = entry.getValue();
        handled.add(newOccupant.getId());

        if (previousOccupant != null && newOccupant.getId().equals(previousOccupant.getId())) {
          // Unchanged
          continue;
        }
        if (message.length() > 0) {
          message.append("\n");
        }

        BoxableView oldOccupant = oldOccupants.get(newOccupant.getId());
        if (oldOccupant.getBoxId() != null) {
          if (oldOccupant.getBoxId().longValue() == box.getId()) {
            // Moved within same box
            message.append(String.format("Relocated %s (%s) from %s to %s", oldOccupant.getAlias(), oldOccupant.getName(),
                oldOccupant.getBoxPosition(), entry.getKey()));
          } else {
            // Moved from a different box
            message.append(String.format("Moved %s (%s) from %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(),
                oldOccupant.getBoxAlias(), oldOccupant.getBoxName(), entry.getKey()));

            Box oldHome = boxStore.get(oldOccupant.getBoxId());
            String oldHomeMessage = String.format("Moved %s (%s) to %s (%s)", oldOccupant.getAlias(), oldOccupant.getName(),
                original.getAlias(), original.getName());
            addBoxContentsChangeLog(oldHome, oldHomeMessage);
          }
          boxStore.removeBoxableFromBox(oldOccupant);
        } else {
          message.append(String.format("Added %s (%s) to %s", oldOccupant.getAlias(), oldOccupant.getName(), entry.getKey()));
        }
      }

      // Process removals
      for (Map.Entry<String, BoxableView> entry : original.getBoxables().entrySet()) {
        if (box.getBoxables().keySet().contains(entry.getKey()) || handled.contains(entry.getValue().getId())) {
          // Already handled. Only checking for removals at this point
          continue;
        }
        if (message.length() > 0) {
          message.append("\n");
        }
        BoxableView oldItem = entry.getValue();
        message.append(String.format("Removed %s (%s)", oldItem.getAlias(), oldItem.getName()));
      }

      // Needs to be a new map to force Hibernate to delete all associations before inserting
      // (prevent violation of unique constraint when position-swapping two items)
      original.setBoxables(new HashMap<>(box.getBoxables()));

      if (message.length() > 0) {
        addBoxContentsChangeLog(original, message.toString());
      }
      setChangeDetails(original);
      return boxStore.save(original);
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
      setChangeDetails(box);
      box.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(box.getIdentificationBarcode()));
      validateChange(box, null);
      boxStore.save(box);

      if (autoGenerateIdBarcodes) {
        box.setIdentificationBarcode(box.getName() + "::" + box.getAlias());
      }
      box.setName(namingScheme.generateNameFor(box));
      validateNameOrThrow(box, namingScheme);
      return boxStore.save(box);
    } catch (MisoNamingException e) {
      throw new IOException("Invalid name for box", e);
    }
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

  public void setChangeLogStore(ChangeLogStore changeLogStore) {
    this.changeLogStore = changeLogStore;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  /**
   * Updates all user data and timestamps associated with the change. Existing timestamps will be preserved
   * if the Box is unsaved, and they are already set
   * 
   * @param box the Box to update
   * @param preserveTimestamps if true, the creationTime and lastModified date are not updated
   * @throws IOException
   */
  private void setChangeDetails(Box box) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Date now = new Date();
    box.setLastModifier(user);

    if (box.getId() == Sample.UNSAVED_ID) {
      box.setCreator(user);
      if (box.getCreationTime() == null) {
        box.setCreationTime(now);
      }
      if (box.getLastModified() == null) {
        box.setLastModified(now);
      }
    } else {
      box.setLastModified(now);
    }
  }

  @Override
  public void updateBoxableLocation(Boxable boxable) throws IOException {
    if (boxable.isDiscarded()) {
      boxable.removeFromBox();
    }
    if (boxable.getBox() != null && boxable.getBoxPosition() == null) {
      throw new IllegalArgumentException("Box position missing");
    } else if (boxable.getBoxPosition() != null && (boxable.getBox() == null || boxable.getBox().getId() == AbstractBox.UNSAVED_ID)) {
      throw new IllegalArgumentException("Box position set, but no box specified");
    }
    BoxableView managed = getBoxableView(new BoxableId(boxable.getEntityType(), boxable.getId()));
    if (managed.getBoxId() != null && boxable.getBox() == null) {
      Box box = getDetached(managed.getBoxId());
      box.removeBoxable(managed.getBoxPosition());
      save(box);
    } else if (boxable.getBox() != null && (
        managed.getBoxId() == null
            || managed.getBoxId().longValue() != boxable.getBox().getId()
            || !managed.getBoxPosition().equals(boxable.getBoxPosition())
        )) {
      Box box = getDetached(boxable.getBox().getId());
      if (box.getBoxable(boxable.getBoxPosition()) != null) {
        throw new IllegalArgumentException(String.format("Box position already occupied: %s %s", box.getName(), boxable.getBoxPosition()));
      }
      box.setBoxable(boxable.getBoxPosition(), BoxableView.fromBoxable(boxable));
      save(box);
    }
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

}
