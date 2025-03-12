package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.RunLibraryQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.RunLibraryQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultRunLibraryQcStatusService extends AbstractSaveService<RunLibraryQcStatus>
    implements RunLibraryQcStatusService {

  @Autowired
  private RunLibraryQcStatusDao runLibraryQcStatusDao;
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
  public List<RunLibraryQcStatus> listByIdList(List<Long> ids) throws IOException {
    return runLibraryQcStatusDao.listByIdList(ids);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<RunLibraryQcStatus> list() throws IOException {
    return runLibraryQcStatusDao.list();
  }

  @Override
  public SaveDao<RunLibraryQcStatus> getDao() {
    return runLibraryQcStatusDao;
  }

  @Override
  public RunLibraryQcStatus getByDescription(String description) throws IOException {
    return runLibraryQcStatusDao.getByDescription(description);
  }

  @Override
  protected void collectValidationErrors(RunLibraryQcStatus object, RunLibraryQcStatus beforeChange,
      List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(RunLibraryQcStatus::getDescription, object, beforeChange)
        && runLibraryQcStatusDao.getByDescription(object.getDescription()) != null) {
      errors.add(ValidationError.forDuplicate("run-library QC status", "description"));
    }
  }

  @Override
  protected void applyChanges(RunLibraryQcStatus to, RunLibraryQcStatus from) throws IOException {
    to.setDescription(from.getDescription());
    to.setQcPassed(from.getQcPassed());
  }

  @Override
  protected void authorizeUpdate(RunLibraryQcStatus object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public ValidationResult validateDeletion(RunLibraryQcStatus object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = runLibraryQcStatusDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, "run " + Pluralizer.libraries(usage)));
    }
    return result;
  }

}
