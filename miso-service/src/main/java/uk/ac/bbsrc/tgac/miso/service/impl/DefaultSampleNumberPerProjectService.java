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

import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.persistence.SampleNumberPerProjectDao;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;

@Transactional
@Service
public class DefaultSampleNumberPerProjectService implements SampleNumberPerProjectService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleNumberPerProjectService.class);

  @Autowired
  private SampleNumberPerProjectDao sampleNumberPerProjectDao;

  @Autowired
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  @Override
  public SampleNumberPerProject get(Long sampleNumberPerProjectId) {
    return sampleNumberPerProjectDao.getSampleNumberPerProject(sampleNumberPerProjectId);
  }

  @Override
  public Long create(SampleNumberPerProject sampleNumberPerProject) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    sampleNumberPerProject.setCreatedBy(user);
    sampleNumberPerProject.setUpdatedBy(user);

    return sampleNumberPerProjectDao.addSampleNumberPerProject(sampleNumberPerProject);
  }

  @Override
  public void update(SampleNumberPerProject sampleNumberPerProject) throws IOException {
    SampleNumberPerProject updatedSampleNumberPerProject = get(sampleNumberPerProject.getSampleNumberPerProjectId());
    // updatedSampleNumberPerProject.setAlias(sampleNumberPerProject.getAlias());
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    updatedSampleNumberPerProject.setUpdatedBy(user);
    sampleNumberPerProjectDao.update(updatedSampleNumberPerProject);
  }

  @Override
  public Set<SampleNumberPerProject> getAll() {
    return Sets.newHashSet(sampleNumberPerProjectDao.getSampleNumberPerProject());
  }

  @Override
  public void delete(Long sampleNumberPerProjectId) {
    SampleNumberPerProject sampleNumberPerProject = get(sampleNumberPerProjectId);
    sampleNumberPerProjectDao.deleteSampleNumberPerProject(sampleNumberPerProject);
  }

}
