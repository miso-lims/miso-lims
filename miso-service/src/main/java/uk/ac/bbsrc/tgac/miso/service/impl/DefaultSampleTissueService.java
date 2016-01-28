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
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.persistence.SampleTissueDao;
import uk.ac.bbsrc.tgac.miso.service.SampleTissueService;

@Transactional
@Service
public class DefaultSampleTissueService implements SampleTissueService {
  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleTissueService.class);
  @Autowired
  private SampleTissueDao sampleTissueDao;
  @Autowired
  private SecurityManager securityManager;

  @Override
  public Long create(SampleTissue sampleTissue) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    sampleTissue.setCreatedBy(user);
    sampleTissue.setUpdatedBy(user);
    return sampleTissueDao.addSampleTissue(sampleTissue);
  }

  @Override
  public void delete(Long sampleTissueId) {
    SampleTissue sampleTissue = get(sampleTissueId);
    sampleTissueDao.deleteSampleTissue(sampleTissue);
  }

  @Override
  public SampleTissue get(Long sampleTissueId) {
    return sampleTissueDao.getSampleTissue(sampleTissueId);
  }

  @Override
  public Set<SampleTissue> getAll() {
    return Sets.newHashSet(sampleTissueDao.getSampleTissue());
  }

  public SampleTissueDao getSampleTissueDao() {
    return sampleTissueDao;
  }

  public SecurityManager getSecurityManager() {
    return securityManager;
  }

  public void setSampleTissueDao(SampleTissueDao sampleTissueDao) {
    this.sampleTissueDao = sampleTissueDao;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Override
  public void update(SampleTissue sampleTissue) throws IOException {
    SampleTissue updatedSampleTissue = get(sampleTissue.getId());
    updatedSampleTissue.setInstituteTissueName(sampleTissue.getInstituteTissueName());
    updatedSampleTissue.setCellularity(sampleTissue.getCellularity());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSampleTissue.setUpdatedBy(user);
    sampleTissueDao.update(updatedSampleTissue);
  }

}
