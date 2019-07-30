package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.generateTemporaryName;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.PoolStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultPoolService implements PoolService, PaginatedDataSource<Pool> {

  @Value("${miso.pools.strictIndexChecking:false}")
  private Boolean strictPools;

  @Value("${miso.autoGenerateIdentificationBarcodes}")
  private Boolean autoGenerateIdBarcodes;

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private PoolStore poolStore;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private PoolableElementViewService poolableElementViewService;
  @Autowired
  private SequencingOrderService sequencingOrderService;
  @Autowired
  private FileAttachmentService fileAttachmentService;

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

  public void setFileAttachmentService(FileAttachmentService fileAttachmentService) {
    this.fileAttachmentService = fileAttachmentService;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<Pool> listBySearch(String query) throws IOException {
    return poolStore.listAllByCriteria(null, query, null);
  }

  @Override
  public List<Pool> listWithLimit(int limit) throws IOException {
    return poolStore.listAllByCriteria(null, null, limit);
  }

  @Override
  public List<Pool> list() throws IOException {
    return poolStore.listAll();
  }

  @Override
  public List<Pool> listByPlatform(PlatformType platformType) throws IOException {
    return poolStore.listAllByCriteria(platformType, null, null);
  }

  @Override
  public List<Pool> listByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    return poolStore.listAllByCriteria(platformType, query, null);
  }

  @Override
  public List<Pool> listByProjectId(long projectId) throws IOException {
    return poolStore.listByProjectId(projectId);
  }

  @Override
  public List<Pool> listByLibraryId(long libraryId) throws IOException {
    return poolStore.listByLibraryId(libraryId);
  }

  @Override
  public List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException {
    return poolStore.listByLibraryAliquotId(aliquotId);
  }

  @Override
  public void deleteNote(Pool pool, Long noteId) throws IOException {
    if (noteId == null) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Pool managed = poolStore.get(pool.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getId() == noteId.longValue()) {
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
  public long create(Pool pool) throws IOException {
    if (pool.isDiscarded()) {
      pool.setVolume(0.0);
    }
    if (pool.getConcentration() == null) {
      pool.setConcentrationUnits(null);
    }
    if (pool.getVolume() == null) {
      pool.setVolumeUnits(null);
    }

    pool.setName(generateTemporaryName());
    loadPoolElements(pool.getPoolContents(), pool);
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
    long savedId = poolStore.save(pool);
    boxService.updateBoxableLocation(pool);
    return savedId;
  }

  @Override
  public long update(Pool pool) throws IOException {
    if (pool.isDiscarded()) {
      pool.setVolume(0.0);
    }
    if (pool.getConcentration() == null) {
      pool.setConcentrationUnits(null);
    }
    if (pool.getVolume() == null) {
      pool.setVolumeUnits(null);
    }
    Pool managed = poolStore.get(pool.getId());
    boxService.throwIfBoxPositionIsFilled(pool);
    validateChange(pool, managed);
    managed.setAlias(pool.getAlias());
    managed.setConcentration(pool.getConcentration());
    managed.setConcentrationUnits(pool.getConcentrationUnits());
    managed.setDescription(pool.getDescription());
    managed.setIdentificationBarcode(LimsUtils.nullifyStringIfBlank(pool.getIdentificationBarcode()));
    managed.setPlatformType(pool.getPlatformType());
    managed.setQcPassed(pool.getQcPassed());
    managed.setDiscarded(pool.isDiscarded());
    managed.setCreationDate(pool.getCreationDate());
    if (pool.isDiscarded() || pool.isDistributed()) {
      managed.setVolume(0.0);
    } else {
      managed.setVolume(pool.getVolume());
    }
    managed.setVolumeUnits(pool.getVolume() == null ? null : pool.getVolumeUnits());
    managed.setDistributed(pool.isDistributed());
    managed.setDistributionDate(pool.getDistributionDate());
    managed.setDistributionRecipient(pool.getDistributionRecipient());

    Set<String> originalItems = extractAliquotNames(managed.getPoolContents());
    loadPoolElements(pool, managed);
    Set<String> updatedItems = extractAliquotNames(managed.getPoolContents());

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
    long savedId = poolStore.save(managed);
    boxService.updateBoxableLocation(pool);
    return savedId;
  }

  private void refreshPoolElements(Pool pool) throws IOException {
    Set<PoolElement> pes = new HashSet<>();
    for(PoolElement oldPe : pool.getPoolContents()){
      PoolElement newPe = new PoolElement();
      newPe.setPool(pool);
      newPe.setProportion(oldPe.getProportion());
      newPe.setPoolableElementView(poolableElementViewService.get(oldPe.getPoolableElementView().getAliquotId()));
      pes.add(newPe);
    }
    pool.setPoolElements(pes);
  }

  private Set<String> getAllBadIndices(Pool pool){
    Set<String> indices = pool.getDuplicateIndicesSequences();
    indices.addAll(pool.getNearDuplicateIndicesSequences());
    return indices;
  }

  public void validateIndices(Pool pool, Pool beforeChange, Collection<ValidationError> errors) throws IOException {
    refreshPoolElements(pool, errors);
    Set<String> indices = getAllBadIndices(pool);
    Set<String> bcIndices = getAllBadIndices(beforeChange);

    if(indices.size() > bcIndices.size()){ // If this change introduces new conflicts
      String errorMessage = "Pools may not contain Library Aliquots with indices with 2 or fewer positions of difference, please address the following conflicts: ";
      indices.removeAll(bcIndices);

      for (String index : indices){
        errorMessage += index + " ";
      }

      errors.add(new ValidationError("poolElements", errorMessage));
    }
  }

  private void validateChange(Pool pool, Pool beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    validateConcentrationUnits(pool.getConcentration(), pool.getConcentrationUnits(), errors);
    validateVolumeUnits(pool.getVolume(), pool.getVolumeUnits(), errors);
    validateBarcodeUniqueness(pool, beforeChange, poolStore::getByBarcode, errors, "pool");
    if (strictPools) validateIndices(pool, beforeChange, errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void loadPoolElements(Collection<PoolElement> source, Pool target) throws IOException {
    Set<PoolElement> targetAliquots = target.getPoolContents();
    targetAliquots.removeIf(notInOther(source));
    Set<PoolElement> additions = source.stream()
        .filter(notInOther(targetAliquots))
        .collect(Collectors.toSet());
    for (PoolElement sourcePd : additions) {
      PoolableElementView v = poolableElementViewService.get(sourcePd.getPoolableElementView().getAliquotId());
      if (v == null) {
        throw new IllegalStateException("Pool contains an unsaved library aliquot");
      }
      targetAliquots.add(new PoolElement(target, v, sourcePd.getProportion()));
    }
    for (PoolElement targetPd : targetAliquots) {
      PoolElement sourcePd = source.stream()
          .filter(spd -> spd.getPoolableElementView().getAliquotId() == targetPd.getPoolableElementView().getAliquotId())
          .findFirst().orElse(null);
      if (sourcePd != null) {
        targetPd.setProportion(sourcePd.getProportion());
      }
    }
  }

  private Predicate<PoolElement> notInOther(Collection<PoolElement> otherCollection) {
    return t -> otherCollection.stream()
        .noneMatch(other -> other.getPoolableElementView().getAliquotId() == t.getPoolableElementView().getAliquotId());
  }

  private void loadPoolElements(Pool source, Pool target) throws IOException {
    loadPoolElements(source.getPoolContents(), target);
  }

  private Set<String> extractAliquotNames(Set<PoolElement> elements) {
    Set<String> original = new HashSet<>();
    for (PoolElement element : elements) {
      original.add(element.getPoolableElementView().getAliquotName());
    }
    return original;
  }

  @Override
  public void addNote(Pool pool, Note note) throws IOException {
    Pool managed = poolStore.get(pool.getId());
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    poolStore.save(managed);
  }

  @Override
  public Pool get(long poolId) throws IOException {
    return poolStore.get(poolId);
  }

  @Override
  public Pool getByBarcode(String barcode) throws IOException {
    return poolStore.getByBarcode(barcode);
  }

  @Override
  public List<Pool> listByIdList(List<Long> poolIds) throws IOException {
    return poolStore.listPoolsById(poolIds);
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
    Set<SequencingOrder> orders = sequencingOrderService.getByPool(object);
    sequencingOrderService.bulkDelete(orders);

    Box box = object.getBox();
    if (box != null) {
      box.getBoxPositions().remove(object.getBoxPosition());
      boxService.save(box);
    }
    fileAttachmentService.beforeDelete(object);
  }

  @Override
  public void afterDelete(Pool object) throws IOException {
    fileAttachmentService.afterDelete(object);
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

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return poolStore.count(errorHandler, filter);
  }

  @Override
  public List<Pool> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol, PaginationFilter... filter)
      throws IOException {
    return poolStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
