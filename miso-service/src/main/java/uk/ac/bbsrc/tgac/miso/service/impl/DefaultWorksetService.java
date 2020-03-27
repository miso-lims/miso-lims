package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
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
    loadMembers(workset);
    validateChange(workset, null);
    workset.setChangeDetails(authorizationManager.getCurrentUser());
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

  private void loadMembers(Workset newWorkflow) {
    newWorkflow.setSamples(loadMembers("Sample", newWorkflow.getSamples(), WhineyFunction.rethrow(id -> sampleService.get(id))));
    newWorkflow.setLibraries(loadMembers("Library", newWorkflow.getLibraries(), WhineyFunction.rethrow(id -> libraryService.get(id))));
    newWorkflow.setLibraryAliquots(
        loadMembers("Library Aliquot", newWorkflow.getLibraryAliquots(), WhineyFunction.rethrow(id -> libraryAliquotService.get(id))));
  }

  private <T extends Identifiable> Set<T> loadMembers(String typeName, Collection<T> items, Function<Long, T> getter) {
    Set<T> members = new HashSet<>();
    for (T item : items) {
      T member = getter.apply(item.getId());
      if (member == null) {
        throw new IllegalArgumentException(String.format("%s %d not found", typeName, item.getId()));
      }
      members.add(member);
    }
    return members;
  }

  private void applyChanges(Workset from, Workset to) throws IOException {
    to.setAlias(from.getAlias());
    to.setDescription(from.getDescription());
    applyMemberChanges(from, to);
  }

  private void applyMemberChanges(Workset changed, Workset managed) {
    applyMemberChanges(changed.getSamples(), managed.getSamples(), WhineyFunction.rethrow(ids -> sampleService.listByIdList(ids)));
    applyMemberChanges(changed.getLibraries(), managed.getLibraries(), WhineyFunction.rethrow(ids -> libraryService.listByIdList(ids)));
    applyMemberChanges(changed.getLibraryAliquots(), managed.getLibraryAliquots(), WhineyFunction.rethrow(ids -> libraryAliquotService.listByIdList(ids)));
  }

  private <T extends Identifiable> void applyMemberChanges(Set<T> changed, Set<T> managed,
      Function<List<Long>, Collection<T>> getter) {
    Set<Long> oldIds = managed.stream().map(Identifiable::getId).collect(Collectors.toSet());
    Set<Long> newIds = changed.stream().map(Identifiable::getId).collect(Collectors.toSet());
    Set<Long> removed = oldIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());
    List<Long> added = newIds.stream().filter(id -> !oldIds.contains(id)).collect(Collectors.toList());

    managed.removeIf(item -> removed.contains(Long.valueOf(item.getId())));

    Collection<T> addedMembers = getter.apply(added);
    if (addedMembers.size() != added.size()) {
      throw new IllegalArgumentException("One or more added items not found");
    }
    managed.addAll(addedMembers);
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
  public void moveSamples(Workset from, Workset to, Collection<Sample> items) throws IOException {
    moveItems(from, to, items, Workset::getSamples, "samples");
  }

  @Override
  public void moveLibraries(Workset from, Workset to, Collection<Library> items) throws IOException {
    moveItems(from, to, items, Workset::getLibraries, "libraries");
  }

  @Override
  public void moveLibraryAliquots(Workset from, Workset to, Collection<LibraryAliquot> items) throws IOException {
    moveItems(from, to, items, Workset::getLibraryAliquots, "library aliquots");
  }

  public <T extends Identifiable> void moveItems(Workset from, Workset to, Collection<T> items, Function<Workset, Set<T>> getter,
      String pluralTypeLabel) throws IOException {
    if (from.getId() == to.getId()) {
      throw new ValidationException(String.format("Trying to move %s from the same workset", pluralTypeLabel));
    }
    Set<T> sourceItems = getter.apply(from);
    int initialSize = sourceItems.size();
    Set<Long> itemIds = items.stream().map(Identifiable::getId).collect(Collectors.toSet());
    sourceItems.removeIf(item -> itemIds.contains(item.getId()));
    if (initialSize - sourceItems.size() != items.size()) {
      throw new ValidationException(String.format("Not all %s were found in source workset", pluralTypeLabel));
    }
    getter.apply(to).addAll(items);
    worksetStore.save(to);
    worksetStore.save(from);
  }

}
