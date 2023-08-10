package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.WorkstationService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.WorkstationDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorkstationService extends AbstractSaveService<Workstation> implements WorkstationService {

  @Autowired
  private WorkstationDao workstationDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
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
  public SaveDao<Workstation> getDao() {
    return workstationDao;
  }

  @Override
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public List<Workstation> list() throws IOException {
    return workstationDao.list();
  }

  @Override
  public List<Workstation> listByIdList(List<Long> ids) throws IOException {
    return workstationDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(Workstation object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(Workstation object, Workstation beforeChange, List<ValidationError> errors)
      throws IOException {
    if ((beforeChange == null || !object.getAlias().equals(beforeChange.getAlias()))
        && workstationDao.getByAlias(object.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a workstation with this alias"));
    }
  }

  @Override
  protected void applyChanges(Workstation to, Workstation from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setIdentificationBarcode(from.getIdentificationBarcode());
  }

  @Override
  public ValidationResult validateDeletion(Workstation object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = workstationDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraries(usage)));
    }
    return result;
  }
}
