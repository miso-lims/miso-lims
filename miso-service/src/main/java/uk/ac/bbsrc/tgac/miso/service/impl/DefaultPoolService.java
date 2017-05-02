package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.PoolChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.exception.AuthorizationIOException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.PoolableElementViewService;
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
  private PoolQcStore poolQcStore;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private PoolAlertManager poolAlertManager;
  @Autowired
  private LibraryDilutionStore libraryDilutionStore;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private PoolableElementViewService poolableElementViewService;

  public void setAutoGenerateIdBarcodes(boolean autoGenerateIdBarcodes) {
    this.autoGenerateIdBarcodes = autoGenerateIdBarcodes;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setPoolStore(PoolStore poolStore) {
    this.poolStore = poolStore;
  }

  public void setPoolQcStore(PoolQcStore poolQcStore) {
    this.poolQcStore = poolQcStore;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setPoolAlertManager(PoolAlertManager poolAlertManager) {
    this.poolAlertManager = poolAlertManager;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
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
  public Collection<Pool> listAllPoolsBySearch(String query) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(null, query, null, false);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listAllPoolsWithLimit(int limit) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(null, null, limit, false);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listAllPools() throws IOException {
    Collection<Pool> pools = poolStore.listAll();
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listAllPoolsByPlatform(PlatformType platformType) throws IOException {
    Collection<Pool> pools = poolStore.listAllByCriteria(platformType, null, null, false);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(platformType, query, null, false);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listReadyPoolsByPlatform(PlatformType platformType) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(platformType, null, null, true);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    List<Pool> pools = poolStore.listAllByCriteria(platformType, query, null, true);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listPoolsByProjectId(long projectId) throws IOException {
    Collection<Pool> pools = poolStore.listByProjectId(projectId);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public Collection<Pool> listPoolsByLibraryId(long libraryId) throws IOException {
    Collection<Pool> pools = poolStore.listByLibraryId(libraryId);
    return authorizationManager.filterUnreadable(pools);
  }

  @Override
  public void deletePool(Pool pool) throws IOException {
    authorizationManager.throwIfNonAdmin();
    if (!poolStore.remove(pool)) {
      throw new IOException("Unable to delete Pool.");
    }
  }

  @Override
  public void deletePoolNote(Pool pool, Long noteId) throws IOException {
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
  public long savePool(Pool pool) throws IOException {
    if (pool.isDiscarded()) {
      pool.setVolume(0.0);
    }
    pool.setLastModifier(authorizationManager.getCurrentUser());

    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      pool.setName(generateTemporaryName());
      loadPooledElements(pool.getPoolableElementViews(), pool);
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
    } else {
      Pool original = poolStore.get(pool.getId());
      authorizationManager.throwIfNotWritable(original);
      original.setAlias(pool.getAlias());
      original.setConcentration(pool.getConcentration());
      original.setDescription(pool.getDescription());
      original.setIdentificationBarcode(pool.getIdentificationBarcode());
      original.setPlatformType(pool.getPlatformType());
      original.setQcPassed(pool.getQcPassed());
      original.setReadyToRun(pool.getReadyToRun());

      Set<String> originalItems = extractDilutionNames(original.getPoolableElementViews());
      loadPooledElements(pool, original);
      Set<String> updatedItems = extractDilutionNames(original.getPoolableElementViews());

      Set<String> added = new TreeSet<>(updatedItems);
      added.removeAll(originalItems);
      Set<String> removed = new TreeSet<>(originalItems);
      removed.removeAll(updatedItems);

      if (!added.isEmpty() || !removed.isEmpty()) {
        StringBuilder message = new StringBuilder();
        message.append("Items");
        LimsUtils.appendSet(message, added, "added");
        LimsUtils.appendSet(message, removed, "removed");

        PoolChangeLog changeLog = new PoolChangeLog();
        changeLog.setPool(pool);
        changeLog.setColumnsChanged("contents");
        changeLog.setSummary(message.toString());
        changeLog.setTime(new Date());
        changeLog.setUser(pool.getLastModifier());
        changeLogService.create(changeLog);
      }
      pool = original;
    }
    long id = poolStore.save(pool);
    if (poolAlertManager != null) poolAlertManager.update(pool);
    return id;
  }

  private void loadPooledElements(Collection<PoolableElementView> source, Pool target) throws IOException {
    Set<PoolableElementView> pooledElements = new HashSet<>();
    for (PoolableElementView dilution : source) {
      PoolableElementView v = null;
      if (dilution.getDilutionId() != LibraryDilution.UNSAVED_ID) {
        v = poolableElementViewService.get(dilution.getDilutionId());
      } else if (dilution.getPreMigrationId() != null) {
        v = poolableElementViewService.getByPreMigrationId(dilution.getPreMigrationId());
      }
      if (v == null) throw new IllegalStateException("Pool contains an unsaved dilution");
      pooledElements.add(v);
    }
    target.setPoolableElementViews(pooledElements);
  }

  private void loadPooledElements(Pool source, Pool target) throws IOException {
    loadPooledElements(source.getPoolableElementViews(), target);
  }

  private Set<String> extractDilutionNames(Set<PoolableElementView> dilutions) {
    Set<String> original = new HashSet<>();
    for (PoolableElementView dilution : dilutions) {
      original.add(dilution.getDilutionName());
    }
    return original;
  }

  @Override
  public long savePoolQC(PoolQC poolQC) throws IOException {
    if (poolQC.getId() != PoolImpl.UNSAVED_ID) {
      PoolQC original = getPoolQCById(poolQC.getId());
      authorizationManager.throwIfNotWritable(original.getPool());
      original.setResults(poolQC.getResults());
      poolQC = original;
    }
    return poolQcStore.save(poolQC);
  }

  @Override
  public void savePoolNote(Pool pool, Note note) throws IOException {
    Pool managed = poolStore.get(pool.getId());
    authorizationManager.throwIfNotWritable(managed);
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    poolStore.save(managed);
  }

  @Override
  public Pool getPoolById(long poolId) throws IOException {
    Pool pool = poolStore.get(poolId);
    authorizationManager.throwIfNotReadable(pool);
    return pool;
  }

  @Override
  public PoolQC getPoolQCById(long poolQcId) throws IOException {
    PoolQC qc = poolQcStore.get(poolQcId);
    authorizationManager.throwIfNotReadable(qc.getPool());
    return qc;
  }

  @Override
  public Pool getPoolByBarcode(String barcode) throws IOException {
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
    authorizationManager.throwIfNotReadable(pool);
    if (!managedPool.userCanRead(managedWatcher)) {
      throw new AuthorizationIOException("User " + watcher.getLoginName() + " cannot see this pool.");
    }
    poolStore.addWatcher(pool, watcher);
    if (poolAlertManager != null) poolAlertManager.addWatcher(pool, watcher);
  }

  @Override
  public void removePoolWatcher(Pool pool, User watcher) throws IOException {
    User managedWatcher = securityManager.getUserById(watcher.getUserId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(managedWatcher);
    poolStore.removeWatcher(pool, managedWatcher);
    if (poolAlertManager != null) poolAlertManager.removeWatcher(pool, watcher);
  }

}
