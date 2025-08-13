package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.PipelineService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.PipelineDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultPipelineService extends AbstractSaveService<Pipeline> implements PipelineService {

  @Autowired
  private PipelineDao pipelineDao;
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<Pipeline> listByIdList(List<Long> ids) throws IOException {
    return pipelineDao.listByIdList(ids);
  }

  @Override
  public List<Pipeline> list() throws IOException {
    return pipelineDao.list();
  }

  @Override
  public SaveDao<Pipeline> getDao() {
    return pipelineDao;
  }

  @Override
  protected void collectValidationErrors(Pipeline object, Pipeline beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(Pipeline::getAlias, object, beforeChange)
        && pipelineDao.getByAlias(object.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("pipeline", "alias"));
    }
  }

  @Override
  protected void authorizeUpdate(Pipeline object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void applyChanges(Pipeline to, Pipeline from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(Pipeline object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = pipelineDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.projects(usage)));
    }
    return result;
  }

}
