package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AssayService;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
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
  private ChangeLogService changeLogService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
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
    ValidationUtils.loadChildEntity(object::setAssay, object.getAssay(), assayService, "assayId");
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
  }

  @Override
  protected void applyChanges(Requisition to, Requisition from) throws IOException {
    to.setAlias(from.getAlias());
    to.setAssay(from.getAssay());
    to.setStopped(from.isStopped());
    to.setStopReason(from.getStopReason());
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
  }

  @Override
  public void addNote(Requisition entity, Note note) throws IOException {
    Requisition managed = requisitionDao.get(entity.getId());
    note.setCreationDate(new Date());
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
  public Requisition moveToRequisition(Requisition template, List<Sample> samples)
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
      Sample managedSample = sampleService.get(sample.getId());
      managedSample.setRequisition(requisition);
      sampleService.save(managedSample);
    }
    return requisition;
  }

  @Override
  public void addSupplementalSamples(Requisition requisition, Collection<Sample> samples) throws IOException {
    for (Sample sample : samples) {
      if (LimsUtils.isDetailedSample(sample) && ((DetailedSample) sample).isSynthetic()) {
        throw new ValidationException("Ghost samples cannot be added as supplemental samples");
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
        .append(" supplementary ")
        .append(Pluralizer.samples(samples.size()))
        .append(": ")
        .append(samples.stream()
            .map(sample -> "%s (%s)".formatted(sample.getName(), sample.getAlias()))
            .collect(Collectors.joining("; ")));

    ChangeLog change =
        requisition.createChangeLog(sb.toString(), "supplementary samples", authorizationManager.getCurrentUser());
    changeLogService.create(change);
  }

}
