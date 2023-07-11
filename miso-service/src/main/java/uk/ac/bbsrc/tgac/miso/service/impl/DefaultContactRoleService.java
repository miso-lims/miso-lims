package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.ContactRole;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContactRoleService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.ContactRoleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultContactRoleService extends AbstractSaveService<ContactRole> implements ContactRoleService {

  @Autowired
  private ContactRoleDao contactRoleDao;
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public SaveDao<ContactRole> getDao() {
    return contactRoleDao;
  }

  @Override
  public List<ContactRole> list() throws IOException {
    return contactRoleDao.list();
  }

  @Override
  public List<ContactRole> listByIdList(List<Long> ids) throws IOException {
    return contactRoleDao.listByIdList(ids);
  }

  @Override
  protected void applyChanges(ContactRole to, ContactRole from) throws IOException {
    to.setName(from.getName());
  }

  @Override
  protected void collectValidationErrors(ContactRole object, ContactRole beforeChange, List<ValidationError> errors)
      throws IOException {
    if (ValidationUtils.isChanged(ContactRole::getName, object, beforeChange)
        && contactRoleDao.getByName(object.getName()) != null) {
      errors.add(ValidationError.forDuplicate("contactRole", "name"));
    }
  }

  @Override
  protected void authorizeUpdate(ContactRole object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  // @Override
  // public ValidationResult validateDeletion(ContactRole object) throws IOException {
  // ValidationResult result = new ValidationResult();
  // long usage = contactRoleDao.getUsage(object);
  // if (usage > 0L) {
  // result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.projects(usage)));
  // }
  // return result;
  // }

}
