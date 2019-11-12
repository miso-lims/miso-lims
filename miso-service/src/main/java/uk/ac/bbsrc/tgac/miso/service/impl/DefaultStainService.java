package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StainCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.StainService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.StainDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStainService implements StainService {

  @Autowired
  private StainDao stainDao;

  @Autowired
  private StainCategoryService stainCategoryService;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Stain get(long id) throws IOException {
    return stainDao.get(id);
  }

  @Override
  public List<Stain> list() throws IOException {
    return stainDao.list();
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
  public long create(Stain stain) throws IOException {
    authorizationManager.throwIfNonAdmin();
    loadChildEntities(stain);
    validateChange(stain, null);
    return stainDao.create(stain);
  }

  @Override
  public long update(Stain stain) throws IOException {
    authorizationManager.throwIfNonAdmin();
    loadChildEntities(stain);
    Stain managed = get(stain.getId());
    validateChange(stain, managed);
    applyChanges(managed, stain);
    return stainDao.update(managed);
  }

  private void loadChildEntities(Stain stain) throws IOException {
    ValidationUtils.loadChildEntity(stain::setCategory, stain.getCategory(), stainCategoryService, "categoryId");
  }

  private void validateChange(Stain stain, Stain beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(Stain::getName, stain, beforeChange) && stainDao.getByName(stain.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a stain with this name"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(Stain to, Stain from) {
    to.setName(from.getName());
    to.setCategory(from.getCategory());
  }

  @Override
  public ValidationResult validateDeletion(Stain object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = stainDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }
    return result;
  }

}
