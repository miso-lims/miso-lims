package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LabDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLabService extends AbstractSaveService<Lab> implements LabService {

  @Autowired
  private LabDao labDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public SaveDao<Lab> getDao() {
    return labDao;
  }

  public void setLabDao(LabDao labDao) {
    this.labDao = labDao;
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  protected void authorizeCreate(Lab object) throws IOException {
    // Do nothing - anyone can create
  }

  @Override
  protected void authorizeUpdate(Lab object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(Lab object, Lab beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isChanged(Lab::getAlias, object, beforeChange) && labDao.getByAlias(object.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("lab", "alias"));
    }
  }

  @Override
  protected void applyChanges(Lab to, Lab from) throws IOException {
    to.setAlias(from.getAlias());
    to.setArchived(from.isArchived());
  }

  @Override
  protected void beforeSave(Lab object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public List<Lab> list() throws IOException {
    return labDao.list();
  }

  @Override
  public List<Lab> listByIdList(List<Long> ids) throws IOException {
    return labDao.listByIdList(ids);
  }

  @Override
  public ValidationResult validateDeletion(Lab object) {
    ValidationResult result = new ValidationResult();

    long tissueUsage = labDao.getUsageByTissues(object);
    if (tissueUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, tissueUsage, Pluralizer.samples(tissueUsage)));
    }
    long transferUsage = labDao.getUsageByTransfers(object);
    if (transferUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, transferUsage, Pluralizer.transfers(transferUsage)));
    }

    return result;
  }

}
