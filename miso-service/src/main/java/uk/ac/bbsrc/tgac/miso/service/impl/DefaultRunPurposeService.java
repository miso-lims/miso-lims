package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.RunPurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultRunPurposeService extends AbstractSaveService<RunPurpose> implements RunPurposeService {

  @Autowired
  private RunPurposeDao runPurposeDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private DeletionStore deletionStore;

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
  public SaveDao<RunPurpose> getDao() {
    return runPurposeDao;
  }

  @Override
  public List<RunPurpose> list() throws IOException {
    return runPurposeDao.list();
  }

  @Override
  public List<RunPurpose> listByIdList(List<Long> ids) throws IOException {
    return runPurposeDao.listByIdList(ids);
  }

  @Override
  protected void collectValidationErrors(RunPurpose object, RunPurpose beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(RunPurpose::getAlias, object, beforeChange)
        && runPurposeDao.getByAlias(object.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a run purpose with this alias"));
    }
  }

  @Override
  protected void applyChanges(RunPurpose to, RunPurpose from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(RunPurpose object) throws IOException {
    ValidationResult result = new ValidationResult();
    long poolUsage = runPurposeDao.getUsageByPoolOrders(object);
    long seqUsage = runPurposeDao.getUsageBySequencingOrders(object);
    if (poolUsage > 0L || seqUsage > 0L) {
      result.addError(new ValidationError(String.format("Run purpose %s is used by %d pool %s and %d sequencing %s", object.getAlias(),
          poolUsage, Pluralizer.orders(poolUsage), seqUsage, Pluralizer.orders(seqUsage))));
    }
    return result;
  }

}
