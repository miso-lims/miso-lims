package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.TargetedSequencingStore;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTargetedSequencingService extends AbstractSaveService<TargetedSequencing>
    implements TargetedSequencingService {

  @Autowired
  private TargetedSequencingStore targetedSequencingDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private KitDescriptorService kitDescriptorService;
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
  public SaveDao<TargetedSequencing> getDao() {
    return targetedSequencingDao;
  }

  public void setTargetedSequencingDao(TargetedSequencingStore targetedSequencingDao) {
    this.targetedSequencingDao = targetedSequencingDao;
  }

  @Override
  public List<TargetedSequencing> list() throws IOException {
    return targetedSequencingDao.list();
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return targetedSequencingDao.count(errorHandler, filter);
  }

  @Override
  public List<TargetedSequencing> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return targetedSequencingDao.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<TargetedSequencing> listByIdList(List<Long> ids) throws IOException {
    return targetedSequencingDao.listByIdList(ids);
  }

  @Override
  protected void authorizeUpdate(TargetedSequencing object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(TargetedSequencing targetedSequencing, TargetedSequencing beforeChange,
      List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(TargetedSequencing::getAlias, targetedSequencing, beforeChange)
        && targetedSequencingDao.getByAlias(targetedSequencing.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a targeted sequencing with this alias"));
    }
  }

  @Override
  protected void applyChanges(TargetedSequencing to, TargetedSequencing from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setArchived(from.isArchived());
  }

  @Override
  protected void beforeSave(TargetedSequencing object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public ValidationResult validateDeletion(TargetedSequencing object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = targetedSequencingDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.libraryAliquots(usage)));
    }
    return result;
  }

  @Override
  public void beforeDelete(TargetedSequencing object) throws IOException {
    for (KitDescriptor kit : object.getKitDescriptors()) {
      kit.getTargetedSequencing().remove(object);
      kitDescriptorService.update(kit);
    }
    object.getKitDescriptors().clear();
  }

}
