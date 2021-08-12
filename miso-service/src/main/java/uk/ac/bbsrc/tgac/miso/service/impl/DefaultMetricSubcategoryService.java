package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.MetricSubcategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.core.service.MetricSubcategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.MetricSubcategoryDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultMetricSubcategoryService extends AbstractSaveService<MetricSubcategory> implements MetricSubcategoryService {

  @Autowired
  private MetricSubcategoryDao metricSubcategoryDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;
  @Autowired
  private LibraryDesignCodeService libraryDesignCodeService;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<MetricSubcategory> listByIdList(List<Long> ids) throws IOException {
    return metricSubcategoryDao.listByIdList(ids);
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<MetricSubcategory> list() throws IOException {
    return metricSubcategoryDao.list();
  }

  @Override
  public SaveDao<MetricSubcategory> getDao() {
    return metricSubcategoryDao;
  }

  @Override
  protected void authorizeUpdate(MetricSubcategory object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void loadChildEntities(MetricSubcategory object) throws IOException {
    loadChildEntity(object.getLibraryDesignCode(), object::setLibraryDesignCode, libraryDesignCodeService);
  }

  @Override
  protected void collectValidationErrors(MetricSubcategory object, MetricSubcategory beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(MetricSubcategory::getAlias, object, beforeChange)
        && metricSubcategoryDao.getByAliasAndCategory(object.getAlias(), object.getCategory()) != null) {
      errors.add(new ValidationError("alias",
          String.format("There is already another %s subcategory with this alias", object.getCategory().getLabel())));
    }
  }

  @Override
  protected void applyChanges(MetricSubcategory to, MetricSubcategory from) throws IOException {
    to.setAlias(from.getAlias());
    to.setLibraryDesignCode(from.getLibraryDesignCode());
    to.setSortPriority(from.getSortPriority());
  }

  @Override
  public ValidationResult validateDeletion(MetricSubcategory object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = metricSubcategoryDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.metrics(usage)));
    }
    return result;
  }

}
