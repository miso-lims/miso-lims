package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Contact;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ContactService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.ContactStore;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultContactService extends AbstractSaveService<Contact> implements ContactService {

  @Autowired
  private ContactStore contactStore;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public List<Contact> listBySearch(String search) throws IOException {
    return contactStore.listBySearch(search);
  }

  @Override
  public SaveDao<Contact> getDao() {
    return contactStore;
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
  protected void collectValidationErrors(Contact object, Contact beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(Contact::getEmail, object, beforeChange) && contactStore.getByEmail(object.getEmail()) != null) {
      errors.add(ValidationError.forDuplicate("contact", "email", "email address"));
    }
  }

  @Override
  protected void applyChanges(Contact to, Contact from) throws IOException {
    to.setName(from.getName());
    to.setEmail(from.getEmail());
  }

  @Override
  public void authorizeDeletion(Contact object) throws IOException {
    // Anyone can delete
  }

  @Override
  public List<Contact> listByIdList(List<Long> ids) throws IOException {
    return contactStore.listByIdList(ids);
  }

  @Override
  public List<Contact> list() throws IOException {
    return contactStore.list();
  }

}
