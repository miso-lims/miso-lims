package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetStage;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetStageService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.WorksetStageDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorksetStageService extends AbstractSaveService<WorksetStage> implements WorksetStageService {

  @Autowired
  private WorksetStageDao worksetStageDao;
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
  public List<WorksetStage> list() throws IOException {
    return worksetStageDao.list();
  }

  @Override
  public SaveDao<WorksetStage> getDao() {
    return worksetStageDao;
  }

  @Override
  protected void collectValidationErrors(WorksetStage object, WorksetStage beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(WorksetStage::getAlias, object, beforeChange)
        && worksetStageDao.getByAlias(object.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("workset stage", "alias"));
    }
  }

  @Override
  protected void applyChanges(WorksetStage to, WorksetStage from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(WorksetStage object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = worksetStageDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.worksets(usage)));
    }
    return result;
  }

  @Override
  protected void authorizeUpdate(WorksetStage object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<WorksetStage> listByIdList(List<Long> ids) throws IOException {
    return worksetStageDao.listByIdList(ids);
  }

}
