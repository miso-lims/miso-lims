package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTypeDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleTypeService implements SampleTypeService {

  @Autowired
  private SampleTypeDao sampleTypeDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
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
  public long create(SampleType sampleType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(sampleType, null);
    return sampleTypeDao.create(sampleType);
  }

  @Override
  public long update(SampleType sampleType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleType managed = get(sampleType.getId());
    validateChange(sampleType, managed);
    applyChanges(managed, sampleType);
    return sampleTypeDao.update(managed);
  }

  private void validateChange(SampleType sampleType, SampleType beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(SampleType::getName, sampleType, beforeChange)) {
      if (sampleTypeDao.getByName(sampleType.getName()) != null) {
        errors.add(new ValidationError("name", "There is already a sample type with this name"));
      }
      long usage = sampleTypeDao.getUsage(beforeChange);
      if (usage > 0L) {
        errors.add(new ValidationError("name",
            "Cannot change name of sample type because it is already in use by " + usage + " " + Pluralizer.samples(usage)));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(SampleType to, SampleType from) {
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
