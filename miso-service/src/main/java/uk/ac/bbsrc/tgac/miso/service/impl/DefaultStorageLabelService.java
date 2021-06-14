package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLabel;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StorageLabelService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.StorageLabelDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStorageLabelService extends AbstractSaveService<StorageLabel> implements StorageLabelService {

  @Autowired
  private StorageLabelDao storageLabelDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<StorageLabel> listByIdList(List<Long> ids) throws IOException {
    return storageLabelDao.listByIdList(ids);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<StorageLabel> list() throws IOException {
    return storageLabelDao.list();
  }

  @Override
  public SaveDao<StorageLabel> getDao() {
    return storageLabelDao;
  }

  @Override
  protected void collectValidationErrors(StorageLabel object, StorageLabel beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isChanged(StorageLabel::getLabel, object, beforeChange) && storageLabelDao.getByLabel(object.getLabel()) != null) {
      errors.add(ValidationError.forDuplicate("storage label", "label"));
    }
  }

  @Override
  protected void applyChanges(StorageLabel to, StorageLabel from) throws IOException {
    to.setLabel(from.getLabel());
  }

  @Override
  public ValidationResult validateDeletion(StorageLabel object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = storageLabelDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, "storage " + Pluralizer.locations(usage)));
    }
    return result;
  }

  @Override
  protected void authorizeUpdate(StorageLabel object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

}
