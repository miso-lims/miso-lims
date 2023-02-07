package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibrarySpikeInService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibrarySpikeInDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibrarySpikeInService extends AbstractSaveService<LibrarySpikeIn> implements LibrarySpikeInService {

  @Autowired
  private LibrarySpikeInDao librarySpikeInDao;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SaveDao<LibrarySpikeIn> getDao() {
    return librarySpikeInDao;
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
  public List<LibrarySpikeIn> list() throws IOException {
    return librarySpikeInDao.list();
  }

  @Override
  public List<LibrarySpikeIn> listByIdList(List<Long> ids) throws IOException {
    return librarySpikeInDao.listByIdList(ids);
  }

  @Override
  protected void collectValidationErrors(LibrarySpikeIn object, LibrarySpikeIn beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isSetAndChanged(LibrarySpikeIn::getAlias, object, beforeChange)
        && librarySpikeInDao.getByAlias(object.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a library spike-in with this alias"));
    }
  }

  @Override
  protected void applyChanges(LibrarySpikeIn to, LibrarySpikeIn from) {
    to.setAlias(from.getAlias());
  }

  @Override
  protected void authorizeUpdate(LibrarySpikeIn object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public ValidationResult validateDeletion(LibrarySpikeIn object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = librarySpikeInDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }

}
