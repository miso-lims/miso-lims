package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueOriginService implements TissueOriginService {

  @Autowired
  private TissueOriginDao tissueOriginDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public TissueOrigin get(long tissueOriginId) throws IOException {
    return tissueOriginDao.get(tissueOriginId);
  }

  @Override
  public Long create(TissueOrigin tissueOrigin) throws IOException {
    authorizationManager.throwIfNonAdmin();
    tissueOrigin.setChangeDetails(authorizationManager.getCurrentUser());
    validateChange(tissueOrigin, null);
    return tissueOriginDao.create(tissueOrigin);
  }

  @Override
  public void update(TissueOrigin tissueOrigin) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueOrigin managed = get(tissueOrigin.getId());
    validateChange(tissueOrigin, managed);
    managed.setAlias(tissueOrigin.getAlias());
    managed.setDescription(tissueOrigin.getDescription());
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    tissueOriginDao.update(managed);
  }

  private void validateChange(TissueOrigin tissueOrigin, TissueOrigin beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

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

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  @Override
  public List<TissueOrigin> list() throws IOException {
    return tissueOriginDao.list();
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
  public ValidationResult validateDeletion(TissueOrigin object) {
    ValidationResult result = new ValidationResult();

    long usage = tissueOriginDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
