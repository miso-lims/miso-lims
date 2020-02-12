package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultTissueMaterialService implements TissueMaterialService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultTissueMaterialService.class);

  @Autowired
  private TissueMaterialDao tissueMaterialDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public TissueMaterial get(long tissueMaterialId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return tissueMaterialDao.getTissueMaterial(tissueMaterialId);
  }

  @Override
  public Long create(TissueMaterial tissueMaterial) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    tissueMaterial.setCreatedBy(user);
    tissueMaterial.setUpdatedBy(user);
    return tissueMaterialDao.addTissueMaterial(tissueMaterial);
  }

  @Override
  public void update(TissueMaterial tissueMaterial) throws IOException {
    authorizationManager.throwIfNonAdmin();
    TissueMaterial updatedTissueMaterial = get(tissueMaterial.getId());
    updatedTissueMaterial.setAlias(tissueMaterial.getAlias());
    User user = authorizationManager.getCurrentUser();
    updatedTissueMaterial.setUpdatedBy(user);
    tissueMaterialDao.update(updatedTissueMaterial);
  }

  @Override
  public List<TissueMaterial> list() throws IOException {
    return tissueMaterialDao.getTissueMaterial();
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
  public ValidationResult validateDeletion(TissueMaterial object) {
    ValidationResult result = new ValidationResult();

    long usage = tissueMaterialDao.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
