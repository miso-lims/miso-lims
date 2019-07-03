package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.PartitionQcTypeDao;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultPartitionQcTypeService implements PartitionQcTypeService {

  @Autowired
  private PartitionQcTypeDao partitionQcTypeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  public void setPartitionQcTypeDao(PartitionQcTypeDao partitionQcTypeDao) {
    this.partitionQcTypeDao = partitionQcTypeDao;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
  }

  @Override
  public PartitionQCType get(long id) throws IOException {
    return partitionQcTypeDao.get(id);
  }

  @Override
  public long create(PartitionQCType type) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(type, null);
    return partitionQcTypeDao.create(type);
  }

  @Override
  public long update(PartitionQCType type) throws IOException {
    authorizationManager.throwIfNonAdmin();
    PartitionQCType managed = get(type.getId());
    validateChange(type, managed);
    applyChanges(managed, type);
    return partitionQcTypeDao.update(managed);
  }

  private void validateChange(PartitionQCType type, PartitionQCType beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(PartitionQCType::getDescription, type, beforeChange)
        && partitionQcTypeDao.getByDescription(type.getDescription()) != null) {
      errors.add(new ValidationError("description", "There is already a partition QC type with this description"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(PartitionQCType to, PartitionQCType from) {
    to.setDescription(from.getDescription());
    to.setNoteRequired(from.isNoteRequired());
    to.setOrderFulfilled(from.isOrderFulfilled());
    to.setAnalysisSkipped(from.isAnalysisSkipped());
  }

  @Override
  public List<PartitionQCType> list() throws IOException {
    return partitionQcTypeDao.list();
  }

  @Override
  public ValidationResult validateDeletion(PartitionQCType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = partitionQcTypeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.partitions(usage)));
    }
    return result;
  }

}
