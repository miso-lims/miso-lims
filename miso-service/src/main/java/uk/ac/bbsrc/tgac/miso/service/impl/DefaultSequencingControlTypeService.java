package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingControlTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingControlTypeDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingControlTypeService extends AbstractSaveService<SequencingControlType>
    implements SequencingControlTypeService {

  @Autowired
  private SequencingControlTypeDao sequencingControlTypeDao;
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
  public List<SequencingControlType> list() throws IOException {
    return sequencingControlTypeDao.list();
  }

  @Override
  public SaveDao<SequencingControlType> getDao() {
    return sequencingControlTypeDao;
  }

  @Override
  protected void authorizeSave(SequencingControlType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(SequencingControlType object, SequencingControlType beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isSetAndChanged(SequencingControlType::getAlias, object, beforeChange)
        && sequencingControlTypeDao.getByAlias(object.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("sequencing control type", "alias"));
    }
  }

  @Override
  protected void applyChanges(SequencingControlType to, SequencingControlType from) throws IOException {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(SequencingControlType object) throws IOException {
    ValidationResult result = new ValidationResult();

    long usage = sequencingControlTypeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
