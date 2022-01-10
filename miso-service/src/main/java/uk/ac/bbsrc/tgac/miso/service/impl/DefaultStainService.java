package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StainCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.StainService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.StainDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStainService extends AbstractSaveService<Stain> implements StainService {

  @Autowired
  private StainDao stainDao;
  @Autowired
  private StainCategoryService stainCategoryService;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public SaveDao<Stain> getDao() {
    return stainDao;
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<Stain> listByIdList(List<Long> ids) throws IOException {
    return stainDao.listByIdList(ids);
  }

  @Override
  protected void loadChildEntities(Stain stain) throws IOException {
    ValidationUtils.loadChildEntity(stain::setCategory, stain.getCategory(), stainCategoryService, "categoryId");
  }

  @Override
  protected void collectValidationErrors(Stain object, Stain beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(Stain::getName, object, beforeChange) && stainDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate("stain", "name"));
    }
  }

  @Override
  protected void authorizeUpdate(Stain object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void applyChanges(Stain to, Stain from) {
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
