package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryStrategyService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStrategyDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryStrategyService implements LibraryStrategyService {

  @Autowired
  private LibraryStrategyDao libraryStrategyDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setLibraryStrategyDao(LibraryStrategyDao libraryStrategyDao) {
    this.libraryStrategyDao = libraryStrategyDao;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  public void setDeletionStore(DeletionStore deleteionStore) {
    this.deletionStore = deleteionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public LibraryStrategyType get(long id) throws IOException {
    return libraryStrategyDao.get(id);
  }

  @Override
  public long create(LibraryStrategyType type) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(type, null);
    return libraryStrategyDao.create(type);
  }

  @Override
  public long update(LibraryStrategyType type) throws IOException {
    authorizationManager.throwIfNonAdmin();
    LibraryStrategyType managed = get(type.getId());
    validateChange(type, managed);
    applyChanges(managed, type);
    return libraryStrategyDao.update(managed);
  }

  private void validateChange(LibraryStrategyType type, LibraryStrategyType beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(LibraryStrategyType::getName, type, beforeChange)
        && libraryStrategyDao.getByName(type.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a library strategy type with this name"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(LibraryStrategyType to, LibraryStrategyType from) {
    to.setName(from.getName());
    to.setDescription(from.getDescription());
  }

  @Override
  public List<LibraryStrategyType> list() throws IOException {
    return libraryStrategyDao.list();
  }

  @Override
  public ValidationResult validateDeletion(LibraryStrategyType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = libraryStrategyDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }

}
