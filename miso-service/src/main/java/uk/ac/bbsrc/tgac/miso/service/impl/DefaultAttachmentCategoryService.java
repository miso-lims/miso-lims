package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.AttachmentCategoryService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.AttachmentCategoryStore;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultAttachmentCategoryService extends AbstractSaveService<AttachmentCategory>
    implements AttachmentCategoryService {

  @Autowired
  private AttachmentCategoryStore attachmentCategoryStore;
  @Autowired
  private TransactionTemplate transactionTemplate;
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
  public TransactionTemplate getTransactionTemplate() {
    return transactionTemplate;
  }

  @Override
  public SaveDao<AttachmentCategory> getDao() {
    return attachmentCategoryStore;
  }

  @Override
  public List<AttachmentCategory> listByIdList(List<Long> ids) throws IOException {
    return attachmentCategoryStore.listByIdList(ids);
  }

  @Override
  public List<AttachmentCategory> list() throws IOException {
    return attachmentCategoryStore.list();
  }

  @Override
  protected void collectValidationErrors(AttachmentCategory category, AttachmentCategory beforeChange, List<ValidationError> errors) throws IOException {
    if ((beforeChange == null || !category.getAlias().equals(beforeChange.getAlias()))
        && attachmentCategoryStore.getByAlias(category.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already an attachment category with that alias"));
    }
  }

  @Override
  protected void applyChanges(AttachmentCategory to, AttachmentCategory from) {
    to.setAlias(from.getAlias());
  }

  @Override
  public ValidationResult validateDeletion(AttachmentCategory object) {
    ValidationResult result = new ValidationResult();

    long usage = attachmentCategoryStore.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.attachments(usage)));
    }

    return result;
  }

}
