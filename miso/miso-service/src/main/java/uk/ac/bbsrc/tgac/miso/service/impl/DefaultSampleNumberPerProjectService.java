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

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.SampleNumberPerProject;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleNumberPerProjectDao;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleNumberPerProjectService implements SampleNumberPerProjectService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleNumberPerProjectService.class);

  @Autowired
  private SampleNumberPerProjectDao sampleNumberPerProjectDao;

  @Autowired
  private ProjectStore projectStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  public void setSampleNumberPerProjectDao(SampleNumberPerProjectDao sampleNumberPerProjectDao) {
    this.sampleNumberPerProjectDao = sampleNumberPerProjectDao;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public SampleNumberPerProject get(Long sampleNumberPerProjectId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleNumberPerProjectDao.getSampleNumberPerProject(sampleNumberPerProjectId);
  }

  @Override
  public Long create(SampleNumberPerProject sampleNumberPerProject, Long projectId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    Project project = projectStore.get(projectId);
    sampleNumberPerProject.setCreatedBy(user);
    sampleNumberPerProject.setUpdatedBy(user);
    sampleNumberPerProject.setProject(project);
    return sampleNumberPerProjectDao.addSampleNumberPerProject(sampleNumberPerProject);
  }

  @Override
  public void update(SampleNumberPerProject sampleNumberPerProject) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleNumberPerProject updatedSampleNumberPerProject = get(sampleNumberPerProject.getId());
    // updatedSampleNumberPerProject.setAlias(sampleNumberPerProject.getAlias());
    User user = authorizationManager.getCurrentUser();
    updatedSampleNumberPerProject.setUpdatedBy(user);
    sampleNumberPerProjectDao.update(updatedSampleNumberPerProject);
  }

  @Override
  public Set<SampleNumberPerProject> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleNumberPerProjectDao.getSampleNumberPerProject());
  }

  @Override
  public void delete(Long sampleNumberPerProjectId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleNumberPerProject sampleNumberPerProject = get(sampleNumberPerProjectId);
    sampleNumberPerProjectDao.deleteSampleNumberPerProject(sampleNumberPerProject);
  }

  @Override
  public String nextNumber(Project project) throws IOException {
    User user = authorizationManager.getCurrentUser();
    return sampleNumberPerProjectDao.nextNumber(project, user);
  }

  @Override
  public SampleNumberPerProject getByProject(Project project) throws IOException {
    authorizationManager.throwIfNotReadable(project);
    return sampleNumberPerProjectDao.getByProject(project);
  }

}
