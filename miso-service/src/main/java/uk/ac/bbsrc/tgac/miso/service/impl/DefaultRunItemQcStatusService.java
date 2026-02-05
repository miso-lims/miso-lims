package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.RunItemQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.RunItemQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultRunItemQcStatusService extends AbstractSaveService<RunItemQcStatus>
    implements RunItemQcStatusService {

  @Autowired
  private RunItemQcStatusDao runItemQcStatusDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<RunItemQcStatus> listByIdList(List<Long> ids) throws IOException {
    return runItemQcStatusDao.listByIdList(ids);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<RunItemQcStatus> list() throws IOException {
    return runItemQcStatusDao.list();
  }

  @Override
  public SaveDao<RunItemQcStatus> getDao() {
    return runItemQcStatusDao;
  }

  @Override
  public RunItemQcStatus getByDescription(String description) throws IOException {
    return runItemQcStatusDao.getByDescription(description);
  }

  @Override
  protected void collectValidationErrors(RunItemQcStatus object, RunItemQcStatus beforeChange,
                                         List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(RunItemQcStatus::getDescription, object, beforeChange)
        && runItemQcStatusDao.getByDescription(object.getDescription()) != null) {
      errors.add(ValidationError.forDuplicate("run-Item QC status", "description"));
    }
  }

  @Override
  protected void applyChanges(RunItemQcStatus to, RunItemQcStatus from) throws IOException {
    to.setDescription(from.getDescription());
    to.setQcPassed(from.getQcPassed());
  }

  @Override
  protected void authorizeUpdate(RunItemQcStatus object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public ValidationResult validateDeletion(RunItemQcStatus object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = runItemQcStatusDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, "run " + Pluralizer.libraries(usage)));
    }
    return result;
  }

}
