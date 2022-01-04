package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSamplePurposeService extends AbstractSaveService<SamplePurpose> implements SamplePurposeService {

  @Autowired
  private SamplePurposeDao samplePurposeDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public SaveDao<SamplePurpose> getDao() {
    return samplePurposeDao;
  }

  @Override
  public SamplePurpose get(long samplePurposeId) throws IOException {
    return samplePurposeDao.get(samplePurposeId);
  }

  @Override
  protected void authorizeUpdate(SamplePurpose object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(SamplePurpose object, SamplePurpose beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isChanged(SamplePurpose::getAlias, object, beforeChange)) {
      if (samplePurposeDao.getByAlias(object.getAlias()) != null) {
        errors.add(ValidationError.forDuplicate("Sample Purpose", "alias"));
      }
    }
  }

  @Override
  protected void applyChanges(SamplePurpose to, SamplePurpose from) throws IOException {
    to.setAlias(from.getAlias());
    to.setArchived(from.isArchived());
  }

  @Override
  protected void beforeSave(SamplePurpose object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public List<SamplePurpose> list() throws IOException {
    return samplePurposeDao.list();
  }

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
  public List<SamplePurpose> listByIdList(List<Long> ids) throws IOException {
    return samplePurposeDao.listByIdList(ids);
  }

  @Override
  public ValidationResult validateDeletion(SamplePurpose object) throws IOException {
    ValidationResult result = new ValidationResult();

    long usage = samplePurposeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
