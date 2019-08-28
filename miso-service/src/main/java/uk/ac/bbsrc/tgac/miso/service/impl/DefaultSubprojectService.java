package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
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
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;

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
  public List<Subproject> list() throws IOException {
    return subprojectDao.getSubproject();
  }

  @Override
  public Set<Subproject> getByProjectId(Long projectId) throws IOException {
    Project project = projectStore.get(projectId);
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
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.samples(usage)));
    }

    return result;
  }

}
