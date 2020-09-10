package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.changelog.WorksetChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetItem;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.ProviderService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.ListWorksetViewStore;
import uk.ac.bbsrc.tgac.miso.persistence.WorksetStore;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorksetService implements WorksetService {

  @Autowired
  private WorksetStore worksetStore;
  @Autowired
  private ListWorksetViewStore listWorksetViewStore;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryAliquotService libraryAliquotService;
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setWorksetStore(WorksetStore worksetStore) {
    this.worksetStore = worksetStore;
  }

  public void setListWorksetViewStore(ListWorksetViewStore listWorksetViewStore) {
    this.listWorksetViewStore = listWorksetViewStore;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setLibraryAliquotService(LibraryAliquotService libraryAliquotService) {
    this.libraryAliquotService = libraryAliquotService;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return listWorksetViewStore.count(errorHandler, filter);
  }

  @Override
  public List<ListWorksetView> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return listWorksetViewStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public Workset get(long id) throws IOException {
    return worksetStore.get(id);
  }

  @Override
  public List<ListWorksetView> listBySearch(String query) throws IOException {
    return listWorksetViewStore.listBySearch(query);
  }

  @Override
  public List<Workset> listBySample(long sampleId) throws IOException {
    return worksetStore.listBySample(sampleId);
  }

  @Override
  public List<Workset> listByLibrary(long libraryId) throws IOException {
    return worksetStore.listByLibrary(libraryId);
  }

  @Override
  public List<Workset> listByLibraryAliquot(long aliquotId) throws IOException {
    return worksetStore.listByLibraryAliquot(aliquotId);
  }

  @Override
  public long create(Workset workset) throws IOException {
    workset.setChangeDetails(authorizationManager.getCurrentUser());
    loadMembers(workset, workset.getLastModified());
    validateChange(workset, null);
    return worksetStore.save(workset);
  }

  @Override
  public long update(Workset workset) throws IOException {
    Workset managed = worksetStore.get(workset.getId());
    validateChange(workset, managed);
    applyChanges(workset, managed);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    return worksetStore.save(managed);
  }

  private void loadMembers(Workset newWorkset, Date timestamp) throws IOException {
    loadMembers(newWorkset, newWorkset.getWorksetSamples(), sampleService, timestamp);
    loadMembers(newWorkset, newWorkset.getWorksetLibraries(), libraryService, timestamp);
    loadMembers(newWorkset, newWorkset.getWorksetLibraryAliquots(), libraryAliquotService, timestamp);
  }

  private <T extends Boxable, J extends WorksetItem<T>> void loadMembers(Workset workset, Collection<J> worksetItems,
      ProviderService<T> service, Date timestamp) throws IOException {
    for (J worksetItem : worksetItems) {
      T item = null;
      if (worksetItem.getItem() != null && worksetItem.getItem().isSaved()) {
        item = service.get(worksetItem.getItem().getId());
      }
      if (item == null) {
        throw new IllegalArgumentException("Workset item must be an existing item");
      }
      worksetItem.setItem(item);
      worksetItem.setWorkset(workset);
      worksetItem.setAddedTime(timestamp);
    }
  }

  private void applyChanges(Workset from, Workset to) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
  }

  private void validateChange(Workset workset, Workset beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if ((beforeChange == null || !workset.getAlias().equals(beforeChange.getAlias()))
        && worksetStore.getByAlias(workset.getAlias()) != null) {
      errors.add(new ValidationError("alias", "There is already a workset with this alias"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
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
  public void authorizeDeletion(Workset workset) throws IOException {
    authorizationManager.throwIfNonAdminOrMatchingOwner(workset.getCreator());
  }

  @Override
  public void addSamples(Workset workset, Collection<Sample> items) throws IOException {
    addItems(workset, null, items, Workset::getWorksetSamples, WorksetSample::new, sampleService, Pluralizer.samples(items.size()));
  }

  @Override
  public void addLibraries(Workset workset, Collection<Library> items) throws IOException {
    addItems(workset, null, items, Workset::getWorksetLibraries, WorksetLibrary::new, libraryService, Pluralizer.libraries(items.size()));
  }

  @Override
  public void addLibraryAliquots(Workset workset, Collection<LibraryAliquot> items) throws IOException {
    addItems(workset, null, items, Workset::getWorksetLibraryAliquots, WorksetLibraryAliquot::new, libraryAliquotService,
        Pluralizer.libraryAliquots(items.size()));
  }

  @Override
  public void removeSamples(Workset workset, Collection<Sample> items) throws IOException {
    removeItems(workset, null, items, Workset::getWorksetSamples, Pluralizer.samples(items.size()));
  }

  @Override
  public void removeLibraries(Workset workset, Collection<Library> items) throws IOException {
    removeItems(workset, null, items, Workset::getWorksetLibraries, Pluralizer.libraries(items.size()));
  }

  @Override
  public void removeLibraryAliquots(Workset workset, Collection<LibraryAliquot> items) throws IOException {
    removeItems(workset, null, items, Workset::getWorksetLibraryAliquots, Pluralizer.libraryAliquots(items.size()));
  }

  @Override
  public void moveSamples(Workset from, Workset to, Collection<Sample> items) throws IOException {
    moveItems(from, to, items, Pluralizer.samples(items.size()), Workset::getWorksetSamples, WorksetSample::new, sampleService);
  }

  @Override
  public void moveLibraries(Workset from, Workset to, Collection<Library> items) throws IOException {
    moveItems(from, to, items, Pluralizer.libraries(items.size()), Workset::getWorksetLibraries, WorksetLibrary::new, libraryService);
  }

  @Override
  public void moveLibraryAliquots(Workset from, Workset to, Collection<LibraryAliquot> items) throws IOException {
    moveItems(from, to, items, Pluralizer.libraryAliquots(items.size()), Workset::getWorksetLibraryAliquots, WorksetLibraryAliquot::new,
        libraryAliquotService);
  }

  private <T extends Boxable, J extends WorksetItem<T>> void addItems(Workset toWorkset, Workset fromWorkset, Collection<T> items,
      Function<Workset, Set<J>> getter, Supplier<J> constructor, ProviderService<T> service, String typeLabel)
      throws IOException {
    Date now = new Date();
    Set<J> worksetItems = getter.apply(toWorkset);
    for (T item : items) {
      T managedItem = service.get(item.getId());
      if (managedItem == null) {
        throw new ValidationException(String.format("%s %d not found", item.getEntityType().getLabel(), item.getId()));
      }
      if (worksetItems.stream().anyMatch(worksetItem -> worksetItem.getItem().getId() == managedItem.getId())) {
        throw new ValidationException(String.format("Workset already contains %s (%s)", managedItem.getAlias(), managedItem.getName()));
      }
      worksetItems.add(makeWorksetItem(constructor, managedItem, toWorkset, now));
    }
    toWorkset.setChangeDetails(authorizationManager.getCurrentUser());
    worksetStore.save(toWorkset);
    String actionMessage = fromWorkset == null ? String.format("Added %s", typeLabel)
        : String.format("Added %s from workset '%s'", typeLabel, fromWorkset.getAlias());
    addChangeLogForItems(toWorkset, items, actionMessage, typeLabel);
  }

  private <T extends Boxable, J extends WorksetItem<T>> void removeItems(Workset fromWorkset, Workset toWorkset, Collection<T> items,
      Function<Workset, Set<J>> getter, String typeLabel) throws IOException {
    Set<J> worksetItems = getter.apply(fromWorkset);
    for (T item : items) {
      if (!worksetItems.removeIf(worksetItem -> worksetItem.getItem().getId() == item.getId())) {
        throw new ValidationException(String.format("%s %s not found in workset", item.getEntityType().getLabel(), item.getId()));
      }
    }
    fromWorkset.setChangeDetails(authorizationManager.getCurrentUser());
    worksetStore.save(fromWorkset);
    String actionMessage = toWorkset == null ? String.format("Removed %s", typeLabel)
        : String.format("Removed %s to workset '%s'", typeLabel, toWorkset.getAlias());
    addChangeLogForItems(fromWorkset, items, actionMessage, typeLabel);
  }

  private <T extends Boxable, J extends WorksetItem<T>> void moveItems(Workset from, Workset to, Collection<T> items,
      String typeLabel, Function<Workset, Set<J>> getter, Supplier<J> constructor, ProviderService<T> service) throws IOException {
    removeItems(from, to, items, getter, typeLabel);
    addItems(to, from, items, getter, constructor, service, typeLabel);
  }

  private static <T extends Boxable, J extends WorksetItem<T>> J makeWorksetItem(Supplier<J> constructor, T item, Workset workset,
      Date addedTime) {
    J worksetItem = constructor.get();
    worksetItem.setItem(item);
    worksetItem.setWorkset(workset);
    worksetItem.setAddedTime(addedTime);
    return worksetItem;
  }

  private <T extends Boxable> void addChangeLogForItems(Workset workset, Collection<T> items, String actionMessage, String typeLabel)
      throws IOException {
    String message = String.format("%s: %s", actionMessage, items.stream()
        .map(item -> String.format("%s (%s)", item.getAlias(), item.getName()))
        .collect(Collectors.joining(", ")));

    WorksetChangeLog change = new WorksetChangeLog();
    change.setWorkset(workset);
    change.setTime(workset.getLastModified());
    change.setUser(authorizationManager.getCurrentUser());
    change.setColumnsChanged(typeLabel);
    change.setSummary(message);
    changeLogService.create(change);
  }

  @Override
  public Map<Long, Date> getSampleAddedTimes(long worksetId) throws IOException {
    return worksetStore.getSampleAddedTimes(worksetId);
  }

  @Override
  public Map<Long, Date> getLibraryAddedTimes(long worksetId) throws IOException {
    return worksetStore.getLibraryAddedTimes(worksetId);
  }

  @Override
  public Map<Long, Date> getLibraryAliquotAddedTimes(long worksetId) throws IOException {
    return worksetStore.getLibraryAliquotAddedTimes(worksetId);
  }

}
