package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.ArrayModelDao;
import uk.ac.bbsrc.tgac.miso.service.ArrayModelService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultArrayModelService implements ArrayModelService {

  @Autowired
  private ArrayModelDao arrayModelDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

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
  public ArrayModel get(long id) throws IOException {
    return arrayModelDao.get(id);
  }

  @Override
  public long create(ArrayModel model) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(model, null);
    return arrayModelDao.create(model);
  }

  @Override
  public long update(ArrayModel model) throws IOException {
    authorizationManager.throwIfNonAdmin();
    ArrayModel managed = get(model.getId());
    validateChange(model, managed);
    applyChanges(managed, model);
    return arrayModelDao.update(managed);
  }

  private void validateChange(ArrayModel model, ArrayModel beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();
    
    if (ValidationUtils.isSetAndChanged(ArrayModel::getAlias, model, beforeChange)
        && arrayModelDao.getByAlias(model.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already an array model with this alias"));
    }

    if (beforeChange != null) {
      long usage = arrayModelDao.getUsage(beforeChange);
      if (usage > 0L) {
        if (ValidationUtils.isSetAndChanged(ArrayModel::getRows, model, beforeChange)) {
          errors.add(new ValidationError("rows",
              "Cannot resize because array model is already used by " + usage + " array" + (usage > 1L ? "s" : "")));
        }
        if (ValidationUtils.isSetAndChanged(ArrayModel::getColumns, model, beforeChange)) {
          errors.add(new ValidationError("columns",
              "Cannot resize because array model is already used by " + usage + " array" + (usage > 1L ? "s" : "")));
        }
      }
    }
    
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(ArrayModel to, ArrayModel from) {
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
      result.addError(new ValidationError(
          "Array model '" + object.getAlias() + "' is used by " + usage + " array" + (usage > 1L ? "s" : "")));
    }
    return result;
  }

}
