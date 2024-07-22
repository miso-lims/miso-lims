package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.validateNameOrThrow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.StudyService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.StudyStore;
import uk.ac.bbsrc.tgac.miso.persistence.StudyTypeDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStudyService implements StudyService, PaginatedDataSource<Study> {
  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private NamingSchemeHolder namingSchemeHolder;

  @Autowired
  private ProjectStore projectStore;

  @Autowired
  private StudyStore studyStore;

  @Autowired
  private StudyTypeDao studyTypeDao;

  @Autowired
  private DeletionStore deletionStore;

  @Override
  public Study get(long studyId) throws IOException {
    return studyStore.get(studyId);
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public ProjectStore getProjectStore() {
    return projectStore;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public StudyStore getStudyStore() {
    return studyStore;
  }

  public void setStudyStore(StudyStore studyStore) {
    this.studyStore = studyStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setNamingSchemeHolder(NamingSchemeHolder namingSchemeHolder) {
    this.namingSchemeHolder = namingSchemeHolder;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public Collection<Study> listByProjectId(long projectId) throws IOException {
    return studyStore.listByProjectId(projectId);
  }

  @Override
  public Collection<Study> listWithLimit(long limit) throws IOException {
    return studyStore.listAllWithLimit(limit);
  }

  @Override
  public long create(Study study) throws IOException {
    study.setStudyType(studyTypeDao.get(study.getStudyType().getId()));
    study.setProject(projectStore.get(study.getProject().getId()));
    validateChange(study, null);
    study.setChangeDetails(authorizationManager.getCurrentUser());
    study.setName(LimsUtils.generateTemporaryName());
    long id = studyStore.create(study);
    try {
      NamingScheme namingScheme = namingSchemeHolder.getPrimary();
      study.setName(namingScheme.generateNameFor(study));
      validateNameOrThrow(study, namingScheme);
      studyStore.update(study);
    } catch (MisoNamingException e) {
      throw new IOException(e);
    }
    return id;
  }

  @Override
  public long update(Study study) throws IOException {
    Study original = studyStore.get(study.getId());
    validateChange(study, original);
    original.setAccession(study.getAccession());
    original.setAlias(study.getAlias());
    original.setDescription(study.getDescription());
    original.setChangeDetails(authorizationManager.getCurrentUser());

    // project is immutable
    original.setStudyType(studyTypeDao.get(study.getStudyType().getId()));
    return studyStore.update(original);
  }

  private void validateChange(Study study, Study beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (ValidationUtils.isSetAndChanged(Study::getAlias, study, beforeChange)
        && studyStore.getByAlias(study.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a study with this alias"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return studyStore.count(errorHandler, filter);
  }

  @Override
  public List<Study> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    return studyStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public ValidationResult validateDeletion(Study object) throws IOException {
    ValidationResult result = new ValidationResult();
    long usage = studyStore.getUsage(object);
    if (usage > 0L) {
      result.addError(ValidationError.forDeletionUsage(object, usage, Pluralizer.experiments(usage)));
    }
    return result;
  }

}
