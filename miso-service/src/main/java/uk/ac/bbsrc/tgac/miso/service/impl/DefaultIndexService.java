package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.IndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.IndexStore;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultIndexService extends AbstractSaveService<Index> implements IndexService {

  @Autowired
  private IndexStore indexStore;
  @Autowired
  private IndexFamilyService indexFamilyService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return indexStore.count(errorHandler, filter);
  }

  @Override
  public Index get(long id) throws IOException {
    return indexStore.get(id);
  }

  @Override
  public List<Index> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol, PaginationFilter... filter)
      throws IOException {
    return indexStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  public void setIndexStore(IndexStore indexStore) {
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
  public SaveDao<Index> getDao() {
    return indexStore;
  }

  @Override
  protected void loadChildEntities(Index object) throws IOException {
    loadChildEntity(object.getFamily(), object::setFamily, indexFamilyService);
  }

  @Override
  protected void collectValidationErrors(Index object, Index beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(Index::getName, object, beforeChange)
        && indexStore.getByFamilyPositionAndName(object.getFamily(), object.getPosition(), object.getName()) != null) {
      errors.add(new ValidationError("name", "This index family already contains an index with this name and position"));
    }
    if (ValidationUtils.isSetAndChanged(Index::getPosition, object, beforeChange)
        && indexStore.getByFamilyPositionAndName(object.getFamily(), object.getPosition(), object.getName()) != null) {
      errors.add(new ValidationError("position", "This index family already contains an index with this name and position"));
    }
    if (beforeChange != null && object.getPosition() != beforeChange.getPosition()) {
      long usage = indexStore.getUsage(beforeChange);
      if (usage > 0L) {
        errors.add(new ValidationError("position",
            String.format("Cannot change because the index is already used by %d %s", usage, Pluralizer.libraries(usage))));
      }
    }
  }

  @Override
  protected void applyChanges(Index to, Index from) throws IOException {
    to.setName(from.getName());
    to.setSequence(from.getSequence());
    to.setPosition(from.getPosition());
    to.getRealSequences().removeIf(sequence -> !from.getRealSequences().contains(sequence));
    for (String sequence : from.getRealSequences()) {
      if (!to.getRealSequences().contains(sequence)) {
        to.getRealSequences().add(sequence);
      }
    }
  }

  @Override
  public ValidationResult validateDeletion(Index object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = indexStore.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }

  @Override
  protected void authorizeSave(Index object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

}
