package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxUseService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.BoxUseDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultBoxUseService implements BoxUseService {

  @Autowired
  private BoxUseDao boxUseDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  public void setBoxUseDao(BoxUseDao boxUseDao) {
    this.boxUseDao = boxUseDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
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
  public BoxUse get(long id) throws IOException {
    return boxUseDao.get(id);
  }

  @Override
  public long create(BoxUse boxUse) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(boxUse, null);
    return boxUseDao.create(boxUse);
  }

  @Override
  public long update(BoxUse boxUse) throws IOException {
    authorizationManager.throwIfNonAdmin();
    BoxUse managed = get(boxUse.getId());
    validateChange(boxUse, managed);
    applyChanges(managed, boxUse);
    return boxUseDao.update(managed);
  }

  private void validateChange(BoxUse boxUse, BoxUse beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(BoxUse::getAlias, boxUse, beforeChange) && boxUseDao.getByAlias(boxUse.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a box use with this alias"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(BoxUse to, BoxUse from) {
    to.setAlias(from.getAlias());
  }

  @Override
  public List<BoxUse> list() throws IOException {
    return boxUseDao.list();
  }

  @Override
  public ValidationResult validateDeletion(BoxUse object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = boxUseDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.boxes(usage)));
    }
    return result;
  }

}
