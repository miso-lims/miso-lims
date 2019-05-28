package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.TissueTypeService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueTypeService implements TissueTypeService {

  @Autowired
  private TissueTypeDao tissueTypeDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  @Override
  public TissueType get(long tissueTypeId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return tissueTypeDao.get(tissueTypeId);
  }

  @Override
  public long create(TissueType tissueType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(tissueType, null);
    User user = authorizationManager.getCurrentUser();
    tissueType.setChangeDetails(user);
    return tissueTypeDao.create(tissueType);
  }

  @Override
  public long update(TissueType tissueType) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueType managed = get(tissueType.getId());
    validateChange(tissueType, managed);
    applyChanges(managed, tissueType);
    User user = authorizationManager.getCurrentUser();
    tissueType.setChangeDetails(user);
    return tissueTypeDao.update(managed);
  }

  private void validateChange(TissueType tissueType, TissueType beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(TissueType::getAlias, tissueType, beforeChange)
        && tissueTypeDao.getByAlias(tissueType.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a tissue type with this alias"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(TissueType to, TissueType from) {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
  }

  @Override
  public Set<TissueType> list() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(tissueTypeDao.list());
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
  public ValidationResult validateDeletion(TissueType object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = tissueTypeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(
          new ValidationError("Tissue type '" + object.getAlias() + "' is used by " + usage + " sample" + (usage > 1L ? "s" : "")));
    }
    return result;
  }

}
