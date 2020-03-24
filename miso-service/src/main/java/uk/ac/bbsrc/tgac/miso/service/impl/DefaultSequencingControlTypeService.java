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
  protected void collectValidationErrors(SequencingControlType object, SequencingControlType beforeChange, List<ValidationError> errors)
      throws IOException {
    // TODO (not yet modifiable via the UI)

  }

  @Override
  protected void applyChanges(SequencingControlType to, SequencingControlType from) throws IOException {
    // TODO (not yet modifiable via the UI)

  }

  @Override
  public ValidationResult validateDeletion(SequencingControlType object) throws IOException {
    // TODO (not yet modifiable via the UI)
    return new ValidationResult();
  }

}
