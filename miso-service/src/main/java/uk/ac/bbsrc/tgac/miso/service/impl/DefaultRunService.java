package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentDataManglingPolicy;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.OxfordNanoporeRun;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.RunChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.SequencerPartitionContainerChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.ContainerService;
import uk.ac.bbsrc.tgac.miso.core.service.FileAttachmentService;
import uk.ac.bbsrc.tgac.miso.core.service.InstrumentService;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPartitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.SopService;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.generateTemporaryName;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.hasTemporaryName;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isIlluminaRun;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isLS454Run;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isOxfordNanoporeRun;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isSolidRun;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.HibernateUtilDao;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isChanged;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isSetAndChanged;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.loadChildEntity;
import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.validateNameOrThrow;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultRunService implements RunService {

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
  private DeletionStore deletionStore;
  @Autowired
  private RunStore runDao;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private UserService userService;
  @Autowired
  private NamingSchemeHolder namingSchemeHolder;
  @Autowired
  private ContainerService containerService;
  @Autowired
  private InstrumentService instrumentService;
  @Autowired
  private SequencingParametersService sequencingParametersService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private KitDescriptorService kitDescriptorService;
  @Autowired
  private RunPartitionService runPartitionService;
  @Autowired
  private RunPartitionAliquotService runPartitionAliquotService;
  @Autowired
  private FileAttachmentService fileAttachmentService;
  @Autowired
  private SopService sopService;
  @Autowired
  private HibernateUtilDao hibernateUtilDao;

  @Override
  public Collection<Run> listByProjectId(long projectId) throws IOException {
    return runDao.listByProjectId(projectId);
  }

  @Override
  public Collection<Run> listByPoolId(long poolId) throws IOException {
    return runDao.listByPoolId(poolId);
  }

  @Override
  public List<Run> listByLibraryAliquotId(long libraryAliquotId) throws IOException {
    return runDao.listByLibraryAliquotId(libraryAliquotId);
  }

  @Override
  public List<Run> listByLibraryIdList(Collection<Long> libraryIds) throws IOException {
    return runDao.listByLibraryIdList(libraryIds);
  }

  @Override
  public Collection<Run> listByContainerId(long containerId) throws IOException {
    return runDao.listBySequencerPartitionContainerId(containerId);
  }

  @Override
  public List<Run> listByIdList(Collection<Long> ids) throws IOException {
    return runDao.listByIdList(ids);
  }

  @Override
  public Run get(long runId) throws IOException, AuthorizationException {
    return runDao.get(runId);
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException, AuthorizationException {
    return runDao.getByAlias(alias);
  }

  @Override
  public Run getLatestRunBySequencerPartitionContainerId(long containerId) throws IOException, AuthorizationException {
    return runDao.getLatestRunIdRunBySequencerPartitionContainerId(containerId);
  }

  @Override
  public void addNote(Run run, Note note) throws IOException {
    Run managed = runDao.get(run.getId());
    note.setCreationDate(LocalDate.now(ZoneId.systemDefault()));
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    managed.setLastModifier(authorizationManager.getCurrentUser());
    runDao.update(managed);
  }

  @Override
  public void deleteNote(Run run, Long noteId) throws IOException {
    if (noteId == null) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Run managed = runDao.get(run.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getId() == noteId.longValue()) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Run  " + run.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    runDao.update(managed);
  }

  @Override
  public long create(Run run) throws IOException {
    loadChildEntities(run);
    validateChanges(null, run);
    saveContainers(run);

    run.setName(generateTemporaryName());

    Run saved = save(run);
    makeContainerChangesChangeLog(saved, Collections.emptyList(), saved.getSequencerPartitionContainers());
    return saved.getId();
  }

  @Override
  public long update(Run run) throws IOException {
    Run managed = get(run.getId());
    loadChildEntities(run);
    validateChanges(managed, run);
    saveContainers(run);
    applyChanges(managed, run);
    return save(managed).getId();
  }

  private Run save(Run run) throws IOException {
    try {
      run.setChangeDetails(authorizationManager.getCurrentUser());
      Long id = run.isSaved() ? runDao.update(run) : runDao.create(run);
      Run saved = runDao.get(id);

      // post-save field generation
      boolean needsUpdate = false;
      if (hasTemporaryName(run)) {
        NamingScheme namingScheme = namingSchemeHolder.getPrimary();
        saved.setName(namingScheme.generateNameFor(saved));
        validateNameOrThrow(saved, namingScheme);
        needsUpdate = true;
      }
      if (needsUpdate) {
        runDao.update(saved);
        saved = runDao.get(saved.getId());
      }
      createRunPartitions(run);
      return saved;
    } catch (MisoNamingException e) {
      throw new IllegalArgumentException("Name generator failed to generate a valid name", e);
    } catch (ConstraintViolationException e) {
      // Send the nested root cause message to the user, since it contains the actual error.
      throw new ConstraintViolationException(e.getMessage() + " " + ExceptionUtils.getRootCauseMessage(e),
          e.getSQLException(),
          e.getConstraintName());
    }
  }

  private void createRunPartitions(Run run) throws IOException {
    for (SequencerPartitionContainer container : run.getSequencerPartitionContainers()) {
      for (Partition partition : container.getPartitions()) {
        RunPartition runPartition = runPartitionService.get(run, partition);
        if (runPartition == null) {
          runPartition = new RunPartition();
          runPartition.setRunId(run.getId());
          runPartition.setPartitionId(partition.getId());
          runPartition.setPurpose(run.getSequencer().getDefaultRunPurpose());
          runPartitionService.save(runPartition);
        }
      }
    }
  }

  private void saveContainers(Run run) throws IOException {
    for (RunPosition runPos : run.getRunPositions()) {
      runPos.setContainer(containerService.save(runPos.getContainer()));
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

  /**
   * Loads persisted objects into run fields. Should be called before saving or updating. Loads all
   * fields except for:
   * <ul>
   * <li>creator/lastModifier User objects</li>
   * </ul>
   * 
   * @param run the Run to load entities into. Must contain at least the IDs of the objects to load.
   * @throws IOException
   */
  private void loadChildEntities(Run run) throws IOException {
    loadChildEntity(run::setSequencingParameters, run.getSequencingParameters(), sequencingParametersService,
        "sequencingParametersId");
    loadChildEntity(run::setSequencer, run.getSequencer(), instrumentService, "instrumentId");
    loadChildEntity(run::setSequencingKit, run.getSequencingKit(), kitDescriptorService, "sequencingKitId");
    loadChildEntity(run::setDataReviewer, run.getDataReviewer(), userService, "dataReviewer");
    if (run.getRunPositions() != null) {
      for (RunPosition position : run.getRunPositions()) {
        if (position.getContainer().isSaved()) {
          position.setContainer(containerService.get(position.getContainer().getId()));
        }
        if (position.getSequencingParameters() != null) {
          position.setSequencingParameters(sequencingParametersService.get(position.getSequencingParameters().getId()));
        }
      }
    }
    loadChildEntity(run::setSop, run.getSop(), sopService, "sopId");
  }

  private void validateChanges(Run before, Run changed) throws IOException {
    ValidationUtils.updateQcDetails(changed, before, Run::getQcPassed, Run::getQcUser, Run::setQcUser,
        authorizationManager, Run::getQcDate,
        Run::setQcDate);
    if (isChanged(Run::getQcPassed, changed, before)) {
      changed.setDataReview(null);
    }
    ValidationUtils.updateQcDetails(changed, before, Run::getDataReview, Run::getDataReviewer, Run::setDataReviewer,
        authorizationManager,
        Run::getDataReviewDate, Run::setDataReviewDate);

    List<ValidationError> errors = new ArrayList<>();
    if (!changed.getHealth().isDone()) {
      changed.setCompletionDate(null);
    } else if (changed.getCompletionDate() == null) {
      errors.add(new ValidationError("completionDate", "Completion date must be provided for finished run"));
    }
    if (before != null) {
      if (before.getCompletionDate() != null && changed.getCompletionDate() != null
          && !changed.getCompletionDate().equals(before.getCompletionDate()) && !authorizationManager.isAdminUser()) {
        errors.add(new ValidationError("completionDate", "Only admin may change completion date"));
      }
      if (before.getStartDate() != null && changed.getStartDate() != null
          && !changed.getStartDate().equals(before.getStartDate()) && !authorizationManager.isAdminUser()) {
        errors.add(new ValidationError("completionDate", "Only admin may change start date"));
      }
    }
    if (isSetAndChanged(Run::getAlias, changed, before) && getRunByAlias(changed.getAlias()) != null) {
      errors.add(
          new ValidationError("alias", "A different run with this alias already exists. Run alias must be unique."));
    }

    InstrumentModel platform = changed.getSequencer().getInstrumentModel();
    for (RunPosition position : changed.getRunPositions()) {
      if (position.getPosition() != null && !platform.getPositions().contains(position.getPosition())) {
        errors.add(new ValidationError(
            String.format("Platform %s does not have a position %s", platform.getAlias(), position.getPosition())));
      }
      if (position.getContainer() != null) {
        for (Partition partition : position.getContainer().getPartitions()) {
          if (partition.getPool() != null && partition.getPool().getPlatformType() != changed.getSequencer()
              .getInstrumentModel().getPlatformType()) {
            errors.add(new ValidationError("Sequencer and pool platforms do not match"));
            break;
          }
        }
      }
    }
    validateSequencingParameters(changed, platform.getPlatformType(), errors);

    if (changed.getSequencingKit() != null && changed.getSequencingKit().getKitType() != KitType.SEQUENCING) {
      errors.add(new ValidationError("sequencingKitId", "Must be a sequencing kit"));
    }
    if (changed.getSequencingKitLot() != null && changed.getSequencingKit() == null) {
      errors.add(new ValidationError("sequencingKitLot", "Sequencing kit not specified"));
    }

    if (isSetAndChanged(Run::getDataReview, changed, before) && changed.getQcPassed() == null) {
      errors.add(new ValidationError("dataReview", "Cannot set data review before QC status"));
    }
    ValidationUtils.validateQcUser(changed.getQcPassed(), changed.getQcUser(), errors);
    ValidationUtils.validateQcUser(changed.getDataReview(), changed.getDataReviewer(), errors, "data review",
        "Data reviewer");

    User user = authorizationManager.getCurrentUser();
    if (((before == null && changed.getDataReview() != null)
        || (before != null && isChanged(Run::getDataReview, changed, before)))
        && !user.isRunReviewer() && !user.isAdmin()) {
      errors.add(new ValidationError("dataReview", "You are not authorized to make this change"));
    }

    if (changed.getSequencerPartitionContainers() != null) {
      if (changed.getSequencerPartitionContainers().size() > changed.getSequencer().getInstrumentModel()
          .getNumContainers()) {
        errors.add(new ValidationError(
            String.format("Cannot have more than %d containers",
                changed.getSequencer().getInstrumentModel().getNumContainers())));
      }
      for (SequencerPartitionContainer container : changed.getSequencerPartitionContainers()) {
        if (changed.getSequencer().getInstrumentModel().getContainerModels().stream()
            .noneMatch(model -> model.getId() == container.getModel().getId())) {
          errors.add(
              new ValidationError(
                  String.format("Container '%s' is not valid for instrument '%s'", container.getIdentificationBarcode(),
                      changed.getSequencer().getInstrumentModel().getAlias())));
        }
      }
    }

    if (changed.getSop() != null) {
      long sopId = changed.getSop().getId();

      if (sopId == 0L) {
        errors.add(new ValidationError("sopId", "SOP ID is missing"));
      } else {
        Sop persistedSop = sopService.get(sopId);
        if (persistedSop == null) {
          errors.add(new ValidationError("sopId", "SOP not found"));
        } else {
          boolean valid = true;

          if (persistedSop.getCategory() == null) {
            errors.add(new ValidationError("sopId", "SOP category is missing"));
            valid = false;
          } else if (persistedSop.getCategory() != SopCategory.RUN) {
            errors.add(new ValidationError("sopId", "Only RUN SOPs may be assigned to a Run"));
            valid = false;
          }

          boolean sopChanged = isChanged(Run::getSop, changed, before);
          if (sopChanged && persistedSop.isArchived()) {
            errors.add(new ValidationError("sopId", "Cannot assign an archived SOP to a Run"));
            valid = false;
          }

          if (valid) {
            changed.setSop(persistedSop);
          }
        }
      }
    }



    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private static void validateSequencingParameters(Run run, PlatformType platformType, List<ValidationError> errors) {
    // Ensure sequencing parameters are not set at the wrong location. Parameters are required on the
    // front-end, but not the back-end, as we want to accept runs from Run Scanner even if the
    // parameters cannot be determined.
    if (platformType.hasContainerLevelParameters()) {
      if (run.getSequencingParameters() != null) {
        errors.add(new ValidationError("sequencingParametersId",
            "Sequencing parameters should not be set at the run level for %s runs"
                .formatted(platformType.getKey())));
      }
    } else {
      for (RunPosition runPosition : run.getRunPositions()) {
        if (runPosition.getSequencingParameters() != null) {
          errors.add(new ValidationError(
              "Sequencing parameters should not be set at the %s level for %s runs"
                  .formatted(platformType.getContainerName(), platformType.getKey())));
          break;
        }
      }
    }
  }

  private void applyChanges(Run target, Run source) throws IOException {
    target.setAlias(source.getAlias());
    target.setAccession(source.getAccession());
    target.setDescription(source.getDescription());
    target.setFilePath(source.getFilePath());
    target.setHealth(source.getHealth());
    target.setStartDate(source.getStartDate());
    target.setCompletionDate(source.getCompletionDate());

    makeContainerChangesChangeLog(target, target.getSequencerPartitionContainers(),
        source.getSequencerPartitionContainers());
    applyContainerChanges(target, source);
    target.setSequencingParameters(source.getSequencingParameters());
    target.setSequencer(source.getSequencer());
    target.setSequencingKit(source.getSequencingKit());
    target.setSequencingKitLot(source.getSequencingKitLot());
    target.setQcPassed(source.getQcPassed());
    target.setQcUser(source.getQcUser());
    target.setQcDate(source.getQcDate());
    target.setDataReview(source.getDataReview());
    target.setDataReviewer(source.getDataReviewer());
    target.setDataReviewDate(source.getDataReviewDate());
    target.setSop(source.getSop());
    target.setDataManglingPolicy(source.getDataManglingPolicy());
    if (isIlluminaRun(target)) {
      applyIlluminaChanges((IlluminaRun) target, (IlluminaRun) source);
    } else if (isLS454Run(target)) {
      applyLS454Changes((LS454Run) target, (LS454Run) source);
    } else if (isSolidRun(target)) {
      applySolidChanges((SolidRun) target, (SolidRun) source);
    } else if (isOxfordNanoporeRun(target)) {
      applyOxfordNanoporeChanges((OxfordNanoporeRun) target, (OxfordNanoporeRun) source);
    }
    // Source metrics will only be set when coming from Run Scanner
    if (source.getMetrics() != null) {
      target.setMetrics(source.getMetrics());
    }
  }

  private void applyContainerChanges(Run target, Run source) throws IOException {
    Iterator<RunPosition> iterator = target.getRunPositions().iterator();
    while (iterator.hasNext()) {
      RunPosition existingPos = iterator.next();
      if (source.getRunPositions().stream().noneMatch(rp -> isSamePosition(rp, existingPos))) {
        runPartitionService.deleteForRunContainer(target, existingPos.getContainer());
        runPartitionAliquotService.deleteForRunContainer(target, existingPos.getContainer());
        iterator.remove();
      }
    }
    for (RunPosition sourcePos : source.getRunPositions()) {
      RunPosition existingPos = target.getRunPositions().stream()
          .filter(rp -> isSamePosition(rp, sourcePos))
          .findFirst().orElse(null);
      if (existingPos == null) {
        RunPosition newPos = new RunPosition();
        newPos.setRun(target);
        newPos.setContainer(sourcePos.getContainer());
        newPos.setPosition(sourcePos.getPosition());
        newPos.setSequencingParameters(sourcePos.getSequencingParameters());
        target.getRunPositions().add(newPos);
      } else {
        existingPos.setContainer(sourcePos.getContainer());
        existingPos.setSequencingParameters(sourcePos.getSequencingParameters());
      }
    }
  }

  private static boolean isSamePosition(RunPosition pos1, RunPosition pos2) {
    String pos1Alias = pos1.getPosition() == null ? null : pos1.getPosition().getAlias();
    String pos2Alias = pos2.getPosition() == null ? null : pos2.getPosition().getAlias();
    if (pos1Alias == null && pos2Alias == null) {
      return true;
    }
    if (pos1Alias == null) {
      return false;
    }
    return pos1Alias.equals(pos2Alias);
  }

  private void applyIlluminaChanges(IlluminaRun target, IlluminaRun source) {
    target.setCallCycle(source.getCallCycle());
    target.setImgCycle(source.getImgCycle());
    target.setNumCycles(source.getNumCycles());
    target.setScoreCycle(source.getScoreCycle());
    target.setPairedEnd(source.getPairedEnd());
    target.setRunBasesMask(source.getRunBasesMask());
    target.setWorkflowType(source.getWorkflowType());
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
   * If any containers were added or removed from the run, generates and saves a single Run changelog
   * entry and one Container changelog entry for each Container affected. May be called before or
   * after managedRun is updated, as the original and updated container list are both provided
   * separately
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

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  public void setSequencingParametersService(SequencingParametersService sequencingParametersService) {
    this.sequencingParametersService = sequencingParametersService;
  }

  public void setInstrumentService(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
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
  public boolean processNotification(Run source) throws IOException, MisoNamingException {
    User user = userService.getByLoginName("notification");
    final Run target;

    Run runFromDb = runDao.getByAlias(source.getAlias());
    boolean isNew;

    if (runFromDb == null) {
      target = source.getPlatformType().createRun();
      target.setAlias(source.getAlias());
      target.setSequencer(source.getSequencer());
      isNew = true;
    } else {
      target = runFromDb;
      // Detach to keep separate from the entity that will be updated if/when we call the update method
      hibernateUtilDao.detach(target);
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
    isMutated |= updateContainerFromNotification(target, source);
    isMutated |= updateHealthFromNotification(source, target, user);
    isMutated |= updateSequencingKitFromNotification(target, source.getSequencingKit());
    isMutated |= updateDataManglingPolicyFromNotification(target, source.getDataManglingPolicy(), user);

    switch (source.getPlatformType()) {
      case ILLUMINA:
        isMutated |= updateIlluminaRunFromNotification((IlluminaRun) source, (IlluminaRun) target);
        break;
      case LS454:
        isMutated |= updateField(((LS454Run) source).getCycles(), ((LS454Run) target).getCycles(),
            ((LS454Run) target)::setCycles);
        isMutated |= updateField(source.getPairedEnd(), target.getPairedEnd(), target::setPairedEnd);
        break;
      case OXFORDNANOPORE:
        isMutated |= updateField(((OxfordNanoporeRun) source).getMinKnowVersion(),
            ((OxfordNanoporeRun) target).getMinKnowVersion(),
            ((OxfordNanoporeRun) target)::setMinKnowVersion);
        isMutated |= updateField(((OxfordNanoporeRun) source).getProtocolVersion(),
            ((OxfordNanoporeRun) target).getProtocolVersion(),
            ((OxfordNanoporeRun) target)::setProtocolVersion);
        break;
      case IONTORRENT:
      case PACBIO:
      case SOLID:
        // Nothing to do
        break;
      default:
        throw new NotImplementedException();
    }

    isMutated |= updateSequencingParameters(target, user, source.getSequencingParameters());

    if (isNew) {
      create(target);
    } else if (isMutated) {
      update(target);
    }
    return isNew;
  }

  private boolean updateMetricsFromNotification(Run source, Run target) {
    if (source.getMetrics() == null) {
      return false;
    }
    if (source.getMetrics().equals(target.getMetrics()))
      return false;
    if (target.getMetrics() == null) {
      target.setMetrics(source.getMetrics());
      return true;
    }

    // Use separate ObjectMapper to allow <>& characters, which may be used in metrics
    ObjectMapper metricsMapper = new ObjectMapper();
    ArrayNode sourceMetrics;
    try {
      sourceMetrics = metricsMapper.readValue(source.getMetrics(), ArrayNode.class);
    } catch (IOException e) {
      log.error("Impossible junk metrics were passed in for run " + target.getId(), e);
      return false;
    }
    ArrayNode targetMetrics;
    try {
      targetMetrics = metricsMapper.readValue(target.getMetrics(), ArrayNode.class);
    } catch (IOException e) {
      log.error("The database is full of garbage metrics for run " + target.getId(), e);
      return false;
    }
    Map<String, JsonNode> sourceMetricsMap = parseMetrics(sourceMetrics);
    Map<String, JsonNode> targetMetricsMap = parseMetrics(targetMetrics);
    boolean changed = false;
    for (Map.Entry<String, JsonNode> x : sourceMetricsMap.entrySet()) {
      if (!targetMetricsMap.containsKey(x.getKey()) || !targetMetricsMap.get(x.getKey()).equals(x.getValue())) {
        targetMetricsMap.put(x.getKey(), x.getValue());
        changed = true;
      }
    }
    if (changed) {
      ArrayNode combinedMetrics = metricsMapper.createArrayNode();
      combinedMetrics.addAll(targetMetricsMap.values());
      try {
        target.setMetrics(metricsMapper.writeValueAsString(combinedMetrics));
        return true;
      } catch (JsonProcessingException e) {
        log.error("Failed to save data just unserialised.", e);
        return false;
      }
    }
    return false;
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
    isMutated |= updateField(source.getRunBasesMask(), target.getRunBasesMask(), target::setRunBasesMask);
    isMutated |= updateField(source.getWorkflowType(), target.getWorkflowType(), target::setWorkflowType);
    return isMutated;
  }

  private boolean updateSequencingParameters(Run target, User user, SequencingParameters params) throws IOException {
    // container-level params are handled in updateContainerFromNotification
    if (params == null || target.getPlatformType().hasContainerLevelParameters()) {
      return false;
    }
    // Only update if the sequencing parameters haven't been updated by a human
    if (!target.didSomeoneElseChangeColumn("sequencingParameters_parametersId", user)) {
      if (target.getSequencingParameters() == null
          || params.getId() != target.getSequencingParameters().getId()) {
        target.setSequencingParameters(params);
        return true;
      }
    }
    return false;
  }

  private boolean updateContainerFromNotification(Run target, Run source) throws IOException {
    boolean isMutated = false;
    for (RunPosition sourcePosition : source.getRunPositions()) {
      if (sourcePosition.getContainer() == null) {
        continue;
      }
      String serialNumber = sourcePosition.getContainer().getIdentificationBarcode();
      Collection<SequencerPartitionContainer> containers = containerService.listByBarcode(serialNumber);
      switch (containers.size()) {
        case 0:
          RunPosition newRunPos = new RunPosition();
          newRunPos.setRun(target);
          newRunPos.setContainer(sourcePosition.getContainer());
          newRunPos.setPosition(sourcePosition.getPosition());
          newRunPos.setSequencingParameters(sourcePosition.getSequencingParameters());
          target.getRunPositions().add(newRunPos);
          isMutated = true;
          break;
        case 1:
          SequencerPartitionContainer sourceContainer = sourcePosition.getContainer();
          SequencerPartitionContainer targetContainer = containers.iterator().next();
          if (sourceContainer.getPartitions().size() != targetContainer.getPartitions().size()) {
            throw new IllegalArgumentException(
                String.format("The container %s has %d partitions, but %d were detected by the scanner.", serialNumber,
                    targetContainer.getPartitions().size(), sourceContainer.getPartitions().size()));
          }

          // only update container model from fallback to non-fallback
          if (targetContainer.getModel().isFallback() && !sourceContainer.getModel().isFallback()) {
            targetContainer.setModel(sourceContainer.getModel());
            isMutated = true;
          }
          for (Partition targetPartition : targetContainer.getPartitions()) {
            Partition sourcePartition = sourceContainer.getPartitionAt(targetPartition.getPartitionNumber());
            if (sourcePartition.getPool() != null && targetPartition.getPool() == null) {
              targetPartition.setPool(sourcePartition.getPool());
              isMutated = true;
            }
          }

          RunPosition targetPosition = target.getRunPositions().stream()
              .filter(pos -> pos.getContainer() != null && pos.getContainer().getId() == targetContainer.getId())
              .findFirst().orElse(null);
          if (targetPosition == null) {
            targetPosition = target.addSequencerPartitionContainer(targetContainer, sourcePosition.getPosition());
            isMutated = true;
          } else {
            validatePositionMatch(targetPosition, sourcePosition, targetContainer);
          }

          if ((targetPosition.getSequencingParameters() == null) != (sourcePosition.getSequencingParameters() == null)
              || (sourcePosition.getSequencingParameters() != null
                  && targetPosition.getSequencingParameters().getId() != sourcePosition.getSequencingParameters()
                      .getId())) {
            targetPosition.setSequencingParameters(sourcePosition.getSequencingParameters());
            isMutated = true;
          }
          break;
        default:
          throw new IllegalArgumentException("Multiple containers with same identifier: " + serialNumber);
      }
    }
    return isMutated;
  }

  private static void validatePositionMatch(RunPosition targetPosition, RunPosition sourcePosition,
      SequencerPartitionContainer targetContainer) {
    InstrumentPosition targetInstrumentPosition = targetPosition.getPosition();
    InstrumentPosition sourceInstrumentPosition = sourcePosition.getPosition();
    if ((targetInstrumentPosition == null) != (sourceInstrumentPosition == null)
        || (targetInstrumentPosition != null && sourceInstrumentPosition != null
            && targetInstrumentPosition.getId() != sourceInstrumentPosition.getId())) {
      throw new IllegalArgumentException(
          "Container %s is already in position %s, but the scanner detected it in position %s".formatted(
              targetContainer.getIdentificationBarcode(),
              targetPosition.getPosition() == null ? null : targetPosition.getPosition().getAlias(),
              sourcePosition.getPosition() == null ? null : sourcePosition.getPosition().getAlias()));
    }
  }

  private boolean updateHealthFromNotification(Run notification, final Run managed, User user) {
    if (notification.getHealth() == null || notification.getHealth() == managed.getHealth()) {
      // server sent us nothing, or no change
      return false;
    } else if (notification.getHealth() == HealthType.Unknown) {
      // If it is sending us (effectively) an error, don't update the health if we have something already.
      if (managed.getHealth() == null) {
        managed.setHealth(notification.getHealth());
        managed.setCompletionDate(notification.getHealth().isDone() ? notification.getCompletionDate() : null);
        return true;
      }
    } else {
      if (!managed.didSomeoneElseChangeColumn("health", user)
          || (!managed.getHealth().isDone() && notification.getHealth().isDone())) {
        // A human user has never changed the health of this run, so we will.
        // Alternatively, a human set the status to not-done but runscanner has indicated that the run is
        // now done, so we will update with
        // this.
        managed.setHealth(notification.getHealth());
        LocalDate completionDate = null;
        if (notification.getHealth().isDone()) {
          // RunScanner might not have been able to figure out a date this run was completed. If so, use the
          // existing completion date,
          // otherwise, guess.
          if (notification.getCompletionDate() != null) {
            completionDate = notification.getCompletionDate();
          } else if (managed.getCompletionDate() != null) {
            completionDate = managed.getCompletionDate();
          } else {
            completionDate = LocalDate.now(ZoneId.systemDefault());
          }
        }
        managed.setCompletionDate(completionDate);
        return true;
      }
    }
    return false;
  }

  private boolean updateSequencingKitFromNotification(Run target, KitDescriptor kit) throws IOException {
    if (kit == null) {
      boolean changed = target.getSequencingKit() != null;
      target.setSequencingKit(null);
      return changed;
    }
    KitDescriptor managedKit =
        kitDescriptorService.getByPartNumber(kit.getPartNumber(), KitType.SEQUENCING, target.getPlatformType());
    if (managedKit == null) {
      managedKit = kitDescriptorService.getByName(kit.getName());
      if (managedKit == null || managedKit.getKitType() != KitType.SEQUENCING
          || managedKit.getPlatformType() != target.getPlatformType()) {
        return false;
      }
    }
    boolean changed = target.getSequencingKit() == null || target.getSequencingKit().getId() != managedKit.getId();
    target.setSequencingKit(managedKit);
    return changed;
  }

  private boolean updateDataManglingPolicyFromNotification(Run target, InstrumentDataManglingPolicy policy, User user) {
    if (target.didSomeoneElseChangeColumn("dataManglingPolicy", user)) {
      return false;
    }
    return updateField(policy, target.getDataManglingPolicy(), target::setDataManglingPolicy);
  }

  private <T> boolean updateField(T newValue, T oldValue, Consumer<T> writer) {
    if (newValue == null) {
      return false;
    }
    if (newValue.equals(oldValue)) {
      return false;
    }
    writer.accept(newValue);
    return true;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return runDao.count(errorHandler, filter);
  }

  @Override
  public List<Run> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return runDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public void authorizeDeletion(Run object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public void beforeDelete(Run object) throws IOException {
    fileAttachmentService.beforeDelete(object);
    runPartitionService.deleteForRun(object);
    for (SequencerPartitionContainer container : object.getSequencerPartitionContainers()) {
      runPartitionAliquotService.deleteForRunContainer(object, container);
    }
  }

  @Override
  public void afterDelete(Run object) throws IOException {
    fileAttachmentService.afterDelete(object);
  }

}
