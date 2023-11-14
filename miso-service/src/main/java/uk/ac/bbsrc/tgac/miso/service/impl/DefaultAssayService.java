package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayMetric;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AssayService;
import uk.ac.bbsrc.tgac.miso.core.service.AssayTestService;
import uk.ac.bbsrc.tgac.miso.core.service.MetricService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
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
  private AssayTestService assayTestService;
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
    Set<AssayTest> loadedTests = new HashSet<>();
    for (AssayTest test : object.getAssayTests()) {
      AssayTest loadedTest = assayTestService.get(test.getId());
      if (loadedTest == null) {
        throw new ValidationException(new ValidationError("tests", "Invalid test ID: %d".formatted(test.getId())));
      }
      loadedTests.add(loadedTest);
    }
    object.getAssayTests().clear();
    object.getAssayTests().addAll(loadedTests);
  }

  @Override
  protected void collectValidationErrors(Assay object, Assay beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(Assay::getAlias, object, beforeChange)
        && assayDao.getByAliasAndVersion(object.getAlias(), object.getVersion()) != null) {
      errors.add(ValidationError.forDuplicate("assay", "alias", "alias and version"));
    }
    long distinctTests = object.getAssayTests().stream().mapToLong(AssayTest::getId).distinct().count();
    if (object.getAssayTests().size() != distinctTests) {
      errors.add(new ValidationError("tests", "Duplicate tests not allowed"));
    }
    long distinctMetrics = object.getAssayMetrics().stream().mapToLong(x -> x.getMetric().getId()).distinct().count();
    if (object.getAssayMetrics().size() != distinctMetrics) {
      errors.add(new ValidationError("metrics", "Duplicate metrics not allowed"));
    }
  }

  @Override
  protected void applyChanges(Assay to, Assay from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setArchived(from.isArchived());
    ValidationUtils.applySetChanges(to.getAssayTests(), from.getAssayTests());
    applyMetricChanges(to.getAssayMetrics(), from.getAssayMetrics());
    to.setCaseTargetDays(from.getCaseTargetDays());
    to.setReceiptTargetDays(from.getReceiptTargetDays());
    to.setExtractionTargetDays(from.getExtractionTargetDays());
    to.setLibraryPreparationTargetDays(from.getLibraryPreparationTargetDays());
    to.setLibraryQualificationTargetDays(from.getLibraryQualificationTargetDays());
    to.setFullDepthSequencingTargetDays(from.getFullDepthSequencingTargetDays());
    to.setAnalysisReviewTargetDays(from.getAnalysisReviewTargetDays());
    to.setReleaseApprovalTargetDays(from.getReleaseApprovalTargetDays());
    to.setReleaseTargetDays(from.getReleaseTargetDays());
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
