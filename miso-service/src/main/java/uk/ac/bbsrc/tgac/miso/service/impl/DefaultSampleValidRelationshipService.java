package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultSampleValidRelationshipService implements SampleValidRelationshipService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleValidRelationshipService.class);

  @Autowired
  private SampleValidRelationshipDao sampleValidRelationshipDao;

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SampleValidRelationship get(Long sampleValidRelationshipId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleValidRelationshipDao.getSampleValidRelationship(sampleValidRelationshipId);
  }

  @Override
  public Long create(SampleValidRelationship sampleValidRelationship, Long parentSampleClassId, Long childSampleClassId)
      throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    SampleClass parent = sampleClassDao.getSampleClass(parentSampleClassId);
    SampleClass child = sampleClassDao.getSampleClass(childSampleClassId);
    sampleValidRelationship.setCreatedBy(user);
    sampleValidRelationship.setUpdatedBy(user);
    sampleValidRelationship.setParent(parent);
    sampleValidRelationship.setChild(child);
    return sampleValidRelationshipDao.addSampleValidRelationship(sampleValidRelationship);
  }

  @Override
  public void update(SampleValidRelationship sampleValidRelationship, Long parentSampleClassId, Long childSampleClassId)
      throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleValidRelationship updatedSampleValidRelationship = get(sampleValidRelationship.getSampleValidRelationshipId());
    SampleClass parent = sampleClassDao.getSampleClass(parentSampleClassId);
    SampleClass child = sampleClassDao.getSampleClass(childSampleClassId);
    updatedSampleValidRelationship.setParent(parent);
    updatedSampleValidRelationship.setChild(child);
    User user = authorizationManager.getCurrentUser();
    updatedSampleValidRelationship.setUpdatedBy(user);
    sampleValidRelationshipDao.update(updatedSampleValidRelationship);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Set<SampleValidRelationship> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleValidRelationshipDao.getSampleValidRelationship());
  }

  @Override
  public void delete(Long sampleValidRelationshipId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleValidRelationship sampleValidRelationship = get(sampleValidRelationshipId);
    sampleValidRelationshipDao.deleteSampleValidRelationship(sampleValidRelationship);
  }

}
