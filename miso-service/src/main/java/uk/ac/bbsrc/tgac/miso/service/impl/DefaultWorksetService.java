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
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.store.WorksetStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.WhineyFunction;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Service
@Transactional(rollbackFor = Exception.class)
public class DefaultWorksetService implements WorksetService {

  @Autowired
  private WorksetStore worksetStore;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private LibraryDilutionService dilutionService;
  @Autowired
  private DeletionStore deletionStore;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setWorksetStore(WorksetStore worksetStore) {
    this.worksetStore = worksetStore;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setDilutionService(LibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
  }

  public void setDeletionStore(DeletionStore deletionStore) {
    this.deletionStore = deletionStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return worksetStore.count(errorHandler, filter);
  }

  @Override
  public List<Workset> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return worksetStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public Workset get(long id) {
    return worksetStore.get(id);
  }

  @Override
  public List<Workset> listBySearch(String query) {
    return worksetStore.listBySearch(query);
  }

  @Override
  public List<Workset> listBySample(long sampleId) {
    return worksetStore.listBySample(sampleId);
  }

  @Override
  public List<Workset> listByLibrary(long libraryId) {
    return worksetStore.listByLibrary(libraryId);
  }

  @Override
  public List<Workset> listByDilution(long dilutionId) {
    return worksetStore.listByDilution(dilutionId);
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
    newWorkflow.setDilutions(loadMembers("Dilution", newWorkflow.getDilutions(), WhineyFunction.rethrow(id -> dilutionService.get(id))));
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
    applyMemberChanges(changed.getDilutions(), managed.getDilutions(), WhineyFunction.rethrow(ids -> dilutionService.listByIdList(ids)));
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

  private void validateChange(Workset workset, Workset beforeChange) {
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

}
