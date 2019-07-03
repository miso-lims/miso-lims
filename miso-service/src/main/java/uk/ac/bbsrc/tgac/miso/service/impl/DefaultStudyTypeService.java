package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StudyTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.StudyTypeDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStudyTypeService implements StudyTypeService {

  @Autowired
  private StudyTypeDao studyTypeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  public void setStudyTypeDao(StudyTypeDao studyTypeDao) {
    this.studyTypeDao = studyTypeDao;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
  }

  @Override
  public StudyType get(long id) throws IOException {
    return studyTypeDao.get(id);
  }

  @Override
  public long create(StudyType studyType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(studyType, null);
    return studyTypeDao.create(studyType);
  }

  @Override
  public long update(StudyType studyType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    StudyType managed = get(studyType.getId());
    validateChange(studyType, managed);
    applyChanges(managed, studyType);
    return studyTypeDao.update(managed);
  }

  private void validateChange(StudyType studyType, StudyType beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(StudyType::getName, studyType, beforeChange)
        && studyTypeDao.getByName(studyType.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a study type with this name"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(StudyType to, StudyType from) {
    to.setName(from.getName());
  }

  @Override
  public List<StudyType> list() throws IOException {
    return studyTypeDao.list();
  }

  @Override
  public ValidationResult validateDeletion(StudyType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = studyTypeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.studies(usage)));
    }
    return result;
  }

}
