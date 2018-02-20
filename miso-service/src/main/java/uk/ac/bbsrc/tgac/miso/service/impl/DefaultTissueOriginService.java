package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.store.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueOriginService implements TissueOriginService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultTissueOriginService.class);

  @Autowired
  private TissueOriginDao tissueOriginDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public TissueOrigin get(Long tissueOriginId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return tissueOriginDao.getTissueOrigin(tissueOriginId);
  }

  @Override
  public Long create(TissueOrigin tissueOrigin) throws IOException {
    authorizationManager.throwIfNotInternal();
    setChangeDetails(tissueOrigin);
    validateChange(tissueOrigin, null);
    return tissueOriginDao.addTissueOrigin(tissueOrigin);
  }

  @Override
  public void update(TissueOrigin tissueOrigin) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueOrigin managed = get(tissueOrigin.getId());
    validateChange(tissueOrigin, managed);
    managed.setAlias(tissueOrigin.getAlias());
    managed.setDescription(tissueOrigin.getDescription());
    setChangeDetails(managed);
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

  /**
   * Updates all user data and timestamps associated with the change. Existing timestamps will be preserved
   * if the TissueOrigin is unsaved, and they are already set
   * 
   * @param tissueOrigin the TissueOrigin to update
   * @throws IOException
   */
  private void setChangeDetails(TissueOrigin tissueOrigin) throws IOException {
    User user = authorizationManager.getCurrentUser();
    Date now = new Date();
    tissueOrigin.setUpdatedBy(user);

    if (tissueOrigin.getId() == TissueOrigin.UNSAVED_ID) {
      tissueOrigin.setCreatedBy(user);
      if (tissueOrigin.getCreationDate() == null) {
        tissueOrigin.setCreationDate(now);
      }
      if (tissueOrigin.getLastUpdated() == null) {
        tissueOrigin.setLastUpdated(now);
      }
    } else {
      tissueOrigin.setLastUpdated(now);
    }
  }

  @Override
  public Set<TissueOrigin> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(tissueOriginDao.getTissueOrigin());
  }

  @Override
  public void delete(Long tissueOriginId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueOrigin tissueOrigin = get(tissueOriginId);
    validateDelete(tissueOrigin);
    tissueOriginDao.deleteTissueOrigin(tissueOrigin);
  }

  private void validateDelete(TissueOrigin tissueOrigin) {
    List<ValidationError> errors = new ArrayList<>();

    int usage = tissueOriginDao.getUsageCount(tissueOrigin.getId());
    if (usage > 0) {
      errors.add(new ValidationError("Tissue Origin '" + tissueOrigin.getAlias() + "' is used by " + usage + " samples"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

}
