package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.WorksetCategoryDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorksetCategoryService extends AbstractSaveService<WorksetCategory> implements WorksetCategoryService {

  @Autowired
  private WorksetCategoryDao worksetCategoryDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public List<WorksetCategory> list() throws IOException {
    return worksetCategoryDao.list();
  }

  @Override
  public SaveDao<WorksetCategory> getDao() {
    return worksetCategoryDao;
  }

  @Override
  protected void collectValidationErrors(WorksetCategory object, WorksetCategory beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(WorksetCategory::getAlias, object, beforeChange)) {
      if (Workset.ReservedWord.find(object.getAlias()) != null) {
        errors.add(new ValidationError("This is a reserved word", "alias"));
      } else if (worksetCategoryDao.getByAlias(object.getAlias()) != null) {
        errors.add(ValidationError.forDuplicate("workset category", "alias"));
      }
    }
  }

  @Override
  protected void applyChanges(WorksetCategory to, WorksetCategory from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(WorksetCategory object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = worksetCategoryDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.worksets(usage)));
    }
    return result;
  }

  @Override
  protected void authorizeUpdate(WorksetCategory object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<WorksetCategory> listByIdList(List<Long> ids) throws IOException {
    return worksetCategoryDao.listByIdList(ids);
  }

}
