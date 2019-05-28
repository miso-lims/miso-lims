package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultDetailedQcStatusService implements DetailedQcStatusService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultDetailedQcStatusService.class);

  @Autowired
  private DetailedQcStatusDao detailedQcStatusDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  @Override
  public DetailedQcStatus get(long detailedQcStatus) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return detailedQcStatusDao.get(detailedQcStatus);
  }

  @Override
  public long create(DetailedQcStatus detailedQcStatus) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(detailedQcStatus, null);
    User user = authorizationManager.getCurrentUser();
    detailedQcStatus.setChangeDetails(user);
    return detailedQcStatusDao.create(detailedQcStatus);
  }

  @Override
  public long update(DetailedQcStatus detailedQcStatus) throws IOException {
    authorizationManager.throwIfNonAdmin();
    DetailedQcStatus managed = get(detailedQcStatus.getId());
    validateChange(detailedQcStatus, managed);
    applyChanges(managed, detailedQcStatus);
    User user = authorizationManager.getCurrentUser();
    managed.setChangeDetails(user);
    return detailedQcStatusDao.update(managed);
  }

  private void validateChange(DetailedQcStatus detailedQcStatus, DetailedQcStatus beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(DetailedQcStatus::getDescription, detailedQcStatus, beforeChange)
        && detailedQcStatusDao.getByDescription(detailedQcStatus.getDescription()) != null) {
      errors.add(new ValidationError("description", "There is already a detailed QC status with this description"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void applyChanges(DetailedQcStatus to, DetailedQcStatus from) {
    to.setStatus(from.getStatus());
    to.setDescription(from.getDescription());
    to.setNoteRequired(from.getNoteRequired());
  }

  @Override
  public Set<DetailedQcStatus> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(detailedQcStatusDao.list());
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
  public ValidationResult validateDeletion(DetailedQcStatus object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = detailedQcStatusDao.getUsage(object);
    if (usage > 0L) {
      result.addError(new ValidationError(
          "Detailed QC status '" + object.getDescription() + "' is used by " + usage + " sample" + (usage > 1L ? "s" : "")));
    }
    return result;
  }

}
