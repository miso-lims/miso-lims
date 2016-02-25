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
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO;

@Transactional
@Service
public class DefaultSubprojectService implements SubprojectService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSubprojectService.class);

  @Autowired
  private SQLProjectDAO sqlProjectDAO;
  @Autowired
  private SubprojectDao subprojectDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Subproject get(Long subprojectId) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return subprojectDao.getSubproject(subprojectId);
  }
  

  @Override
  public Long create(Subproject subproject, Long parentProjectId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    User user = authorizationManager.getCurrentUser();
    Project parentProject = sqlProjectDAO.get(parentProjectId);
    subproject.setCreatedBy(user);
    subproject.setUpdatedBy(user);
    subproject.setParentProject(parentProject);
    return subprojectDao.addSubproject(subproject);
  }

  @Override
  public void update(Subproject subproject) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Subproject updatedSubproject = get(subproject.getSubprojectId());
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
  public void delete(Long subprojectId) throws IOException {
    authorizationManager.throwIfNonAdmin();
    Subproject subproject = get(subprojectId);
    subprojectDao.deleteSubproject(subproject);
  }

}
