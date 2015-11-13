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

import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;

@Transactional
@Service
public class DefaultSampleGroupService implements SampleGroupService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleGroupService.class);

  @Autowired
  private SampleGroupDao sampleGroupDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleGroupId get(Long sampleGroupId) {
    return sampleGroupDao.getSampleGroup(sampleGroupId);
  }

  @Override
  public Long create(SampleGroupId sampleGroup) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    sampleGroup.setCreatedBy(user);
    sampleGroup.setUpdatedBy(user);
    return sampleGroupDao.addSampleGroup(sampleGroup);
  }

  @Override
  public void update(SampleGroupId sampleGroup) throws IOException {
    SampleGroupId updatedSampleGroup = get(sampleGroup.getSampleGroupId());
    // updatedSampleGroup.setAlias(sampleGroup.getAlias());
    updatedSampleGroup.setDescription(sampleGroup.getDescription());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSampleGroup.setUpdatedBy(user);
    sampleGroupDao.update(updatedSampleGroup);
  }

  @Override
  public Set<SampleGroupId> getAll() {
    return Sets.newHashSet(sampleGroupDao.getSampleGroups());
  }

  @Override
  public void delete(Long sampleGroupId) {
    SampleGroupId sampleGroup = get(sampleGroupId);
    sampleGroupDao.deleteSampleGroup(sampleGroup);
  }

}
