package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizedPaginatedDataSource;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultPoolService implements PoolService, AuthorizedPaginatedDataSource<Pool> {

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private PoolStore poolStore;
  @Autowired
  private SecurityProfileStore securityProfileStore;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private PoolOrderService poolOrderService;

  public void setAutoGenerateIdBarcodes(boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setPoolStore(PoolStore poolStore) {
    this.poolStore = poolStore;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

  public void setPoolableElementViewService(PoolableElementViewService poolableElementViewService) {
    this.poolableElementViewService = poolableElementViewService;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public PaginatedDataSource<Pool> getBackingPaginationSource() {
    return poolStore;
  }

  @Override
  public Collection<Pool> listBySearch(String query) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(null, query, null);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listWithLimit(int limit) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(null, null, limit);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> list() throws IOException {
    Collection<Pool> pools = poolStore.listAll();
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listByPlatform(PlatformType platformType) throws IOException {
    Collection<Pool> pools = poolStore.listAllByCriteria(platformType, null, null);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(platformType, query, null);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listByProjectId(long projectId) throws IOException {
    Collection<Pool> pools = poolStore.listByProjectId(projectId);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listByLibraryId(long libraryId) throws IOException {
    Collection<Pool> pools = poolStore.listByLibraryId(libraryId);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public void deleteNote(Pool pool, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Pool managed = poolStore.get(pool.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Pool " + pool.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    poolStore.save(managed);
  }

  @Override
  public long save(Pool pool) throws IOException {
    if (pool.isDiscarded()) {
      pool.setVolume(0.0);
    }
    if (pool.getConcentration() == null) {
      pool.setConcentrationUnits(null);
    }
    if (pool.getVolume() == null) {
      pool.setVolumeUnits(null);
    }

    long savedId;
    if (!pool.isSaved()) {
      pool.setName(generateTemporaryName());
      loadSecurityProfile(pool);
      loadPoolDilutions(pool.getPoolDilutions(), pool);
      pool.setChangeDetails(authorizationManager.getCurrentUser());
      boxService.throwIfBoxPositionIsFilled(pool);
      validateChange(pool, null);
      poolStore.save(pool);

      if (autoGenerateIdBarcodes) {
        LimsUtils.generateAndSetIdBarcode(pool);
      }
      try {
        pool.setName(namingScheme.generateNameFor(pool));
        validateNameOrThrow(pool, namingScheme);
      } catch (MisoNamingException e) {
        throw new IOException("Invalid name for pool", e);
      }
      savedId = poolStore.save(pool);
    } else {
      Pool managed = poolStore.get(pool.getId());
      authorizationManager.throwIfNotWritable(managed);
      boxService.throwIfBoxPositionIsFilled(pool);
      validateChange(pool, managed);
      managed.setAlias(pool.getAlias());
      managed.setConcentration(pool.getConcentration());
      managed.setConcentrationUnits(pool.getConcentrationUnits());
      managed.setDescription(pool.getDescription());
      managed.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(pool.getIdentificationBarcode()));
      managed.setPlatformType(pool.getPlatformType());
      managed.setQcPassed(pool.getQcPassed());
      managed.setVolume(pool.getVolume());
      managed.setVolumeUnits(pool.getVolumeUnits());
      managed.setDiscarded(pool.isDiscarded());
      managed.setCreationDate(pool.getCreationDate());

      Set<String> originalItems = extractDilutionNames(managed.getPoolDilutions());
      loadPoolDilutions(pool, managed);
      Set<String> updatedItems = extractDilutionNames(managed.getPoolDilutions());

      Set<String> added = new TreeSet<>(updatedItems);
      added.removeAll(originalItems);
      Set<String> removed = new TreeSet<>(originalItems);
      removed.removeAll(updatedItems);

      managed.setChangeDetails(authorizationManager.getCurrentUser());
      if (!added.isEmpty() || !removed.isEmpty()) {
        StringBuilder message = new StringBuilder();
        message.append("Items");
        LimsUtils.appendSet(message, added, "added");
        LimsUtils.appendSet(message, removed, "removed");

        PoolChangeLog changeLog = new PoolChangeLog();
        changeLog.setPool(managed);
        changeLog.setColumnsChanged("contents");
        changeLog.setSummary(message.toString());
        changeLog.setTime(new Date());
        changeLog.setUser(managed.getLastModifier());
        changeLogService.create(changeLog);
      }
      savedId = poolStore.save(managed);
    }
    boxService.updateBoxableLocation(pool);
    return savedId;
  }

  private void validateChange(Pool pool, Pool beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    validateConcentrationUnits(pool.getConcentration(), pool.getConcentrationUnits(), errors);
    validateVolumeUnits(pool.getVolume(), pool.getVolumeUnits(), errors);
    validateBarcodeUniqueness(pool, beforeChange, poolStore::getByBarcode, errors, "pool");

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void loadSecurityProfile(Pool pool) throws IOException {
    if (pool.getSecurityProfile() == null) {
      pool.setSecurityProfile(new SecurityProfile(authorizationManager.getCurrentUser()));
    }
    if (pool.getSecurityProfile().getProfileId() == SecurityProfile.UNSAVED_ID) {
      pool.getSecurityProfile().setProfileId(securityProfileStore.save(pool.getSecurityProfile()));
    }
    pool.setSecurityProfile(securityProfileStore.get(pool.getSecurityProfile().getProfileId()));
  }

  private void loadPoolDilutions(Collection<PoolDilution> source, Pool target) throws IOException {
    Set<PoolDilution> targetDilutions = target.getPoolDilutions();
    targetDilutions.removeIf(notInOther(source));
    Set<PoolDilution> additions = source.stream()
        .filter(notInOther(targetDilutions))
        .collect(Collectors.toSet());
    for (PoolDilution sourcePd : additions) {
      PoolableElementView v = poolableElementViewService.get(sourcePd.getPoolableElementView().getDilutionId());
      if (v == null) {
        throw new IllegalStateException("Pool contains an unsaved dilution");
      }
      targetDilutions.add(new PoolDilution(target, v, sourcePd.getProportion()));
    }
    for (PoolDilution targetPd : targetDilutions) {
      PoolDilution sourcePd = source.stream()
          .filter(spd -> spd.getPoolableElementView().getDilutionId() == targetPd.getPoolableElementView().getDilutionId())
          .findFirst().orElse(null);
      if (sourcePd != null) {
        targetPd.setProportion(sourcePd.getProportion());
      }
    }
  }

  private Predicate<PoolDilution> notInOther(Collection<PoolDilution> otherCollection) {
    return t -> otherCollection.stream()
        .noneMatch(other -> other.getPoolableElementView().getDilutionId() == t.getPoolableElementView().getDilutionId());
  }

  private void loadPoolDilutions(Pool source, Pool target) throws IOException {
    loadPoolDilutions(source.getPoolDilutions(), target);
  }

  private Set<String> extractDilutionNames(Set<PoolDilution> dilutions) {
    Set<String> original = new HashSet<>();
    for (PoolDilution dilution : dilutions) {
      original.add(dilution.getPoolableElementView().getDilutionName());
    }
    return original;
  }

  @Override
  public void addNote(Pool pool, Note note) throws IOException {
    Pool managed = poolStore.get(pool.getId());
    authorizationManager.throwIfNotWritable(managed);
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    poolStore.save(managed);
  }

  @Override
  public Pool get(long poolId) throws IOException {
    Pool pool = poolStore.get(poolId);
    authorizationManager.throwIfNotReadable(pool);
    return pool;
  }

  @Override
  public Pool getByBarcode(String barcode) throws IOException {
    Pool pool = poolStore.getByBarcode(barcode);
    authorizationManager.throwIfNotReadable(pool);
    return pool;
  }

  @Override
  public Map<String, Integer> getPoolColumnSizes() throws IOException {
    return poolStore.getPoolColumnSizes();
  }

  @Override
  public void addPoolWatcher(Pool pool, User watcher) throws IOException {
    User managedWatcher = securityManager.getUserById(watcher.getUserId());
    Pool managedPool = poolStore.get(pool.getId());
    authorizationManager.throwIfNotReadable(managedPool);
    authorizationManager.throwIfNotReadable(managedPool, managedWatcher);
    poolStore.addWatcher(pool, watcher);
  }

  @Override
  public void removePoolWatcher(Pool pool, User watcher) throws IOException {
    User managedWatcher = securityManager.getUserById(watcher.getUserId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(managedWatcher);
    poolStore.removeWatcher(pool, managedWatcher);
  }

  @Override
  public Collection<Pool> listByIdList(List<Long> poolIds) throws IOException {
    return authorizationManager.filterUnreadable(poolStore.listPoolsById(poolIds));
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(Pool object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public void beforeDelete(Pool object) throws IOException {
    Set<PoolOrder> orders = poolOrderService.getByPool(object.getId());
    poolOrderService.bulkDelete(orders);

    Box box = object.getBox();
    if (box != null) {
      box.getBoxPositions().remove(object.getBoxPosition());
      boxService.save(box);
    }
  }

  @Override
  public ValidationResult validateDeletion(Pool object) {
    ValidationResult result = new ValidationResult();

    long usage = poolStore.getPartitionCount(object);
    if (usage > 0L) {
      result.addError(new ValidationError("Pool '" + object.getName() + "' has been added to " + usage + " partitions"));
    }

    return result;
  }

}
