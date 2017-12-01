package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.GetLaneContents;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.OxfordNanoporeRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.exception.AuthorizationIOException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityProfileStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.SequencerReferenceService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizedPaginatedDataSource;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultRunService implements RunService, AuthorizedPaginatedDataSource<Run> {
  
  private static class NotIn implements Predicate<SequencerPartitionContainer> {

    private final Collection<SequencerPartitionContainer> containers;

    public NotIn(Collection<SequencerPartitionContainer> containers) {
      this.containers = containers;
    }

    @Override
    public boolean test(SequencerPartitionContainer t) {
      for (SequencerPartitionContainer c : containers) {
        if (c.getLabelText().equals(t.getLabelText())) {
          return false;
        }
      }
      return true;
    }
  }
  
  private static final Logger log = LoggerFactory.getLogger(DefaultRunService.class);

  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private RunStore runDao;
  @Autowired
  private ChangeLogService changeLogService;
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
  @Autowired
  private PoolService poolService;

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
    runDao.addWatcher(managedRun, watcher);
  }

  @Override
  public void removeRunWatcher(Run run, User watcher) throws IOException {
    User managedWatcher = securityManager.getUserById(watcher.getUserId());
    Run managedRun = runDao.get(run.getId());
    authorizationManager.throwIfNonAdminOrMatchingOwner(managedWatcher);
    runDao.removeWatcher(managedRun, managedWatcher);
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
  public Long create(Run run) throws IOException {
    authorizationManager.throwIfNotWritable(run);
    saveContainers(run);
    setChangeDetails(run);
    loadChildEntities(run);

    run.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(run.getSecurityProfile())));
    run.setName(generateTemporaryName());

    Run saved = save(run);
    makeContainerChangesChangeLog(saved, Collections.emptyList(), saved.getSequencerPartitionContainers());
    return saved.getId();
  }

  @Override
  public void update(Run run) throws IOException {
    Run updatedRun = get(run.getId());
    authorizationManager.throwIfNotWritable(updatedRun);
    saveContainers(run);
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
    if (run.getSequencingParameters() != null) {
      run.setSequencingParameters(sequencingParametersService.get(run.getSequencingParameters().getId()));
    }
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

    makeContainerChangesChangeLog(target, target.getSequencerPartitionContainers(), source.getSequencerPartitionContainers());
    target.setSequencerPartitionContainers(source.getSequencerPartitionContainers());

    target.setSequencingParameters(source.getSequencingParameters());
    target.setSequencerReference(source.getSequencerReference());
    if (isIlluminaRun(target)) {
      applyIlluminaChanges((IlluminaRun) target, (IlluminaRun) source);
    } else if (isLS454Run(target)) {
      applyLS454Changes((LS454Run) target, (LS454Run) source);
    } else if (isSolidRun(target)) {
      applySolidChanges((SolidRun) target, (SolidRun) source);
    } else if (isOxfordNanoporeRun(target)) {
      applyOxfordNanoporeChanges((OxfordNanoporeRun) target, (OxfordNanoporeRun) source);
    }
  }

  private void applyIlluminaChanges(IlluminaRun target, IlluminaRun source) {
    target.setCallCycle(source.getCallCycle());
    target.setImgCycle(source.getImgCycle());
    target.setNumCycles(source.getNumCycles());
    target.setScoreCycle(source.getScoreCycle());
    target.setPairedEnd(source.getPairedEnd());
  }

  private void applyLS454Changes(LS454Run target, LS454Run source) {
    target.setCycles(source.getCycles());
    target.setPairedEnd(source.getPairedEnd());
  }

  private void applySolidChanges(SolidRun target, SolidRun source) {
    target.setPairedEnd(source.getPairedEnd());
  }

  private void applyOxfordNanoporeChanges(OxfordNanoporeRun target, OxfordNanoporeRun source) {
    target.setMinKnowVersion(isStringEmptyOrNull(source.getMinKnowVersion()) ? null : source.getMinKnowVersion());
    target.setProtocolVersion(isStringEmptyOrNull(source.getProtocolVersion()) ? null : source.getProtocolVersion());
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

  /**
   * If any containers were added or removed from the run, generates and saves a single Run changelog entry and one Container changelog
   * entry for each Container affected. May be called before or after managedRun is updated, as the original and updated container list
   * are both provided separately
   * 
   * @param managedRun Run to add changelog entry to
   * @param originalContainers Containers attached to the Run before change
   * @param updatedContainers Containers attached to the Run after change
   * @throws IOException
   */
  private void makeContainerChangesChangeLog(Run managedRun, List<SequencerPartitionContainer> originalContainers,
      List<SequencerPartitionContainer> updatedContainers) throws IOException {
    List<SequencerPartitionContainer> added = updatedContainers.stream()
        .filter(new NotIn(originalContainers))
        .collect(Collectors.toList());
    List<SequencerPartitionContainer> removed = originalContainers.stream()
        .filter(new NotIn(updatedContainers))
        .collect(Collectors.toList());
    if (!added.isEmpty() || !removed.isEmpty()) {
      Set<String> addedLabels = Barcodable.extractLabels(added);
      Set<String> removedLabels = Barcodable.extractLabels(removed);
      StringBuilder message = new StringBuilder();
      message.append("Containers");
      LimsUtils.appendSet(message, addedLabels, "added");
      LimsUtils.appendSet(message, removedLabels, "removed");

      RunChangeLog changeLog = new RunChangeLog();
      changeLog.setRun(managedRun);
      changeLog.setColumnsChanged("containers");
      changeLog.setSummary(message.toString());
      changeLog.setTime(new Date());
      changeLog.setUser(authorizationManager.getCurrentUser());
      changeLogService.create(changeLog);
      for (SequencerPartitionContainer add : added) {
        String addMessage = "Added to run " + managedRun.getAlias();
        saveContainerRunChangeLog(add, addMessage);
      }
      for (SequencerPartitionContainer remove : removed) {
        String removeMessage = "Removed from run " + managedRun.getAlias();
        saveContainerRunChangeLog(remove, removeMessage);
      }
    }
  }

  private void saveContainerRunChangeLog(SequencerPartitionContainer container, String message) throws IOException {
    SequencerPartitionContainerChangeLog changeLog = new SequencerPartitionContainerChangeLog();
    changeLog.setSequencerPartitionContainer(container);
    changeLog.setColumnsChanged("run");
    changeLog.setSummary(message);
    changeLog.setTime(new Date());
    changeLog.setUser(authorizationManager.getCurrentUser());
    changeLogService.create(changeLog);
  }

  @Override
  public Map<String, Integer> getRunColumnSizes() throws IOException {
    return runDao.getRunColumnSizes();
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
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

  @Override
  public boolean processNotification(Run source, int laneCount, String containerSerialNumber, String sequencerName,
      Predicate<SequencingParameters> filterParameters, GetLaneContents getLaneContents)
      throws IOException, MisoNamingException {
    final Date now = new Date();
    User user = securityManager.getUserByLoginName("notification");
    final Run target;

    Run runFromDb = runDao.getByAlias(source.getAlias());
    boolean isNew;

    if (runFromDb == null) {
      target = source.getPlatformType().createRun(user);
      target.setCreationTime(now);
      target.setCreator(user);
      target.setName(generateTemporaryName());
      target.setAlias(source.getAlias());
      isNew = true;
    } else {
      target = runFromDb;
      if (source.getPlatformType() != target.getPlatformType()) {
        throw new IllegalStateException(
            "Run scanner detected a run from " + source.getPlatformType().name() + " and there is a save run from "
                + target.getPlatformType().name());
      }
      isNew = false;
    }

    target.setLastModifier(user);
    boolean isMutated = false;
    isMutated |= updateMetricsFromNotification(source, target);
    isMutated |= updateField(source.getFilePath(), target.getFilePath(), target::setFilePath);
    isMutated |= updateField(source.getStartDate(), target.getStartDate(), target::setStartDate);

    final SequencerReference sequencer = sequencerReferenceService.getByName(sequencerName);
    if (sequencer == null) {
      throw new IllegalArgumentException("No such sequencer: " + sequencerName);
    }
    target.setSequencerReference(sequencer);

    if (!sequencer.getPlatform().getPartitionSizes().contains(laneCount)) {
      throw new IllegalArgumentException("Invalid number of partitions: " + laneCount);
    }

    isMutated |= updateContainerFromNotification(target, user, laneCount, containerSerialNumber, sequencer, getLaneContents);
    isMutated |= updateHealthFromNotification(source, target, user);

    switch (source.getPlatformType()) {
    case ILLUMINA:
      isMutated |= updateIlluminaRunFromNotification((IlluminaRun) source, (IlluminaRun) target);
      break;
    case IONTORRENT:
      // Nothing to do
      break;
    case LS454:
      isMutated |= updateField(((LS454Run) source).getCycles(), ((LS454Run) target).getCycles(),
          v -> ((LS454Run) target).setCycles(v));
      isMutated |= updateField(source.getPairedEnd(), target.getPairedEnd(), target::setPairedEnd);
      break;
    case OXFORDNANOPORE:
      throw new NotImplementedException();
    case PACBIO:
      // Nothing to do
      break;
    case SOLID:
      // Nothing to do
      break;
    default:
      throw new NotImplementedException();
    }

    isMutated |= updateSequencingParameters(target, user, filterParameters, sequencer);

    setChangeDetails(target);
    if (isNew) {
      target.setSecurityProfile(securityProfileStore.get(securityProfileStore.save(target.getSecurityProfile())));
      target.setName(generateTemporaryName());

      save(target);
    } else if (isMutated) {
      save(target);
    }
    return isNew;
  }

  private boolean updateMetricsFromNotification(Run source, Run target) {
    if (source.getMetrics() != null && source.getMetrics().equals(target.getMetrics())) return false;
    if (source.getMetrics() == null) {
      return false;
    }
    if (source.getMetrics() != null && target.getMetrics() == null) {
      target.setMetrics(source.getMetrics());
      return true;
    }

    ObjectMapper mapper = new ObjectMapper();
    ArrayNode sourceMetrics;
    try {
      sourceMetrics = mapper.readValue(source.getMetrics(), ArrayNode.class);
    } catch (IOException e) {
      log.error("Impossible junk metrics were passed in for run " + target.getId(), e);
      return false;
    }
    ArrayNode targetMetrics;
    try {
      targetMetrics = mapper.readValue(target.getMetrics(), ArrayNode.class);
    } catch (IOException e) {
      log.error("The database is full of garbage metrics for run " + target.getId(), e);
      return false;
    }
    Map<String, JsonNode> sourceMetricsMap = parseMetrics(sourceMetrics);
    Map<String, JsonNode> targetMetricsMap = parseMetrics(targetMetrics);
    if (sourceMetricsMap.equals(targetMetricsMap))
      return false;
    targetMetricsMap.putAll(sourceMetricsMap);
    ArrayNode combinedMetrics = mapper.createArrayNode();
    combinedMetrics.addAll(targetMetricsMap.values());
    try {
      target.setMetrics(mapper.writeValueAsString(combinedMetrics));
    } catch (JsonProcessingException e) {
      log.error("Failed to save data just unserialised.", e);
      return false;
    }
    return true;
  }

  private Map<String, JsonNode> parseMetrics(ArrayNode metrics) {
    Map<String, JsonNode> results = new TreeMap<>();
    for (JsonNode node : metrics) {
      if (node.isObject()) {
        results.put(node.get("type").textValue(), node);
      }
    }
    return results;
  }

  private boolean updateIlluminaRunFromNotification(IlluminaRun source, final IlluminaRun target) {
    boolean isMutated = false;
    isMutated |= updateField(source.getCallCycle(), target.getCallCycle(), target::setCallCycle);
    isMutated |= updateField(source.getImgCycle(), target.getImgCycle(), target::setImgCycle);
    isMutated |= updateField(source.getNumCycles(), target.getNumCycles(), target::setNumCycles);
    isMutated |= updateField(source.getScoreCycle(), target.getScoreCycle(), target::setScoreCycle);
    isMutated |= updateField(source.getPairedEnd(), target.getPairedEnd(), target::setPairedEnd);
    return isMutated;
  }

  private boolean updateSequencingParameters(final Run target, User user, Predicate<SequencingParameters> filterParameters,
      final SequencerReference sequencer) throws IOException {
    // If the sequencing parameters haven't been updated by a human, see if we can find exactly one that matches.
    if (!target.didSomeoneElseChangeColumn("parameters", user)) {
      List<SequencingParameters> possibleParameters = sequencingParametersService.getForPlatform(sequencer.getPlatform().getId()).stream()
          .filter(parameters -> !parameters.getName().startsWith("Custom")).filter(filterParameters).collect(Collectors.toList());
      if (possibleParameters.size() == 1) {
        if (target.getSequencingParameters() == null
            || possibleParameters.get(0).getId() != target.getSequencingParameters().getId()) {
          target.setSequencingParameters(possibleParameters.get(0));
          return true;
        }
      }
    }
    return false;
  }

  private boolean updateContainerFromNotification(final Run target, User user, int laneCount, String containerSerialNumber,
      final SequencerReference sequencer, final GetLaneContents getLaneContents) throws IOException {
    final Collection<SequencerPartitionContainer> containers = containerService.listByBarcode(containerSerialNumber);
    switch (containers.size()) {
    case 0:
      SequencerPartitionContainer newContainer = new SequencerPartitionContainerImpl(user);
      newContainer.setPlatform(sequencer.getPlatform());
      newContainer.setCreator(user);
      newContainer.setIdentificationBarcode(containerSerialNumber);
      newContainer.setPartitionLimit(laneCount);
      newContainer
          .setPartitions(
              IntStream.range(0, laneCount).mapToObj((i) -> new PartitionImpl(newContainer, i + 1)).collect(Collectors.toList()));
      updatePartitionContents(getLaneContents, newContainer);
      target.setSequencerPartitionContainers(Collections.singletonList(containerService.create(newContainer)));
      return true;
    case 1:
      SequencerPartitionContainer container = containers.iterator().next();
      if (container.getPartitions().size() != laneCount) {
        throw new IllegalArgumentException(String.format("The container %s has %d partitions, but %d were detected by the scanner.",
            containerSerialNumber, container.getPartitions().size(), laneCount));
      }
      if (target.getSequencerPartitionContainers().stream().noneMatch(c -> c.getId() == container.getId())) {
        target.getSequencerPartitionContainers().add(container);
        updatePartitionContents(getLaneContents, container);
        return true;
      }
      break;
    default:
      throw new IllegalArgumentException("Multiple containers with same identifier: " + containerSerialNumber);
    }
    return false;
  }

  private void updatePartitionContents(final GetLaneContents getLaneContents, SequencerPartitionContainer newContainer) {
    newContainer.getPartitions().stream().filter(partition -> partition.getPool() == null)
        .forEach(partition -> getLaneContents.getLaneContents(partition.getPartitionNumber()).filter(s -> !LimsUtils.isStringBlankOrNull(s))
            .map(WhineyFunction.rethrow(poolService::getByBarcode)).ifPresent(partition::setPool));
  }

  private boolean updateHealthFromNotification(Run notification, final Run managed, User user) {
    if (notification.getHealth() == null) {
      // If the server has sent us nothing, ignore it.
      return false;
    } else if (notification.getHealth() == HealthType.Unknown) {
      // If it is sending us (effectively) an error, don't update the health if we have something already.
      if (managed.getHealth() == null) {
        managed.setHealth(notification.getHealth());
        managed.setCompletionDate(notification.getHealth().isDone() ? notification.getCompletionDate() : null);
        return true;
      }
    } else {
      if (!managed.didSomeoneElseChangeColumn("health", user) || (!managed.getHealth().isDone() && notification.getHealth().isDone())) {
        // A human user has never change the health of this run, so we will.
        // Alternatively, a human set the status to not-done but runscanner has indicated that the run is now done, so we will update with
        // this.
        managed.setHealth(notification.getHealth());
        managed.setCompletionDate(notification.getHealth().isDone() ? notification.getCompletionDate() : null);
        return true;
      }
    }
    return false;
  }

  private <T> boolean updateField(T dtoValue, T modelValue, Consumer<T> writer) {
    if (dtoValue == null) {
      return false;
    }
    if (dtoValue.equals(modelValue)) {
      return false;
    }
    writer.accept(dtoValue);
    return true;
  }

}
