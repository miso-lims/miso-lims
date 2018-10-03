package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;
import uk.ac.bbsrc.tgac.miso.core.store.AttachmentCategoryStore;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.service.AttachmentCategoryService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultAttachmentCategoryService implements AttachmentCategoryService {

  @Autowired
  private AttachmentCategoryStore attachmentCategoryStore;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public AttachmentCategory get(long id) throws IOException {
    return attachmentCategoryStore.get(id);
  }

  @Override
  public List<AttachmentCategory> list() throws IOException {
    return attachmentCategoryStore.list();
  }

  @Override
  public long save(AttachmentCategory category) throws IOException {
    if (!category.isSaved()) {
      validateChange(category, null);
      return attachmentCategoryStore.save(category);
    } else {
      AttachmentCategory managed = get(category.getId());
      validateChange(category, managed);
      applyChanges(category, managed);
      return attachmentCategoryStore.save(managed);
    }
  }

  private void validateChange(AttachmentCategory category, AttachmentCategory beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if ((beforeChange == null || !category.getAlias().equals(beforeChange.getAlias()))
        && attachmentCategoryStore.getByAlias(category.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already an attachment category with that alias"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(AttachmentCategory from, AttachmentCategory to) {
    to.setAlias(from.getAlias());
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
  public ValidationResult validateDeletion(AttachmentCategory object) {
    ValidationResult result = new ValidationResult();

    long usage = attachmentCategoryStore.getUsage(object);
    if (usage > 0L) {
      result.addError(new ValidationError("Attachment category '" + object.getAlias() + "' is used by " + usage + " attachments"));
    }

    return result;
  }

}
