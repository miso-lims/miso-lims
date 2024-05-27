package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultDetailedQcStatusService extends AbstractSaveService<DetailedQcStatus>
    implements DetailedQcStatusService {

  @Autowired
  private DetailedQcStatusDao detailedQcStatusDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public SaveDao<DetailedQcStatus> getDao() {
    return detailedQcStatusDao;
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
  protected void authorizeUpdate(DetailedQcStatus object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void collectValidationErrors(DetailedQcStatus object, DetailedQcStatus beforeChange,
      List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(DetailedQcStatus::getDescription, object, beforeChange)
        && detailedQcStatusDao.getByDescription(object.getDescription()) != null) {
      errors.add(new ValidationError("description", "There is already a detailed QC status with this description"));
    }
  }

  protected void applyChanges(DetailedQcStatus to, DetailedQcStatus from) {
    to.setStatus(from.getStatus());
    to.setDescription(from.getDescription());
    to.setNoteRequired(from.getNoteRequired());
    to.setArchived(from.getArchived());
  }

  @Override
  protected void beforeSave(DetailedQcStatus object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public List<DetailedQcStatus> listByIdList(List<Long> ids) throws IOException {
    return detailedQcStatusDao.listByIdList(ids);
  }

  @Override
  public ValidationResult validateDeletion(DetailedQcStatus object) throws IOException {
    ValidationResult result = new ValidationResult();
    long samUsage = detailedQcStatusDao.getUsageBySamples(object);
    if (samUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, samUsage, Pluralizer.samples(samUsage)));
    }
    long libUsage = detailedQcStatusDao.getUsageByLibraries(object);
    if (libUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, libUsage, Pluralizer.libraries(libUsage)));
    }
    long aliUsage = detailedQcStatusDao.getUsageByLibraryAliquots(object);
    if (aliUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, aliUsage, Pluralizer.libraryAliquots(aliUsage)));
    }
    return result;
  }

  @Override
  public List<DetailedQcStatus> list() throws IOException {
    return detailedQcStatusDao.list();
  }
}
