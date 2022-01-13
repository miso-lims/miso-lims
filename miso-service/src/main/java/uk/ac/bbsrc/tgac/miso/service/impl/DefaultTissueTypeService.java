package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TissueTypeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueTypeService extends AbstractSaveService<TissueType> implements TissueTypeService {

  @Autowired
  private TissueTypeDao tissueTypeDao;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  public SaveDao<TissueType> getDao() {
    return tissueTypeDao;
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
  protected void authorizeUpdate(TissueType object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void beforeSave(TissueType object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  protected void collectValidationErrors(TissueType tissueType, TissueType beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isSetAndChanged(TissueType::getAlias, tissueType, beforeChange)
        && tissueTypeDao.getByAlias(tissueType.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("tissue type", "alias"));
    }
  }

  @Override
  protected void applyChanges(TissueType to, TissueType from) {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
  }

  @Override
  public List<TissueType> list() throws IOException {
    return tissueTypeDao.list();
  }

  @Override
  public List<TissueType> listByIdList(List<Long> ids) throws IOException {
    return tissueTypeDao.listByIdList(ids);
  }

  @Override
  public ValidationResult validateDeletion(TissueType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = tissueTypeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }
    return result;
  }

}
