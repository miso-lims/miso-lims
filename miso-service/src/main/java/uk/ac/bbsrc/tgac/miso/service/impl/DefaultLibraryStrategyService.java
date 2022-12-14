package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryStrategyService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStrategyDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryStrategyService extends AbstractSaveService<LibraryStrategyType>
    implements LibraryStrategyService {

  @Autowired
  private LibraryStrategyDao libraryStrategyDao;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SaveDao<LibraryStrategyType> getDao() {
    return libraryStrategyDao;
  }

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

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public List<LibraryStrategyType> listByIdList(List<Long> ids) throws IOException {
    return libraryStrategyDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(LibraryStrategyType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(LibraryStrategyType object, LibraryStrategyType beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(LibraryStrategyType::getName, object, beforeChange)
        && libraryStrategyDao.getByName(object.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a library strategy type with this name"));
    }
  }

  @Override
  protected void applyChanges(LibraryStrategyType to, LibraryStrategyType from) {
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
    long libraryUsage = libraryStrategyDao.getUsageByLibraries(object);
    if (libraryUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, libraryUsage, Pluralizer.libraries(libraryUsage)));
    }
    long designUsage = libraryStrategyDao.getUsageByLibraryDesigns(object);
    if (designUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, designUsage, Pluralizer.libraryDesigns(designUsage)));
    }
    return result;
  }

}
