package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryIndexFamilyDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryIndexFamilyService extends AbstractSaveService<LibraryIndexFamily>
    implements LibraryIndexFamilyService {

  @Autowired
  private LibraryIndexFamilyDao indexFamilyDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<LibraryIndexFamily> list() throws IOException {
    return indexFamilyDao.list();
  }

  @Override
  public SaveDao<LibraryIndexFamily> getDao() {
    return indexFamilyDao;
  }

  @Override
  protected void collectValidationErrors(LibraryIndexFamily object, LibraryIndexFamily beforeChange,
      List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(LibraryIndexFamily::getName, object, beforeChange)
        && indexFamilyDao.getByName(object.getName()) != null) {
      errors.add(new ValidationError("name", "There is already an index family with this name"));
    }
  }

  @Override
  protected void applyChanges(LibraryIndexFamily to, LibraryIndexFamily from) throws IOException {
    to.setName(from.getName());
    to.setUniqueDualIndex(from.isUniqueDualIndex());
    to.setArchived(from.getArchived());
  }

  @Override
  public ValidationResult validateDeletion(LibraryIndexFamily object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = indexFamilyDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }

  @Override
  protected void authorizeUpdate(LibraryIndexFamily object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public LibraryIndexFamily getByName(String name) throws IOException {
    return indexFamilyDao.getByName(name);
  }

}
