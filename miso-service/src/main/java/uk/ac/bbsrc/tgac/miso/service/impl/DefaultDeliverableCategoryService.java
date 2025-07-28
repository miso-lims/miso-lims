package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.DeliverableCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.DeliverableCategoryDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultDeliverableCategoryService extends AbstractSaveService<DeliverableCategory>
    implements DeliverableCategoryService {

  @Autowired
  private DeliverableCategoryDao deliverableCategoryDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<DeliverableCategory> listByIdList(List<Long> ids) throws IOException {
    return deliverableCategoryDao.listByIdList(ids);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<DeliverableCategory> list() throws IOException {
    return deliverableCategoryDao.list();
  }

  @Override
  public SaveDao<DeliverableCategory> getDao() {
    return deliverableCategoryDao;
  }

  @Override
  protected void collectValidationErrors(DeliverableCategory object, DeliverableCategory beforeChange,
      List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isChanged(DeliverableCategory::getName, object, beforeChange)
        && deliverableCategoryDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate("deliverable category", "name"));
    }
  }

  @Override
  protected void applyChanges(DeliverableCategory to, DeliverableCategory from) throws IOException {
    to.setName(from.getName());
  }

  @Override
  protected void authorizeCreate(DeliverableCategory object) throws IOException {
    authorizationManager.throwIfNonAdmin();;
  }

  @Override
  protected void authorizeUpdate(DeliverableCategory object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public ValidationResult validateDeletion(DeliverableCategory object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = deliverableCategoryDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.deliverables(usage)));
    }
    return result;
  }

}
