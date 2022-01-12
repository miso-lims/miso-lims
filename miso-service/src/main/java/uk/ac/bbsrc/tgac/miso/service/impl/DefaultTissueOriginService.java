package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueOriginService extends AbstractSaveService<TissueOrigin> implements TissueOriginService {

  @Autowired
  private TissueOriginDao tissueOriginDao;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private TransactionTemplate transactionTemplate;

  @Override
  protected void authorizeUpdate(TissueOrigin object) throws IOException {
    authorizationManager.throwIfNonAdmin();
  }

  @Override
  protected void beforeSave(TissueOrigin object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  protected void collectValidationErrors(TissueOrigin tissueOrigin, TissueOrigin beforeChange, List<ValidationError> errors) throws IOException {
    if (LimsUtils.isStringBlankOrNull(tissueOrigin.getAlias())) {
      errors.add(new ValidationError("alias", "Alias cannot be blank"));
    }
    if (LimsUtils.isStringBlankOrNull(tissueOrigin.getDescription())) {
      errors.add(new ValidationError("description", "Description cannot be blank"));
    }

    if (beforeChange == null || !tissueOrigin.getAlias().equals(beforeChange.getAlias())) {
      TissueOrigin duplicateAlias = tissueOriginDao.getByAlias(tissueOrigin.getAlias());
      if (duplicateAlias != null) {
        errors.add(new ValidationError("alias", "There is already a Tissue Origin with this alias"));
      }
    }
  }

  @Override
  protected void applyChanges(TissueOrigin to, TissueOrigin from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
  }

  @Override
  public List<TissueOrigin> list() throws IOException {
    return tissueOriginDao.list();
  }

  @Override
  public SaveDao<TissueOrigin> getDao() {
    return tissueOriginDao;
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
  public List<TissueOrigin> listByIdList(List<Long> ids) throws IOException {
    return tissueOriginDao.listByIdList(ids);
  }

  @Override
  public ValidationResult validateDeletion(TissueOrigin object) throws IOException {
    ValidationResult result = new ValidationResult();

    long usage = tissueOriginDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
