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
import uk.ac.bbsrc.tgac.miso.core.data.SampleGroupId;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSampleGroupService implements SampleGroupService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSampleGroupService.class);

  @Autowired
  private SampleGroupDao sampleGroupDao;

  @Autowired
  private ProjectStore projectStore;

  @Autowired
  private SubprojectDao subprojectDAO;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SampleGroupId get(Long sampleGroupId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return sampleGroupDao.getSampleGroup(sampleGroupId);
  }

  @Override
  public Long create(SampleGroupId sampleGroup, Long projectId, Long subprojectId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    User user = authorizationManager.getCurrentUser();
    Project project = projectStore.get(projectId);
    Subproject subproject = subprojectId == null ? null : subprojectDAO.getSubproject(subprojectId);
    if (subproject != null && subproject.getParentProject().getProjectId() != projectId) {
      throw new IllegalArgumentException("Subproject specified is not part of project.");
    }
    sampleGroup.setCreatedBy(user);
    sampleGroup.setUpdatedBy(user);
    sampleGroup.setProject(project);
    sampleGroup.setSubproject(subproject);
    return sampleGroupDao.addSampleGroup(sampleGroup);
  }

  @Override
  public void update(SampleGroupId sampleGroup) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleGroupId updatedSampleGroup = get(sampleGroup.getId());
    // updatedSampleGroup.setAlias(sampleGroup.getAlias());
    updatedSampleGroup.setDescription(sampleGroup.getDescription());
    User user = authorizationManager.getCurrentUser();
    updatedSampleGroup.setUpdatedBy(user);
    sampleGroupDao.update(updatedSampleGroup);
  }

  @Override
  public Set<SampleGroupId> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleGroupDao.getSampleGroups());
  }

  @Override
  public void delete(Long sampleGroupId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    SampleGroupId sampleGroup = get(sampleGroupId);
    sampleGroupDao.deleteSampleGroup(sampleGroup);
  }

  @Override
  public Set<SampleGroupId> getAllForProject(Long projectId) throws AuthorizationException, IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleGroupDao.getSampleGroupsForProject(projectId));
  }

  @Override
  public Set<SampleGroupId> getAllForSubproject(Long subprojectId) throws AuthorizationException, IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(sampleGroupDao.getSampleGroupsForSubproject(subprojectId));
  }

}
