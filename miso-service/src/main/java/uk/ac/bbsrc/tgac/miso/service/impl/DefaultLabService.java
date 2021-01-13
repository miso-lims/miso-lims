package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LabService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.LabDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLabService implements LabService {

  @Autowired
  private LabDao labDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setLabDao(LabDao labDao) {
    this.labDao = labDao;
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
  public long create(Lab lab) throws IOException {
    authorizationManager.throwIfNotInternal();
    lab.setChangeDetails(authorizationManager.getCurrentUser());
    return labDao.addLab(lab);
  }

  @Override
  public long update(Lab lab) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Lab updatedLab = get(lab.getId());
    updatedLab.setAlias(lab.getAlias());
    updatedLab.setArchived(lab.isArchived());
    lab.setChangeDetails(authorizationManager.getCurrentUser());
    labDao.update(updatedLab);
    return updatedLab.getId();
  }

  @Override
  public List<Lab> list() throws IOException {
    return labDao.getLabs();
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

    long tissueUsage = labDao.getUsageByTissues(object);
    if (tissueUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, tissueUsage, Pluralizer.samples(tissueUsage)));
    }
    long transferUsage = labDao.getUsageByTransfers(object);
    if (transferUsage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, transferUsage, Pluralizer.transfers(transferUsage)));
    }

    return result;
  }

}
