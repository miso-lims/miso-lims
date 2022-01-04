package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTypeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleTypeService extends AbstractSaveService<SampleType> implements SampleTypeService {

  @Autowired
  private SampleTypeDao sampleTypeDao;
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
  public List<SampleType> listByIdList(List<Long> ids) throws IOException {
    return sampleTypeDao.listByIdList(ids);
  }

  @Override
  public SaveDao<SampleType> getDao() {
    return sampleTypeDao;
  }

  @Override
  public SampleType get(long id) throws IOException {
    return sampleTypeDao.get(id);
  }

  @Override
  public SampleType getByName(String name) throws IOException {
    return sampleTypeDao.getByName(name);
  }

  @Override
  protected void authorizeUpdate(SampleType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(SampleType object, SampleType beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(SampleType::getName, object, beforeChange)) {
      if (sampleTypeDao.getByName(object.getName()) != null) {
        errors.add(new ValidationError("name", "There is already a sample type with this name"));
      }
      long usage = sampleTypeDao.getUsage(beforeChange);
      if (usage > 0L) {
        errors.add(new ValidationError("name",
                "Cannot change name of sample type because it is already in use by " + usage + " " + Pluralizer.samples(usage)));
      }
    }
  }

  @Override
  protected void applyChanges(SampleType to, SampleType from) {
    to.setName(from.getName());
    to.setArchived(from.isArchived());
  }

  @Override
  public List<SampleType> list() throws IOException {
    return sampleTypeDao.list();
  }

  @Override
  public ValidationResult validateDeletion(SampleType object) throws IOException {
    ValidationResult result = new ValidationResult();

    long usage = sampleTypeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
