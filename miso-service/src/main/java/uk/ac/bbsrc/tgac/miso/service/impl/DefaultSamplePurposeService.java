package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;

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
    return samplePurposeDao.get(samplePurposeId);
  }

  @Override
  public Long create(SamplePurpose samplePurpose) throws IOException {
    samplePurpose.setChangeDetails(authorizationManager.getCurrentUser());
    return samplePurposeDao.create(samplePurpose);
  }

  @Override
  public void update(SamplePurpose samplePurpose) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SamplePurpose updatedSamplePurpose = get(samplePurpose.getId());
    updatedSamplePurpose.setAlias(samplePurpose.getAlias());
    updatedSamplePurpose.setChangeDetails(authorizationManager.getCurrentUser());
    samplePurposeDao.update(updatedSamplePurpose);
  }

  @Override
  public List<SamplePurpose> list() throws IOException {
    return samplePurposeDao.list();
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
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
