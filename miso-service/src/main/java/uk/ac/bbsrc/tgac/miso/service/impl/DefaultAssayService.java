package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AssayService;
import uk.ac.bbsrc.tgac.miso.core.service.MetricService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.AssayDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultAssayService extends AbstractSaveService<Assay> implements AssayService {

  @Autowired
  private AssayDao assayDao;
  @Autowired
  private MetricService metricService;
  @Autowired
  private AuthorizationManager authorizationManager;
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
  public List<Assay> list() throws IOException {
    return assayDao.list();
  }

  @Override
  public SaveDao<Assay> getDao() {
    return assayDao;
  }

  @Override
  protected void authorizeUpdate(Assay object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(Assay object) throws IOException {
    for (AssayMetric metric : object.getAssayMetrics()) {
      loadChildEntity(metric.getMetric(), metric::setMetric, metricService);
      metric.setAssay(object);
    }
  }

  @Override
  protected void collectValidationErrors(Assay object, Assay beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isChanged(Assay::getAlias, object, beforeChange)
        && assayDao.getByAliasAndVersion(object.getAlias(), object.getVersion()) != null) {
      errors.add(ValidationError.forDuplicate("assay", "alias", "alias and version"));
    }
  }

  @Override
  protected void applyChanges(Assay to, Assay from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setArchived(from.isArchived());
    applyMetricChanges(to.getAssayMetrics(), from.getAssayMetrics());
  }

  private void applyMetricChanges(Set<AssayMetric> to, Set<AssayMetric> from) throws IOException {
    for (Iterator<AssayMetric> iterator = to.iterator(); iterator.hasNext();) {
      AssayMetric toItem = iterator.next();
      if (from.stream().noneMatch(fromItem -> fromItem.getMetric().getId() == toItem.getMetric().getId())) {
        iterator.remove();
        assayDao.deleteAssayMetric(toItem);
      }
    }
    for (AssayMetric fromItem : from) {
      AssayMetric toItem = to.stream()
          .filter(x -> x.getMetric().getId() == fromItem.getMetric().getId())
          .findFirst().orElse(null);
      if (toItem == null) {
        to.add(fromItem);
      } else {
        toItem.setMaximumThreshold(fromItem.getMaximumThreshold());
        toItem.setMinimumThreshold(fromItem.getMinimumThreshold());
      }
    }
  }

  @Override
  public ValidationResult validateDeletion(Assay object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = assayDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.requisitions(usage)));
    }
    return result;
  }

}
