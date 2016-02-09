package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleValidRelationshipDao;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;

@Transactional
@Service
public class DefaultSampleValidRelationshipService implements SampleValidRelationshipService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleValidRelationshipService.class);

  @Autowired
  private SampleValidRelationshipDao sampleValidRelationshipDao;

  @Autowired
  private SampleClassDao sampleClassDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleValidRelationship get(Long sampleValidRelationshipId) {
    return sampleValidRelationshipDao.getSampleValidRelationship(sampleValidRelationshipId);
  }

  @Override
  public Long create(SampleValidRelationship sampleValidRelationship, Long parentSampleClassId, Long childSampleClassId)
      throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
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
    SampleValidRelationship updatedSampleValidRelationship = get(sampleValidRelationship.getSampleValidRelationshipId());
    SampleClass parent = sampleClassDao.getSampleClass(parentSampleClassId);
    SampleClass child = sampleClassDao.getSampleClass(childSampleClassId);
    updatedSampleValidRelationship.setParent(parent);
    updatedSampleValidRelationship.setChild(child);
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSampleValidRelationship.setUpdatedBy(user);
    sampleValidRelationshipDao.update(updatedSampleValidRelationship);
  }

  @Override
  public Set<SampleValidRelationship> getAll() {
    return Sets.newHashSet(sampleValidRelationshipDao.getSampleValidRelationship());
  }

  @Override
  public void delete(Long sampleValidRelationshipId) {
    SampleValidRelationship sampleValidRelationship = get(sampleValidRelationshipId);
    sampleValidRelationshipDao.deleteSampleValidRelationship(sampleValidRelationship);
  }

}
