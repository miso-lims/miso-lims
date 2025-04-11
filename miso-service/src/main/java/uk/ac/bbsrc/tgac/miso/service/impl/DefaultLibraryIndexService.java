package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryIndexService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryIndexDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryIndexService extends AbstractSaveService<LibraryIndex> implements LibraryIndexService {

  @Autowired
  private LibraryIndexDao indexStore;
  @Autowired
  private LibraryIndexFamilyService indexFamilyService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  public void setIndexStore(LibraryIndexDao indexStore) {
    this.indexStore = indexStore;
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
  public SaveDao<LibraryIndex> getDao() {
    return indexStore;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return indexStore.count(errorHandler, filter);
  }

  @Override
  public LibraryIndex get(long id) throws IOException {
    return indexStore.get(id);
  }

  @Override
  public List<LibraryIndex> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return indexStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<LibraryIndex> listByIdList(List<Long> ids) throws IOException {
    return indexStore.listByIdList(ids);
  }

  @Override
  protected void loadChildEntities(LibraryIndex object) throws IOException {
    loadChildEntity(object.getFamily(), object::setFamily, indexFamilyService);
  }

  @Override
  protected void collectValidationErrors(LibraryIndex object, LibraryIndex beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isSetAndChanged(LibraryIndex::getName, object, beforeChange)
        && indexStore.getByFamilyPositionAndName(object.getFamily(), object.getPosition(), object.getName()) != null) {
      errors
          .add(new ValidationError("name", "This index family already contains an index with this name and position"));
    }
    if (ValidationUtils.isSetAndChanged(LibraryIndex::getPosition, object, beforeChange)
        && indexStore.getByFamilyPositionAndName(object.getFamily(), object.getPosition(), object.getName()) != null) {
      errors.add(
          new ValidationError("position", "This index family already contains an index with this name and position"));
    }
    if (beforeChange != null && object.getPosition() != beforeChange.getPosition()) {
      long usage = indexStore.getUsage(beforeChange);
      if (usage > 0L) {
        errors.add(new ValidationError("position",
            String.format("Cannot change because the index is already used by %d %s", usage,
                Pluralizer.libraries(usage))));
      }
    }
  }

  @Override
  protected void applyChanges(LibraryIndex to, LibraryIndex from) throws IOException {
    to.setName(from.getName());
    to.setSequence(from.getSequence());
    to.setPosition(from.getPosition());
    to.setRealSequences(from.getRealSequences());
  }

  @Override
  public ValidationResult validateDeletion(LibraryIndex object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = indexStore.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }

  @Override
  protected void authorizeUpdate(LibraryIndex object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

}
