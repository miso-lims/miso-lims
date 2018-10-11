package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryTemplateStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.service.ProjectService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryTemplateService implements LibraryTemplateService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultLibraryTemplateService.class);

  @Autowired
  private LibraryTemplateStore libraryTemplateStore;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private ProjectService projectService;

  public void setLibraryTemplateStore(LibraryTemplateStore libraryTemplateStore) {
    this.libraryTemplateStore = libraryTemplateStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public LibraryTemplate get(long id) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    LibraryTemplate libraryTemplate = libraryTemplateStore.get(id);
    return libraryTemplate;
  }

  private void applyChanges(LibraryTemplate target, LibraryTemplate source) {
    target.setAlias(source.getAlias());
    target.setPlatformType(source.getPlatformType());
    target.setLibraryType(source.getLibraryType());
    target.setLibrarySelectionType(source.getLibrarySelectionType());
    target.setLibraryStrategyType(source.getLibraryStrategyType());
    target.setKitDescriptor(source.getKitDescriptor());
    target.setIndexFamily(source.getIndexFamily());
    target.setDefaultVolume(source.getDefaultVolume());
    target.setProjects(source.getProjects());
    if (target instanceof DetailedLibraryTemplate) {
      DetailedLibraryTemplate dSource = (DetailedLibraryTemplate) source;
      DetailedLibraryTemplate dTarget = (DetailedLibraryTemplate) target;
      dTarget.setLibraryDesign(dSource.getLibraryDesign());
      dTarget.setLibraryDesignCode(dSource.getLibraryDesignCode());
    }
  }

  @Override
  public void update(LibraryTemplate oldLibraryTemplate) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    LibraryTemplate updatedLibraryTemplate = get(oldLibraryTemplate.getId());
    applyChanges(updatedLibraryTemplate, oldLibraryTemplate);
    libraryTemplateStore.update(updatedLibraryTemplate);
  }

  @Override
  public Long create(LibraryTemplate libraryTemplate) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return libraryTemplateStore.create(libraryTemplate);
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return libraryTemplateStore.count(errorHandler, filter);
  }

  @Override
  public List<LibraryTemplate> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return libraryTemplateStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<LibraryTemplate> listLibraryTemplatesForProject(long projectId) throws IOException {
    authorizationManager.throwIfNotReadable(projectService.get(projectId));
    return libraryTemplateStore.listLibraryTemplatesForProject(projectId);
  }

  @Override
  public List<LibraryTemplate> listByIdList(List<Long> idList) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return new ArrayList<>(libraryTemplateStore.getByIdList(idList));
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public void authorizeDeletion(LibraryTemplate object) throws IOException {
    authorizationManager.throwIfUnauthenticated();
  }

}
