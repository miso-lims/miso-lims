package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractQC;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.AuthorizationIOException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.RunQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizedPaginatedDataSource;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultRunService implements RunService, AuthorizedPaginatedDataSource<Run> {

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private RunStore runDao;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private RunQcStore runQcDao;
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private SecurityProfileStore securityProfileStore;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private SequencerReferenceService sequencerReferenceService;
  @Autowired
  private SequencingParametersService sequencingParametersService;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public PaginatedDataSource<Run> getBackingPaginationSource() {
    return runDao;
  }

  @Override
  public Collection<Run> list() throws IOException {
    Collection<Run> allRuns = runDao.listAll();
    return authorizationManager.filterUnreadable(allRuns);
  }

  @Override
  public Collection<Run> listWithLimit(long limit) throws IOException {
    Collection<Run> runs = runDao.listAllWithLimit(limit);
    return authorizationManager.filterUnreadable(runs);
  }

  @Override
  public Collection<Run> listBySearch(String query) throws IOException {
    Collection<Run> runs = runDao.listBySearch(query);
    return authorizationManager.filterUnreadable(runs);
  }

  @Override
  public Collection<Run> listByProjectId(long projectId) throws IOException {
    Collection<Run> runs = runDao.listByProjectId(projectId);
    return authorizationManager.filterUnreadable(runs);
  }

  @Override
  public Collection<Run> listByPoolId(long poolId) throws IOException {
    Collection<Run> runs = runDao.listByPoolId(poolId);
    return authorizationManager.filterUnreadable(runs);
  }

  @Override
  public Collection<Run> listByContainerId(long containerId) throws IOException {
    Collection<Run> runs = runDao.listBySequencerPartitionContainerId(containerId);
    return authorizationManager.filterUnreadable(runs);
  }

  @Override
  public Collection<Run> listBySequencerId(long sequencerId) throws IOException {
    Collection<Run> runs = runDao.listBySequencerId(sequencerId);
    return authorizationManager.filterUnreadable(runs);
  }

  @Override
  public Run get(long runId) throws IOException, AuthorizationException {
    Run run = runDao.get(runId);
    authorizationManager.throwIfNotReadable(run);
    return run;
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException, AuthorizationException {
    Run run = runDao.getByAlias(alias);
    authorizationManager.throwIfNotReadable(run);
    return run;
  }

  @Override
  public Run getLatestRunBySequencerPartitionContainerId(long containerId) throws IOException, AuthorizationException {
    Run run = runDao.getLatestRunIdRunBySequencerPartitionContainerId(containerId);
    authorizationManager.throwIfNotReadable(run);
    return run;
  }

  @Override
  public void addRunWatcher(Run run, User watcher) throws IOException {
    User managedWatcher = securityManager.getUserById(watcher.getUserId());
    Run managedRun = runDao.get(run.getId());
    authorizationManager.throwIfNotReadable(managedRun);
    if (!managedRun.userCanRead(managedWatcher)) {
      throw new AuthorizationIOException("User " + watcher.getLoginName() + " cannot see this run.");
    }
    runDao.addWatcher(run, watcher);
  }

  @Override
  public void removeRunWatcher(Run run, User watcher) throws IOException {
    User managedWatcher = securityManager.getUserById(watcher.getUserId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(managedWatcher);
    runDao.removeWatcher(run, managedWatcher);
  }

  @Override
  public int count() throws IOException {
    return runDao.count();
  }

  @Override
  public void addNote(Run run, Note note) throws IOException {
    Run managed = runDao.get(run.getId());
    authorizationManager.throwIfNotWritable(managed);
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    managed.setLastModifier(authorizationManager.getCurrentUser());
    runDao.save(managed);
  }

  @Override
  public void deleteNote(Run run, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Run managed = runDao.get(run.getId());
    authorizationManager.throwIfNotWritable(managed);
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Run  " + run.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    runDao.save(managed);
  }

  @Override
  public void addQc(Run run, RunQC qc) throws IOException {
    if (qc.getQcType() == null || qc.getQcType().getQcTypeId() == null) {
      throw new IllegalArgumentException("QC Type cannot be null");
    }
    QcType managedQcType = runQcDao.getRunQcTypeById(qc.getQcType().getQcTypeId());
    if (managedQcType == null) {
      throw new IllegalArgumentException("QC Type " + qc.getQcType().getQcTypeId() + " is not applicable for runs");
    }
    qc.setQcType(managedQcType);
    qc.setQcCreator(authorizationManager.getCurrentUsername());

    Run managed = get(run.getId());
    authorizationManager.throwIfNotWritable(managed);

    managed.addQc(qc);
    managed.setLastModifier(authorizationManager.getCurrentUser());
    runDao.save(managed);
  }

  @Override
  public void bulkAddQcs(Run run) throws IOException {
    for (RunQC qc : run.getRunQCs()) {
      if (qc.getId() == AbstractQC.UNSAVED_ID) addQc(run, qc);
      // TODO: make QCs updatable too
    }
  }

  @Override
  public void deleteQc(Run run, Long qcId) throws IOException {
    if (qcId == null || qcId.equals(SampleQCImpl.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Run QC");
    }
    Run managed = runDao.get(run.getId());
    authorizationManager.throwIfNotWritable(managed);
    RunQC deleteQc = null;
    for (RunQC qc : managed.getRunQCs()) {
      if (qc.getId() == qcId) {
        deleteQc = qc;
        break;
      }
    }
    if (deleteQc == null) throw new IOException("QC " + qcId + " not found for Run " + run.getId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(securityManager.getUserByLoginName(deleteQc.getQcCreator()));
    managed.getRunQCs().remove(deleteQc);
    managed.setLastModifier(authorizationManager.getCurrentUser());
    runQcDao.remove(deleteQc);
    runDao.save(managed);
  }

  @Override
  public QcType getRunQcType(long qcTypeId) throws IOException {
    return runQcDao.getRunQcTypeById(qcTypeId);
  }

  @Override
  public QcType getRunQcTypeByName(String qcTypeName) throws IOException {
    return runQcDao.getRunQcTypeByName(qcTypeName);
  }

  @Override
  public Long create(Run run) throws IOException {
    authorizationManager.throwIfNotWritable(run);
    saveContainers(run);
    setChangeDetails(run);
    loadChildEntities(run);

    run.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(run.getSecurityProfile())));
    run.setName(generateTemporaryName());

    return save(run).getId();
  }

  @Override
  public void update(Run run) throws IOException {
    Run updatedRun = get(run.getId());
    authorizationManager.throwIfNotWritable(updatedRun);
    saveContainers(run);
    if (!run.getRunQCs().isEmpty()) bulkAddQcs(run);
    applyChanges(updatedRun, run);
    setChangeDetails(updatedRun);
    loadChildEntities(updatedRun);
    save(updatedRun);
  }

  private Run save(Run run) throws IOException {
    try {
      Long id = runDao.save(run);
      Run saved = runDao.get(id);
      
      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(run)) {
        saved.setName(namingScheme.generateNameFor(saved));
        validateNameOrThrow(saved, namingScheme);
        needsUpdate = true;
      }
      if (needsUpdate) {
        runDao.save(saved);
        saved = runDao.get(saved.getId());
      }
      return saved;
    } catch (MisoNamingException e) {
      throw new IllegalArgumentException("Name generator failed to generate a valid name", e);
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e), e.getSQLException(),
          e.getConstraintName());
    }
  }

  private void saveContainers(Run run) throws IOException {
    List<SequencerPartitionContainer> containers = run.getSequencerPartitionContainers();
    if (containers != null && !containers.isEmpty()) {
      List<SequencerPartitionContainer> savedContainers = Lists.newArrayList();
      for (SequencerPartitionContainer container : containers) {
        savedContainers.add(containerService.save(container));
      }
      run.setSequencerPartitionContainers(savedContainers);
    }
  }

  @Override
  public void saveRuns(Collection<Run> runs) throws IOException {
    for (Run run : runs) {
      if (run.getId() == Run.UNSAVED_ID) {
        create(run);
      } else {
        update(run);
      }
    }
  }

  @Override
  public void delete(Long runId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Run run = get(runId);
    runDao.remove(run);
  }

  /**
   * Loads persisted objects into run fields. Should be called before saving or updating. Loads all fields except for:
   * <ul>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param run the Run to load entities into. Must contain at least the IDs of the objects to load.
   * @throws IOException
   */
  private void loadChildEntities(Run run) throws IOException {
    run.setSequencingParameters(sequencingParametersService.get(run.getSequencingParameters().getId()));
    run.setSequencerReference(sequencerReferenceService.get(run.getSequencerReference().getId()));
  }

  private void applyChanges(Run target, Run source) throws IOException {
    target.setAlias(source.getAlias());
    target.setAccession(source.getAccession());
    target.setDescription(source.getDescription());
    target.setFilePath(source.getFilePath());
    target.setHealth(source.getHealth());
    target.setStartDate(source.getStartDate());
    target.setCompletionDate(source.getCompletionDate());
    target.setMetrics(source.getMetrics());

    makeContainerChangesChangeLog(target, source.getSequencerPartitionContainers());
    target.setSequencerPartitionContainers(source.getSequencerPartitionContainers());

    target.setSequencingParameters(source.getSequencingParameters());
    target.setSequencerReference(source.getSequencerReference());
    if (isIlluminaRun(target)) {
      applyIlluminaChanges((IlluminaRun) target, (IlluminaRun) source);
    } else if (isPacBioRun(target)) {
      applyPacBioChanges((PacBioRun) target, (PacBioRun) source);
    } else if (isLS454Run(target)) {
      applyLS454Changes((LS454Run) target, (LS454Run) source);
    } else if (isSolidRun(target)) {
      applySolidChanges((SolidRun) target, (SolidRun) source);
    }
  }

  private void applyIlluminaChanges(IlluminaRun target, IlluminaRun source) throws IOException {
    target.setCallCycle(source.getCallCycle());
    target.setImgCycle(source.getImgCycle());
    target.setNumCycles(source.getNumCycles());
    target.setScoreCycle(source.getScoreCycle());
    target.setPairedEnd(source.getPairedEnd());
  }

  private void applyPacBioChanges(PacBioRun target, PacBioRun source) throws IOException {
    target.setMovieDuration(source.getMovieDuration());
  }

  private void applyLS454Changes(LS454Run target, LS454Run source) throws IOException {
    target.setCycles(source.getCycles());
    target.setPairedEnd(source.getPairedEnd());
  }

  private void applySolidChanges(SolidRun target, SolidRun source) throws IOException {
    target.setPairedEnd(source.getPairedEnd());
  }

  /**
   * Updates all timestamps and user data associated with the change
   * 
   * @param run the Run to update
   * @throws IOException
   */
  private void setChangeDetails(Run run) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Date now = new Date();
    run.setLastModifier(user);

    if (run.getId() == Run.UNSAVED_ID) {
      run.setCreator(user);
      if (run.getCreationTime() == null) {
        run.setCreationTime(now);
        run.setLastModified(now);
      } else if (run.getLastModified() == null) {
        run.setLastModified(now);
      }
    } else {
      run.setLastModified(now);
    }
  }

  private void makeContainerChangesChangeLog(Run managedRun, List<SequencerPartitionContainer> updatedContainers) throws IOException {
    Set<String> originalContainersString = Barcodable.extractLabels(managedRun.getSequencerPartitionContainers());
    Set<String> updatedContainersString = Barcodable.extractLabels(updatedContainers);
    Set<String> added = new TreeSet<>(updatedContainersString);
    added.removeAll(originalContainersString);
    Set<String> removed = new TreeSet<>(originalContainersString);
    removed.removeAll(updatedContainersString);
    if (!added.isEmpty() || !removed.isEmpty()) {
      StringBuilder message = new StringBuilder();
      message.append("Containers");
      LimsUtils.appendSet(message, added, "added");
      LimsUtils.appendSet(message, removed, "removed");

      RunChangeLog changeLog = new RunChangeLog();
      changeLog.setRun(managedRun);
      changeLog.setColumnsChanged("containers");
      changeLog.setSummary(message.toString());
      changeLog.setTime(new Date());
      changeLog.setUser(managedRun.getLastModifier());
      changeLogService.create(changeLog);
    }
  }

  @Override
  public Collection<QcType> listRunQcTypes() throws IOException {
    return runQcDao.listAllRunQcTypes();
  }

  @Override
  public Map<String, Integer> getRunColumnSizes() throws IOException {
    return runDao.getRunColumnSizes();
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRunQcDao(RunQcStore runQcDao) {
    this.runQcDao = runQcDao;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setSecurityProfileStore(SecurityProfileStore securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  public void setSequencingParametersService(SequencingParametersService sequencingParametersService) {
    this.sequencingParametersService = sequencingParametersService;
  }

  public void setSequencerReferenceService(SequencerReferenceService sequencerReferenceService) {
    this.sequencerReferenceService = sequencerReferenceService;
  }

  public void setChangeLogService(ChangeLogService changeLogService) {
    this.changeLogService = changeLogService;
  }

  public void setContainerService(ContainerService containerService) {
    this.containerService = containerService;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setRunDao(RunStore runDao) {
    this.runDao = runDao;
  }

}
