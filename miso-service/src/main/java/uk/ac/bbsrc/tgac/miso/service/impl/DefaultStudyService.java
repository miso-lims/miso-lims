package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.StudyService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultStudyService implements StudyService {
  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private NamingScheme namingScheme;

  @Autowired
  private ProjectStore projectStore;

  @Autowired
  private StudyStore studyStore;

  @Override
  public void delete(Study study) throws IOException {
    authorizationManager.throwIfNonAdmin();
    studyStore.remove(study);
  }

  @Override
  public Study get(long studyId) throws IOException {
    Study s = studyStore.get(studyId);
    authorizationManager.throwIfNotReadable(s);
    return s;
  }

  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public Map<String, Integer> getColumnSizes() throws IOException {
    return studyStore.getStudyColumnSizes();
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
  public Collection<Study> list() throws IOException {
    return authorizationManager.filterUnreadable(studyStore.listAll());
  }

  @Override
  public Collection<Study> listByProjectId(long projectId) throws IOException {
    return authorizationManager.filterUnreadable(studyStore.listByProjectId(projectId));
  }

  @Override
  public Collection<Study> listBySearch(String query) throws IOException {
    return authorizationManager.filterUnreadable(studyStore.listBySearch(query));
  }

  @Override
  public Collection<StudyType> listTypes() throws IOException {
    return studyStore.listAllStudyTypes();
  }

  @Override
  public Collection<Study> listWithLimit(long limit) throws IOException {
    return authorizationManager.filterUnreadable(studyStore.listAllWithLimit(limit));
  }

  @Override
  public long save(Study study) throws IOException {
    if (study.getId() == StudyImpl.UNSAVED_ID) {
      study.setLastModifier(authorizationManager.getCurrentUser());
      study.setStudyType(studyStore.getType(study.getStudyType().getId()));
      study.setProject(projectStore.get(study.getProject().getId()));
      study.setName(LimsUtils.generateTemporaryName());
      long id = studyStore.save(study);
      try {
        study.setName(namingScheme.generateNameFor(study));
        LimsUtils.validateNameOrThrow(study, namingScheme);
        studyStore.save(study);
      } catch (MisoNamingException e) {
        throw new IOException(e);
      }
      return id;
    } else {
      Study original = studyStore.get(study.getId());
      authorizationManager.throwIfNotWritable(original);
      original.setAccession(study.getAccession());
      original.setAlias(study.getAlias());
      original.setDescription(study.getDescription());
      original.setLastModifier(authorizationManager.getCurrentUser());
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

}
