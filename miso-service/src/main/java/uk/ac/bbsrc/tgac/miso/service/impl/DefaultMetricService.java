package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Metric;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.MetricService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.MetricDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultMetricService extends AbstractSaveService<Metric> implements MetricService {

  @Autowired
  private MetricDao metricDao;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<Metric> listByIdList(List<Long> ids) throws IOException {
    return metricDao.listByIdList(ids);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<Metric> list() throws IOException {
    return metricDao.list();
  }

  @Override
  public SaveDao<Metric> getDao() {
    return metricDao;
  }

  @Override
  protected void authorizeUpdate(Metric object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(Metric object, Metric beforeChange, List<ValidationError> errors) throws IOException {
    if ((ValidationUtils.isChanged(Metric::getAlias, object, beforeChange)
        || ValidationUtils.isChanged(Metric::getCategory, object, beforeChange))
        && metricDao.getByAliasAndCategory(object.getAlias(), object.getCategory()) != null) {
      errors.add(ValidationError.forDuplicate("metric", "alias"));
    }
  }

  @Override
  protected void applyChanges(Metric to, Metric from) throws IOException {
    to.setAlias(from.getAlias());
    to.setCategory(from.getCategory());
    to.setThresholdType(from.getThresholdType());
    to.setUnits(from.getUnits());
  }

  @Override
  public ValidationResult validateDeletion(Metric object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = metricDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.assays(usage)));
    }
    return result;
  }

}
