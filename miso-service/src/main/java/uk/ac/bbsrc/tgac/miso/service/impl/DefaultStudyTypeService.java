package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StudyTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.StudyTypeDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStudyTypeService extends AbstractSaveService<StudyType> implements StudyTypeService {

  @Autowired
  private StudyTypeDao studyTypeDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
  }

  @Override
  public SaveDao<StudyType> getDao() {
    return studyTypeDao;
  }

  @Override
  protected void authorizeUpdate(StudyType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(StudyType studyType, StudyType beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(StudyType::getName, studyType, beforeChange)
        && studyTypeDao.getByName(studyType.getName()) != null) {
      errors.add(new ValidationError("name", "There is already a study type with this name"));
    }
  }

  @Override
  protected void applyChanges(StudyType to, StudyType from) {
    to.setName(from.getName());
  }

  @Override
  public List<StudyType> list() throws IOException {
    return studyTypeDao.list();
  }

  @Override
  public List<StudyType> listByIdList(List<Long> ids) throws IOException {
    return studyTypeDao.listByIdList(ids);
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
