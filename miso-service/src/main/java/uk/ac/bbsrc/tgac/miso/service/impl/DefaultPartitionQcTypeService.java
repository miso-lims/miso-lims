package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.PartitionQcTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.PartitionQcTypeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultPartitionQcTypeService extends AbstractSaveService<PartitionQCType>
    implements PartitionQcTypeService {

  @Autowired
  private PartitionQcTypeDao partitionQcTypeDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;

  public void setPartitionQcTypeDao(PartitionQcTypeDao partitionQcTypeDao) {
    this.partitionQcTypeDao = partitionQcTypeDao;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
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
  public SaveDao<PartitionQCType> getDao() {
    return partitionQcTypeDao;
  }

  @Override
  protected void authorizeUpdate(PartitionQCType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(PartitionQCType object, PartitionQCType beforeChange,
      List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(PartitionQCType::getDescription, object, beforeChange)
        && partitionQcTypeDao.getByDescription(object.getDescription()) != null) {
      errors.add(new ValidationError("description", "There is already a partition QC type with this description"));
    }
  }

  @Override
  protected void applyChanges(PartitionQCType to, PartitionQCType from) {
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
  public List<PartitionQCType> listByIdList(List<Long> ids) throws IOException {
    return partitionQcTypeDao.listByIdList(ids);
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
