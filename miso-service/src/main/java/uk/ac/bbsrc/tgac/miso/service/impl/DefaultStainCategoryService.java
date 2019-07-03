package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StainCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.StainCategoryDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStainCategoryService implements StainCategoryService {

  @Autowired
  private StainCategoryDao stainCategoryDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public StainCategory get(long id) throws IOException {
    return stainCategoryDao.get(id);
  }

  @Override
  public long create(StainCategory stainCategory) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(stainCategory, null);
    return stainCategoryDao.create(stainCategory);
  }

  @Override
  public long update(StainCategory stainCategory) throws IOException {
    authorizationManager.throwIfNonAdmin();
    StainCategory managed = get(stainCategory.getId());
    validateChange(stainCategory, managed);
    applyChanges(managed, stainCategory);
    return stainCategoryDao.update(managed);
  }

  private void validateChange(StainCategory stainCategory, StainCategory beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(StainCategory::getName, stainCategory, beforeChange)
        && stainCategoryDao.getByName(stainCategory.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a stain category with this name"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(StainCategory to, StainCategory from) {
    to.setName(from.getName());
  }

  @Override
  public List<StainCategory> list() throws IOException {
    return stainCategoryDao.list();
  }

  @Override
  public ValidationResult validateDeletion(StainCategory object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = stainCategoryDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.stains(usage)));
    }
    return result;
  }

}
