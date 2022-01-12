package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueMaterialService extends AbstractSaveService<TissueMaterial> implements TissueMaterialService {

  @Autowired
  private TissueMaterialDao tissueMaterialDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public List<TissueMaterial> list() throws IOException {
    return tissueMaterialDao.list();
  }

  @Override
  protected void authorizeUpdate(TissueMaterial object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void beforeSave(TissueMaterial object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  protected void collectValidationErrors(TissueMaterial object, TissueMaterial beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(TissueMaterial::getAlias, object, beforeChange)
        && tissueMaterialDao.getByAlias(object.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("subproject", "alias"));
    }
  }

  @Override
  protected void applyChanges(TissueMaterial to, TissueMaterial from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public SaveDao<TissueMaterial> getDao() {
    return tissueMaterialDao;
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

  @Override
  public List<TissueMaterial> listByIdList(List<Long> ids) throws IOException {
    return tissueMaterialDao.listByIdList(ids);
  }

  @Override
  public ValidationResult validateDeletion(TissueMaterial object) throws IOException {
    ValidationResult result = new ValidationResult();

    long usage = tissueMaterialDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
