package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleValidRelationshipService implements SampleValidRelationshipService {

  @Autowired
  private SampleValidRelationshipDao sampleValidRelationshipDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setSampleValidRelationshipDao(SampleValidRelationshipDao sampleValidRelationshipDao) {
    this.sampleValidRelationshipDao = sampleValidRelationshipDao;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public SampleValidRelationship get(Long sampleValidRelationshipId) throws IOException {
    return sampleValidRelationshipDao.getSampleValidRelationship(sampleValidRelationshipId);
  }

  @Override
  public Set<SampleValidRelationship> getAll() throws IOException {
    return Sets.newHashSet(sampleValidRelationshipDao.getSampleValidRelationship());
  }

  @Override
  public void delete(SampleValidRelationship sampleValidRelationship) throws IOException {
    authorizationManager.throwIfNonAdmin();
    sampleValidRelationshipDao.delete(sampleValidRelationship);
  }

}
