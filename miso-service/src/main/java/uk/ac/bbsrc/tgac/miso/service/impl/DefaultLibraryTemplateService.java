package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryTemplateService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.BoxUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryTemplateStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultLibraryTemplateService implements LibraryTemplateService {

  protected static final Logger log = LoggerFactory.getLogger(DefaultLibraryTemplateService.class);

  @Autowired
  private LibraryTemplateStore libraryTemplateStore;
  @Autowired
  private IndexService indexService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

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
    target.setVolumeUnits(source.getVolumeUnits());
    applyProjectChanges(target.getProjects(), source.getProjects());
    if (target instanceof DetailedLibraryTemplate) {
      DetailedLibraryTemplate dSource = (DetailedLibraryTemplate) source;
      DetailedLibraryTemplate dTarget = (DetailedLibraryTemplate) target;
      dTarget.setLibraryDesign(dSource.getLibraryDesign());
      dTarget.setLibraryDesignCode(dSource.getLibraryDesignCode());
    }
    applyIndexChanges(target.getIndexOnes(), source.getIndexOnes());
    applyIndexChanges(target.getIndexTwos(), source.getIndexTwos());
  }

  private void applyProjectChanges(List<Project> toProjects, List<Project> fromProjects) {
    toProjects.removeIf(toProject -> fromProjects.stream()
        .noneMatch(fromProject -> fromProject.getId() == toProject.getId()));
    fromProjects.forEach(fromProject -> {
      if (toProjects.stream().noneMatch(toProject -> toProject.getId() == fromProject.getId())) {
        toProjects.add(fromProject);
      }
    });
  }

  private void applyIndexChanges(Map<String, Index> toIndices, Map<String, Index> fromIndices) {
    for (Iterator<String> iterator = toIndices.keySet().iterator(); iterator.hasNext();) {
      if (!fromIndices.containsKey(iterator.next())) {
        iterator.remove();
      }
    }
    fromIndices.forEach((key, value) -> {
      if (!toIndices.containsKey(key) || toIndices.get(key).getId() != value.getId()) {
        toIndices.put(key, value);
      }
    });
  }

  @Override
  public void update(LibraryTemplate template) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    LibraryTemplate managed = get(template.getId());
    loadChildEntities(template);
    validateChange(template, managed);
    applyChanges(managed, template);
    libraryTemplateStore.update(managed);
  }

  @Override
  public Long create(LibraryTemplate libraryTemplate) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    loadChildEntities(libraryTemplate);
    validateChange(libraryTemplate, null);
    return libraryTemplateStore.create(libraryTemplate);
  }

  private void loadChildEntities(LibraryTemplate template) throws IOException {
    loadIndices(template.getIndexOnes());
    loadIndices(template.getIndexTwos());
  }

  private void loadIndices(Map<String, Index> indices) throws IOException {
    Map<String, Index> loaded = new HashMap<>();
    for (String boxPosition : indices.keySet()) {
      Index index = indices.get(boxPosition);
      Index managedIndex = indexService.get(index.getId());
      if (managedIndex == null) {
        throw new ValidationException(new ValidationError("No index found with ID: " + index.getId()));
      }
      loaded.put(boxPosition, managedIndex);
    }
    indices.putAll(loaded);
  }

  private void validateChange(LibraryTemplate template, LibraryTemplate beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    if (template.getDefaultVolume() != null && template.getVolumeUnits() == null) {
      errors.add(new ValidationError("volumeUnits", "Units must be specified when a default volume is set"));
    }

    if (template.getIndexFamily() == null) {
      if ((!template.getIndexOnes().isEmpty() || !template.getIndexTwos().isEmpty())) {
        errors.add(new ValidationError("Cannot save indices without an index family selected"));
      }
    } else {
      validateIndices(template.getIndexFamily(), template.getIndexOnes(), "ones", errors);
      validateIndices(template.getIndexFamily(), template.getIndexTwos(), "twos", errors);
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateIndices(IndexFamily family, Map<String, Index> indices, String position, List<ValidationError> errors) {
    for (String boxPosition : indices.keySet()) {
      if (!BoxUtils.isValidBoxPosition(boxPosition)) {
        errors.add(new ValidationError("Invalid box position: " + boxPosition));
      }
    }
    for (Index index : indices.values()) {
      if (index.getFamily().getId() != family.getId()) {
        errors.add(new ValidationError(String.format("Index %s must all belong to the selected index family", position)));
        break;
      }
    }
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
  public List<LibraryTemplate> list() throws IOException {
    return libraryTemplateStore.list();
  }

  @Override
  public List<LibraryTemplate> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    authorizationManager.throwIfUnauthenticated();
    return libraryTemplateStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public List<LibraryTemplate> listLibraryTemplatesForProject(long projectId) throws IOException {
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
