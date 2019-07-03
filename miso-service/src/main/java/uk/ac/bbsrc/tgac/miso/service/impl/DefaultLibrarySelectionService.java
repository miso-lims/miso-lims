package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibrarySelectionDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibrarySelectionService implements LibrarySelectionService {

  @Autowired
  private LibrarySelectionDao librarySelectionDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setLibrarySelectionDao(LibrarySelectionDao librarySelectionDao) {
    this.librarySelectionDao = librarySelectionDao;
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
  public LibrarySelectionType get(long id) throws IOException {
    return librarySelectionDao.get(id);
  }

  @Override
  public long create(LibrarySelectionType type) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(type, null);
    return librarySelectionDao.create(type);
  }

  @Override
  public long update(LibrarySelectionType type) throws IOException {
    authorizationManager.throwIfNonAdmin();
    LibrarySelectionType managed = get(type.getId());
    validateChange(type, managed);
    applyChanges(managed, type);
    return librarySelectionDao.update(managed);
  }

  private void validateChange(LibrarySelectionType type, LibrarySelectionType beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(LibrarySelectionType::getName, type, beforeChange)
        && librarySelectionDao.getByName(type.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a library selection type with this name"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(LibrarySelectionType to, LibrarySelectionType from) {
    to.setName(from.getName());
    to.setDescription(from.getDescription());
  }

  @Override
  public List<LibrarySelectionType> list() throws IOException {
    return librarySelectionDao.list();
  }

  @Override
  public ValidationResult validateDeletion(LibrarySelectionType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = librarySelectionDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }

}
