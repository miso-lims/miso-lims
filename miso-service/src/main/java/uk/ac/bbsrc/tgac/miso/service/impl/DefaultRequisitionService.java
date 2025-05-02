package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isIdentitySample;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Requisitionable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionPause;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AssayService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.HibernateUtilDao;
import uk.ac.bbsrc.tgac.miso.persistence.RequisitionDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultRequisitionService extends AbstractSaveService<Requisition> implements RequisitionService {

  @Autowired
  private RequisitionDao requisitionDao;
  @Autowired
  private AssayService assayService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private HibernateUtilDao hibernateUtilDao;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public Requisition getByAlias(String alias) throws IOException {
    return requisitionDao.getByAlias(alias);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return requisitionDao.count(errorHandler, filter);
  }

  @Override
  public List<Requisition> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return requisitionDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public SaveDao<Requisition> getDao() {
    return requisitionDao;
  }

  @Override
  protected void loadChildEntities(Requisition object) throws IOException {
    Set<Assay> loadedAssays = new HashSet<>();
    for (Assay assay : object.getAssays()) {
      ValidationUtils.loadChildEntity(loadedAssays::add, assay, assayService, "assays");
    }
    object.setAssays(loadedAssays);
  }

  @Override
  protected void collectValidationErrors(Requisition object, Requisition beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(Requisition::getAlias, object, beforeChange)
        && requisitionDao.getByAlias(object.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("requisition", "alias"));
    }
    if (object.isStopped()) {
      if (object.getStopReason() == null) {
        errors.add(new ValidationError("stopReason", "Required if the requisition is stopped"));
      }
    } else {
      if (object.getStopReason() != null) {
        errors.add(new ValidationError("stopReason", "Invalid if the requisition is not stopped"));
      }
    }
    validatePauses(object.getPauses(), errors);
  }

  private void validatePauses(List<RequisitionPause> pauses, List<ValidationError> errors) {
    for (int i = 0; i < pauses.size(); i++) {
      RequisitionPause pause = pauses.get(i);
      if (pause.getEndDate() != null) {
        long pauseDays = pause.getStartDate().until(pause.getEndDate()).getDays();
        if (pauseDays < 1) {
          errors.add(new ValidationError("pauses", "Pause length must be at least one day"));
        }
      }
      for (int j = i + 1; j < pauses.size(); j++) {
        RequisitionPause other = pauses.get(j);
        if (isBetween(pause.getStartDate(), other.getStartDate(), other.getEndDate())
            || isBetween(pause.getEndDate(), other.getStartDate(), other.getEndDate())) {
          errors.add(new ValidationError("pauses", "Cannot have overlapping pauses"));
          break;
        }
      }
    }
  }

  private static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
    if (date == null) {
      return end == null;
    } else {
      return (date.isEqual(start) || date.isAfter(start))
          && (end == null || date.isEqual(end) || date.isBefore(end));
    }
  }

  @Override
  protected void applyChanges(Requisition to, Requisition from) throws IOException {
    to.setAlias(from.getAlias());
    ValidationUtils.applySetChanges(to.getAssays(), from.getAssays());
    to.setStopped(from.isStopped());
    to.setStopReason(from.getStopReason());
    applyPauseChanges(to, from);
  }

  private void applyPauseChanges(Requisition to, Requisition from) throws IOException {
    User user = authorizationManager.getCurrentUser();
    List<RequisitionPause> toRemove = to.getPauses().stream()
        .filter(toPause -> from.getPauses().stream().noneMatch(fromPause -> fromPause.getId() == toPause.getId()))
        .toList();
    for (RequisitionPause pause : toRemove) {
      to.getPauses().remove(pause);
      String message = null;
      if (pause.getEndDate() == null) {
        message = "Cancelled pause starting %s".formatted(LimsUtils.formatDate(pause.getStartDate()));
      } else {
        message = "Cancelled pause from %s to %s".formatted(LimsUtils.formatDate(pause.getStartDate()),
            LimsUtils.formatDate(pause.getEndDate()));
      }
      makePauseChangelog(to, user, message);
    }
    for (RequisitionPause fromPause : from.getPauses()) {
      if (fromPause.isSaved()) {
        RequisitionPause toPause =
            to.getPauses().stream().filter(x -> x.getId() == fromPause.getId()).findFirst().orElse(null);
        if (toPause == null) {
          throw new ValidationException(new ValidationError("pauses", "Submitted pause ID not found"));
        }
        if (fromPause.getEndDate() != null
            && (toPause.getEndDate() == null || !toPause.getEndDate().equals(fromPause.getEndDate()))) {
          toPause.setEndDate(fromPause.getEndDate());
          makePauseChangelog(to, user,
              "Requisition resumed effective %s".formatted(LimsUtils.formatDate(fromPause.getEndDate())));
        }
      } else {
        to.getPauses().add(fromPause);
        makePauseChangelog(to, user,
            "Requisition paused effective %s".formatted(LimsUtils.formatDate(fromPause.getStartDate())));
      }
    }
  }

  private void makePauseChangelog(Requisition requisition, User user, String message) throws IOException {
    changeLogService.create(requisition.createChangeLog(message, "pauses", user));
  }

  @Override
  protected void beforeSave(Requisition object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public void authorizeDeletion(Requisition object) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(object.getCreator());
  }

  @Override
  public void beforeDelete(Requisition object) throws IOException {
    List<Sample> samples = sampleService.list(0, 0, false, "id", PaginationFilter.requisitionId(object.getId()));
    for (Sample sample : samples) {
      sample.setRequisition(null);
      sampleService.update(sample);
    }
    List<Sample> supplementalSamples =
        sampleService.list(0, 0, false, "id", PaginationFilter.supplementalToRequisitionId(object.getId()));
    for (Sample sample : supplementalSamples) {
      RequisitionSupplementalSample supplemental = requisitionDao.getSupplementalSample(object, sample);
      requisitionDao.removeSupplementalSample(supplemental);
    }
    List<Library> libraries = libraryService.list(0, 0, false, "id", PaginationFilter.requisitionId(object.getId()));
    for (Library library : libraries) {
      library.setRequisition(null);
      libraryService.update(library);
    }
    List<Library> supplementalLibraries =
        libraryService.list(0, 0, false, "id", PaginationFilter.supplementalToRequisitionId(object.getId()));
    for (Library library : supplementalLibraries) {
      RequisitionSupplementalLibrary supplemental = requisitionDao.getSupplementalLibrary(object, library);
      requisitionDao.removeSupplementalLibrary(supplemental);
    }
  }

  @Override
  public void addNote(Requisition entity, Note note) throws IOException {
    Requisition managed = requisitionDao.get(entity.getId());
    note.setCreationDate(LocalDate.now(ZoneId.systemDefault()));
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    managed.setLastModifier(authorizationManager.getCurrentUser());
    requisitionDao.update(managed);
  }

  @Override
  public void deleteNote(Requisition entity, Long noteId) throws IOException {
    if (noteId == null) {
      throw new IllegalArgumentException("Cannot delete an unsaved note");
    }
    Requisition managed = requisitionDao.get(entity.getId());
    Note deleteNote = managed.getNotes().stream()
        .filter(n -> n.getId() == noteId.longValue())
        .findFirst().orElse(null);
    if (deleteNote == null) {
      throw new IOException(String.format("Note %d not found for requisition %d", noteId, entity.getId()));
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    requisitionDao.update(managed);
  }

  @Override
  public Requisition moveSamplesToRequisition(Requisition template, List<Sample> samples)
      throws IOException {
    Requisition existing = null;
    if (template.isSaved()) {
      existing = get(template.getId());
    } else {
      existing = getByAlias(template.getAlias());
      if (existing != null) {
        throw new ValidationException(ValidationError.forDuplicate("requisition", "alias"));
      }
      long savedId = create(template);
      existing = get(savedId);
    }
    final Requisition requisition = existing;
    for (Sample sample : samples) {
      Sample loadedSample = sampleService.get(sample.getId());
      // Detach to maintain separation between pending changes and managed entity
      hibernateUtilDao.detach(loadedSample);
      loadedSample.setRequisition(requisition);
      sampleService.save(loadedSample);
    }
    return requisition;
  }

  @Override
  public void addSupplementalLibraries(Requisition requisition, Collection<Library> libraries) throws IOException {
    List<Library> managed = libraryService.listByIdList(libraries.stream().map(Library::getId).toList());
    for (Library library : managed) {
      RequisitionSupplementalLibrary supplemental = new RequisitionSupplementalLibrary(requisition.getId(), library);
      requisitionDao.saveSupplementalLibrary(supplemental);
    }
    addSupplementalLibraryChange(requisition, libraries, true);
  }

  @Override
  public void removeSupplementalLibraries(Requisition requisition, Collection<Library> libraries) throws IOException {
    for (Library library : libraries) {
      RequisitionSupplementalLibrary supplemental = requisitionDao.getSupplementalLibrary(requisition, library);
      if (supplemental == null) {
        throw new ValidationException("Supplemental library %s not found".formatted(library.getAlias()));
      }
      requisitionDao.removeSupplementalLibrary(supplemental);
    }
    addSupplementalLibraryChange(requisition, libraries, false);
  }

  private void addSupplementalLibraryChange(Requisition requisition, Collection<Library> libraries, boolean addition)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append(addition ? "Added" : "Removed")
        .append(" supplemental ")
        .append(Pluralizer.libraries(libraries.size()))
        .append(": ")
        .append(libraries.stream()
            .map(sample -> "%s (%s)".formatted(sample.getName(), sample.getAlias()))
            .collect(Collectors.joining("; ")));

    ChangeLog change =
        requisition.createChangeLog(sb.toString(), "supplemental libraries", authorizationManager.getCurrentUser());
    changeLogService.create(change);
  }

  @Override
  public Requisition moveLibrariesToRequisition(Requisition template, List<Library> libraries)
      throws IOException {
    Requisition existing = null;
    if (template.isSaved()) {
      existing = get(template.getId());
    } else {
      existing = getByAlias(template.getAlias());
      if (existing != null) {
        throw new ValidationException(ValidationError.forDuplicate("requisition", "alias"));
      }
      long savedId = create(template);
      existing = get(savedId);
    }
    final Requisition requisition = existing;
    for (Library library : libraries) {
      Library loadedLibrary = libraryService.get(library.getId());
      // Detach to maintain separation between pending changes and managed entity
      hibernateUtilDao.detach(loadedLibrary);
      loadedLibrary.setRequisition(requisition);
      libraryService.update(loadedLibrary);
    }
    return requisition;
  }

  @Override
  public void addSupplementalSamples(Requisition requisition, Collection<Sample> samples) throws IOException {
    List<Sample> managed = sampleService.listByIdList(samples.stream().map(Sample::getId).toList());
    for (Sample sample : managed) {
      if (LimsUtils.isDetailedSample(sample)) {
        if (isIdentitySample(sample)) {
          throw new ValidationException("Identity samples cannot be added as supplemental samples");
        } else if (((DetailedSample) sample).isSynthetic()) {
          throw new ValidationException("Ghost samples cannot be added as supplemental samples");
        }
      }
      RequisitionSupplementalSample supplemental = new RequisitionSupplementalSample(requisition.getId(), sample);
      requisitionDao.saveSupplementalSample(supplemental);
    }
    addSupplementalSampleChange(requisition, samples, true);
  }

  @Override
  public void removeSupplementalSamples(Requisition requisition, Collection<Sample> samples) throws IOException {
    for (Sample sample : samples) {
      RequisitionSupplementalSample supplemental = requisitionDao.getSupplementalSample(requisition, sample);
      if (supplemental == null) {
        throw new ValidationException("Supplemental sample %s not found".formatted(sample.getAlias()));
      }
      requisitionDao.removeSupplementalSample(supplemental);
    }
    addSupplementalSampleChange(requisition, samples, false);
  }

  private void addSupplementalSampleChange(Requisition requisition, Collection<Sample> samples, boolean addition)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append(addition ? "Added" : "Removed")
        .append(" supplemental ")
        .append(Pluralizer.samples(samples.size()))
        .append(": ")
        .append(samples.stream()
            .map(sample -> "%s (%s)".formatted(sample.getName(), sample.getAlias()))
            .collect(Collectors.joining("; ")));

    ChangeLog change =
        requisition.createChangeLog(sb.toString(), "supplemental samples", authorizationManager.getCurrentUser());
    changeLogService.create(change);
  }

  @Override
  public List<Requisition> listByIdList(List<Long> ids) throws IOException {
    return requisitionDao.listByIdList(ids);
  }

  @Override
  public void findOrCreateRequisition(Requisitionable item) throws IOException {
    if (item.getRequisition() != null && !item.getRequisition().isSaved()) {
      Requisition existing = getByAlias(item.getRequisition().getAlias());
      if (existing == null) {
        long requisitionId = create(item.getRequisition());
        item.getRequisition().setId(requisitionId);
      } else {
        item.setRequisition(existing);
      }
    }
  }

}
