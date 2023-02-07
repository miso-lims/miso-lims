package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ArrayModelService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayModelDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultArrayModelService extends AbstractSaveService<ArrayModel> implements ArrayModelService {

  @Autowired
  private ArrayModelDao arrayModelDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public SaveDao<ArrayModel> getDao() {
    return arrayModelDao;
  }

  public void setArrayModelDao(ArrayModelDao arrayModelDao) {
    this.arrayModelDao = arrayModelDao;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<ArrayModel> listByIdList(List<Long> ids) throws IOException {
    return arrayModelDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(ArrayModel object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(ArrayModel model, ArrayModel beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(ArrayModel::getAlias, model, beforeChange)
        && arrayModelDao.getByAlias(model.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already an array model with this alias"));
    }

    if (beforeChange != null) {
      long usage = arrayModelDao.getUsage(beforeChange);
      if (usage > 0L) {
        if (ValidationUtils.isSetAndChanged(ArrayModel::getRows, model, beforeChange)) {
          errors.add(new ValidationError("rows",
              "Cannot resize because array model is already used by " + usage + " " + Pluralizer.arrays(usage)));
        }
        if (ValidationUtils.isSetAndChanged(ArrayModel::getColumns, model, beforeChange)) {
          errors.add(new ValidationError("columns",
              "Cannot resize because array model is already used by " + usage + " " + Pluralizer.arrays(usage)));
        }
      }
    }
  }

  @Override
  protected void applyChanges(ArrayModel to, ArrayModel from) {
    to.setAlias(from.getAlias());
    to.setRows(from.getRows());
    to.setColumns(from.getColumns());
  }

  @Override
  public List<ArrayModel> list() throws IOException {
    return arrayModelDao.list();
  }

  @Override
  public ValidationResult validateDeletion(ArrayModel object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = arrayModelDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.arrays(usage)));
    }
    return result;
  }

}
