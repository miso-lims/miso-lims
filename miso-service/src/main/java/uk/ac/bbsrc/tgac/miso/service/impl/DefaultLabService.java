package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.InstituteDao;
import uk.ac.bbsrc.tgac.miso.persistence.LabDao;
import uk.ac.bbsrc.tgac.miso.service.LabService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLabService implements LabService {

  @Autowired
  private LabDao labDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private InstituteDao instituteDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setLabDao(LabDao labDao) {
    this.labDao = labDao;
  }

  public void setInstituteDao(InstituteDao instituteDao) {
    this.instituteDao = instituteDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public Lab get(long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return labDao.getLab(id);
  }

  @Override
  public Long create(Lab lab, Long instituteId) throws IOException {
    authorizationManager.throwIfNotInternal();
    User user = authorizationManager.getCurrentUser();
    lab.setCreatedBy(user);
    lab.setUpdatedBy(user);
    lab.setInstitute(instituteDao.getInstitute(instituteId));
    return labDao.addLab(lab);
  }

  @Override
  public void update(Lab lab, Long instituteId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Lab updatedLab = get(lab.getId());
    updatedLab.setAlias(lab.getAlias());
    updatedLab.setInstitute(instituteDao.getInstitute(instituteId));
    User user = authorizationManager.getCurrentUser();
    updatedLab.setUpdatedBy(user);
    labDao.update(updatedLab);
  }

  @Override
  public Set<Lab> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(labDao.getLabs());
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
  public ValidationResult validateDeletion(Lab object) {
    ValidationResult result = new ValidationResult();

    long usage = labDao.getUsage(object);
    if (usage > 0L) {
      result.addError(new ValidationError(usage + " sample" + (usage > 1L ? "s are" : " is") + " associated with lab '" + object.getAlias()
          + "'"));
    }

    return result;
  }

}
