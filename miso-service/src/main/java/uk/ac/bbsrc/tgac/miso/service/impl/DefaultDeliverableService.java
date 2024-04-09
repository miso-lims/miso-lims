package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.DeliverableService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.DeliverableDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultDeliverableService extends AbstractSaveService<Deliverable> implements DeliverableService {

  @Autowired
  private DeliverableDao deliverableDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public SaveDao<Deliverable> getDao() {
    return deliverableDao;
  }

  @Override
  public List<Deliverable> list() throws IOException {
    return deliverableDao.list();
  }

  @Override
  public List<Deliverable> listByIdList(List<Long> ids) throws IOException {
    return deliverableDao.listByIdList(ids);
  }

  @Override
  protected void applyChanges(Deliverable to, Deliverable from) throws IOException {
    to.setName(from.getName());
    to.setAnalysisReviewRequired(from.isAnalysisReviewRequired());
  }

  @Override
  protected void collectValidationErrors(Deliverable object, Deliverable beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(Deliverable::getName, object, beforeChange)
        && deliverableDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate("deliverable", "name"));
    }
  }

  @Override
  protected void authorizeUpdate(Deliverable object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public ValidationResult validateDeletion(Deliverable object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = deliverableDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.projects(usage)));
    }
    return result;
  }
}
