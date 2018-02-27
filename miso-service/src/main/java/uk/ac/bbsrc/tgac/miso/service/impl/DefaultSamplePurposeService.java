package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSamplePurposeService implements SamplePurposeService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSamplePurposeService.class);

  @Autowired
  private SamplePurposeDao samplePurposeDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SamplePurpose get(long samplePurposeId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return samplePurposeDao.getSamplePurpose(samplePurposeId);
  }

  @Override
  public Long create(SamplePurpose samplePurpose) throws IOException {
    authorizationManager.throwIfNotInternal();
    User user = authorizationManager.getCurrentUser();
    samplePurpose.setCreatedBy(user);
    samplePurpose.setUpdatedBy(user);
    return samplePurposeDao.addSamplePurpose(samplePurpose);
  }

  @Override
  public void update(SamplePurpose samplePurpose) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SamplePurpose updatedSamplePurpose = get(samplePurpose.getId());
    updatedSamplePurpose.setAlias(samplePurpose.getAlias());
    User user = authorizationManager.getCurrentUser();
    updatedSamplePurpose.setUpdatedBy(user);
    samplePurposeDao.update(updatedSamplePurpose);
  }

  @Override
  public Set<SamplePurpose> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(samplePurposeDao.getSamplePurpose());
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
  public ValidationResult validateDeletion(SamplePurpose object) {
    ValidationResult result = new ValidationResult();

    long usage = samplePurposeDao.getUsage(object);
    if (usage > 0L) {
      result.addError(new ValidationError("Sample Purpose '" + object.getAlias() + "' is used by " + usage + " samples"));
    }

    return result;
  }

}
