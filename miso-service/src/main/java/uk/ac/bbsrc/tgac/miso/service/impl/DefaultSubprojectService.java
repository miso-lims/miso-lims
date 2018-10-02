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
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSubprojectService implements SubprojectService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSubprojectService.class);

  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private SubprojectDao subprojectDao;
  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Subproject get(long subprojectId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return subprojectDao.getSubproject(subprojectId);
  }

  @Override
  public Long create(Subproject subproject, Long parentProjectId) throws IOException {
    authorizationManager.throwIfNotInternal();
    User user = authorizationManager.getCurrentUser();
    Project parentProject = projectStore.get(parentProjectId);
    subproject.setCreatedBy(user);
    subproject.setUpdatedBy(user);
    subproject.setParentProject(parentProject);
    return subprojectDao.addSubproject(subproject);
  }

  @Override
  public void update(Subproject subproject) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Subproject updatedSubproject = get(subproject.getId());
    updatedSubproject.setAlias(subproject.getAlias());
    updatedSubproject.setDescription(subproject.getDescription());
    updatedSubproject.setPriority(subproject.getPriority());
    updatedSubproject.setReferenceGenomeId(subproject.getReferenceGenomeId());
    User user = authorizationManager.getCurrentUser();
    updatedSubproject.setUpdatedBy(user);
    subprojectDao.update(updatedSubproject);
  }

  @Override
  public Set<Subproject> getAll() throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return Sets.newHashSet(subprojectDao.getSubproject());
  }

  @Override
  public Set<Subproject> getByProjectId(Long projectId) throws IOException {
    Project project = projectStore.get(projectId);
    authorizationManager.throwIfNotReadable(project);
    return Sets.newHashSet(subprojectDao.getByProjectId(projectId));
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
  public ValidationResult validateDeletion(Subproject object) {
    ValidationResult result = new ValidationResult();

    long usage = subprojectDao.getUsage(object);
    if (usage > 1L) {
      result.addError(new ValidationError(usage + " sample" + (usage > 1L ? "s are" : " is") + " associated with subproject '"
          + object.getAlias() + "'"));
    }

    return result;
  }

}
