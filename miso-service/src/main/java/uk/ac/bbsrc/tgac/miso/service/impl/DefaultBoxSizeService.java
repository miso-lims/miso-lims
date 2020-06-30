package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxSizeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.BoxSizeDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultBoxSizeService implements BoxSizeService {

  @Autowired
  private BoxSizeDao boxSizeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  public void setBoxSizeDao(BoxSizeDao boxSizeDao) {
    this.boxSizeDao = boxSizeDao;
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
  public BoxSize get(long id) throws IOException {
    return boxSizeDao.get(id);
  }

  @Override
  public long create(BoxSize boxSize) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(boxSize, null);
    return boxSizeDao.create(boxSize);
  }

  @Override
  public long update(BoxSize boxSize) throws IOException {
    authorizationManager.throwIfNonAdmin();
    BoxSize managed = get(boxSize.getId());
    validateChange(boxSize, managed);
    applyChanges(managed, boxSize);
    return boxSizeDao.update(managed);
  }

  private void validateChange(BoxSize boxSize, BoxSize beforeChange) {
    List<ValidationError> errors = new ArrayList<>();
    
    long usage = 0L;
    if (beforeChange != null) {
      usage = boxSizeDao.getUsage(beforeChange);
    }
    if (usage > 0L) {
      if (ValidationUtils.isSetAndChanged(BoxSize::getRows, boxSize, beforeChange)) {
        errors.add(new ValidationError("rows",
            "Cannot resize because box size is already used by " + usage + " " + Pluralizer.boxes(usage)));
      }
      if (ValidationUtils.isSetAndChanged(BoxSize::getColumns, boxSize, beforeChange)) {
        errors.add(new ValidationError("columns",
            "Cannot resize because box size is already used by " + usage + " " + Pluralizer.boxes(usage)));
      }
    }
    
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(BoxSize to, BoxSize from) {
    to.setRows(from.getRows());
    to.setColumns(from.getColumns());
    to.setScannable(from.getScannable());
    to.setBoxType(from.getBoxType());
  }

  @Override
  public List<BoxSize> list() throws IOException {
    return boxSizeDao.list();
  }

  @Override
  public ValidationResult validateDeletion(BoxSize object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = boxSizeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.boxes(usage)));
    }
    return result;
  }

}
