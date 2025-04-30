package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleIndexFamilyService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SampleIndexFamilyDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleIndexFamilyService extends AbstractSaveService<SampleIndexFamily>
    implements SampleIndexFamilyService {

  @Autowired
  private SampleIndexFamilyDao sampleIndexFamilyDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public List<SampleIndexFamily> list() throws IOException {
    return sampleIndexFamilyDao.list();
  }

  @Override
  public SaveDao<SampleIndexFamily> getDao() {
    return sampleIndexFamilyDao;
  }

  @Override
  protected void authorizeUpdate(SampleIndexFamily object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(SampleIndexFamily object, SampleIndexFamily beforeChange,
      List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isChanged(SampleIndexFamily::getName, object, beforeChange)
        && sampleIndexFamilyDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate("sample index family", "name"));
    }
  }

  @Override
  protected void applyChanges(SampleIndexFamily to, SampleIndexFamily from) throws IOException {
    to.setName(from.getName());
  }

  @Override
  public ValidationResult validateDeletion(SampleIndexFamily object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = sampleIndexFamilyDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }
    return result;
  }

}
