package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StainCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.StainCategoryDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStainCategoryService extends AbstractSaveService<StainCategory> implements StainCategoryService {

  @Autowired
  private StainCategoryDao stainCategoryDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public SaveDao<StainCategory> getDao() {
    return stainCategoryDao;
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
  public List<StainCategory> listByIdList(List<Long> ids) throws IOException {
    return stainCategoryDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(StainCategory object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(StainCategory stainCategory, StainCategory beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(StainCategory::getName, stainCategory, beforeChange)
            && stainCategoryDao.getByName(stainCategory.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a stain category with this name"));
    }
  }

  @Override
  protected void applyChanges(StainCategory to, StainCategory from) {
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
