package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStudyService implements StudyService, PaginatedDataSource<Study> {
  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private NamingScheme namingScheme;

  @Autowired
  private ProjectStore projectStore;

  @Autowired
  private StudyStore studyStore;

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

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return adjustNameLength(studyStore.getStudyColumnSizes(), namingScheme);
  }

  public ProjectStore getProjectStore() {
    return projectStore;
  }

  public StudyStore getStudyStore() {
    return studyStore;
  }

  @Override
  public StudyType getType(long id) {
    return studyStore.getType(id);
  }

  @Override
  public Collection<Study> listByProjectId(long projectId) throws IOException {
    return studyStore.listByProjectId(projectId);
  }

  @Override
  public Collection<Study> listBySearch(String query) throws IOException {
    return studyStore.listBySearch(query);
  }

  @Override
  public Collection<StudyType> listTypes() throws IOException {
    return studyStore.listAllStudyTypes();
  }

  @Override
  public Collection<Study> listWithLimit(long limit) throws IOException {
    return studyStore.listAllWithLimit(limit);
  }

  @Override
  public long save(Study study) throws IOException {
    if (study.getId() == StudyImpl.UNSAVED_ID) {
      study.setChangeDetails(authorizationManager.getCurrentUser());
      study.setStudyType(studyStore.getType(study.getStudyType().getId()));
      study.setProject(projectStore.get(study.getProject().getId()));
      study.setName(LimsUtils.generateTemporaryName());
      long id = studyStore.save(study);
      try {
        study.setName(namingScheme.generateNameFor(study));
        validateNameOrThrow(study, namingScheme);
        studyStore.save(study);
      } catch (MisoNamingException e) {
        throw new IOException(e);
      }
      return id;
    } else {
      Study original = studyStore.get(study.getId());
      original.setAccession(study.getAccession());
      original.setAlias(study.getAlias());
      original.setDescription(study.getDescription());
      original.setChangeDetails(authorizationManager.getCurrentUser());

      // project is immutable
      original.setStudyType(studyStore.getType(study.getStudyType().getId()));
      return studyStore.save(original);
    }
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setStudyStore(StudyStore studyStore) {
    this.studyStore = studyStore;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return studyStore.count(errorHandler, filter);
  }

  @Override
  public List<Study> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol, PaginationFilter... filter)
      throws IOException {
    return studyStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

}
