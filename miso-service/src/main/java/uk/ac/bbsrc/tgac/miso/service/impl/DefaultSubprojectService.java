package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.ReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.SaveDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.service.AbstractSaveService;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSubprojectService extends AbstractSaveService<Subproject> implements SubprojectService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultSubprojectService.class);

  @Autowired
  private SubprojectDao subprojectDao;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private ReferenceGenomeService referenceGenomeService;
  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public long update(Subproject subproject) throws IOException {
    authorizationManager.throwIfNonAdmin();
    return super.update(subproject);
  }

  @Override
  protected void loadChildEntities(Subproject object) throws IOException {
    ValidationUtils.loadChildEntity(object::setParentProject, object.getParentProject(), projectService, "parentProjectId");
    ValidationUtils.loadChildEntity(object::setReferenceGenome, object.getReferenceGenome(), referenceGenomeService, "referenceGenomeId");
  }

  @Override
  protected void collectValidationErrors(Subproject object, Subproject beforeChange, List<ValidationError> errors) throws IOException {
    if (ValidationUtils.isChanged(Subproject::getAlias, object, beforeChange)
        && subprojectDao.getByProjectAndAlias(object.getParentProject(), object.getAlias()) != null) {
      errors.add(ValidationError.forDuplicate("subproject", "alias"));
    }
  }

  @Override
  protected void applyChanges(Subproject to, Subproject from) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    to.setPriority(from.getPriority());
    to.setReferenceGenome(from.getReferenceGenome());
  }

  @Override
  protected void beforeSave(Subproject object) throws IOException {
    object.setChangeDetails(authorizationManager.getCurrentUser());
  }

  @Override
  public List<Subproject> list() throws IOException {
    return subprojectDao.list();
  }

  @Override
  public Set<Subproject> listByProjectId(Long projectId) throws IOException {
    return Sets.newHashSet(subprojectDao.listByProjectId(projectId));
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

  @Override
  public SaveDao<Subproject> getDao() {
    return subprojectDao;
  }

}
