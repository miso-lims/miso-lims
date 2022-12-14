package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibrarySelectionDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibrarySelectionService extends AbstractSaveService<LibrarySelectionType>
    implements LibrarySelectionService {

  @Autowired
  private LibrarySelectionDao librarySelectionDao;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SaveDao<LibrarySelectionType> getDao() {
    return librarySelectionDao;
  }

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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<LibrarySelectionType> listByIdList(List<Long> ids) throws IOException {
    return librarySelectionDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(LibrarySelectionType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(LibrarySelectionType object, LibrarySelectionType beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(LibrarySelectionType::getName, object, beforeChange)
        && librarySelectionDao.getByName(object.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a library selection type with this name"));
    }
  }

  @Override
  protected void applyChanges(LibrarySelectionType to, LibrarySelectionType from) {
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
    long libraryUsage = librarySelectionDao.getUsageByLibraries(object);
    if (libraryUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, libraryUsage, Pluralizer.libraries(libraryUsage)));
    }
    long designUsage = librarySelectionDao.getUsageByLibraryDesigns(object);
    if (designUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, designUsage, Pluralizer.libraryDesigns(designUsage)));
    }
    return result;
  }

}
