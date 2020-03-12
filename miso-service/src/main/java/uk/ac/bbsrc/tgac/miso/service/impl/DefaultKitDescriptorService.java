package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isSetAndChanged;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.core.util.Pluralizer;
import uk.ac.bbsrc.tgac.miso.persistence.KitStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultKitDescriptorService implements KitDescriptorService {

  @Autowired
  private KitStore kitStore;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private AuthorizationManager authorizationManager;
  @Autowired
  private DeletionStore deletionStore;

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public List<KitDescriptor> list() throws IOException {
    return kitStore.listAllKitDescriptors();
  }

  @Override
  public Collection<KitDescriptor> listByType(KitType kitType) throws IOException {
    return kitStore.listKitDescriptorsByType(kitType);
  }

  @Override
  public long create(KitDescriptor kitDescriptor) throws IOException {
    authorizationManager.throwIfNonAdmin();
    validateChange(kitDescriptor, null);
    kitDescriptor.setChangeDetails(authorizationManager.getCurrentUser());
    return kitStore.saveKitDescriptor(kitDescriptor);
  }

  @Override
  public long update(KitDescriptor kitDescriptor) throws IOException {
    authorizationManager.throwIfNonAdmin();
    KitDescriptor original = get(kitDescriptor.getId());
    validateChange(kitDescriptor, original);
    original.setName(kitDescriptor.getName());
    original.setDescription(kitDescriptor.getDescription());
    original.setVersion(kitDescriptor.getVersion());
    original.setManufacturer(kitDescriptor.getManufacturer());
    original.setPartNumber(kitDescriptor.getPartNumber());
    original.setStockLevel(kitDescriptor.getStockLevel());
    original.setDescription(kitDescriptor.getDescription());
    kitDescriptor = original;
    kitDescriptor.setChangeDetails(authorizationManager.getCurrentUser());
    return kitStore.saveKitDescriptor(kitDescriptor);
  }

  private void validateChange(KitDescriptor kitDescriptor, KitDescriptor beforeChange) throws IOException {
    List<ValidationError> errors = new ArrayList<>();

    if (isSetAndChanged(KitDescriptor::getName, kitDescriptor, beforeChange)) {
      if (kitStore.getKitDescriptorByName(kitDescriptor.getName()) != null) {
        errors.add(new ValidationError("name", "There is already a kit descriptor with this name"));
      }
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  @Override
  public long saveTargetedSequencingRelationships(KitDescriptor kitDescriptor) throws IOException {
    authorizationManager.throwIfNonAdmin();
    KitDescriptor managed = kitStore.getKitDescriptorById(kitDescriptor.getId());
    if (managed == null) {
      throw new IllegalArgumentException("Cannot change kit descriptor-targeted sequencing relationship when kit does not exist");
    }
    validateTargetedSequencingChange(managed, kitDescriptor);
    managed.clearTargetedSequencing();
    for (TargetedSequencing ts : kitDescriptor.getTargetedSequencing()) {
      managed.addTargetedSequencing(ts);
    }
    loadChildEntities(managed);
    managed.setChangeDetails(authorizationManager.getCurrentUser());
    return kitStore.saveKitDescriptor(managed);
  }

  private void validateTargetedSequencingChange(KitDescriptor managed, KitDescriptor changes) {
    List<ValidationError> errors = new ArrayList<>();
    Collection<TargetedSequencing> removed = CollectionUtils.subtract(managed.getTargetedSequencing(), changes.getTargetedSequencing());
    for (TargetedSequencing ts : removed) {
      List<LibraryAliquot> affectedAliquots = kitStore.getLibraryAliquotsForKdTsRelationship(managed, ts);
      if (!affectedAliquots.isEmpty()) {
        errors.add(new ValidationError(
            String.format("Cannot unlink targeted sequencing '%s' from kit '%s' as %d %s already use this link.", ts.getAlias(),
                managed.getName(), affectedAliquots.size(), Pluralizer.libraryAliquots(affectedAliquots.size()))));
      }
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void loadChildEntities(KitDescriptor kitDescriptor) throws IOException {
    Set<TargetedSequencing> maybeManaged = Sets.newHashSet(kitDescriptor.getTargetedSequencing());
    kitDescriptor.clearTargetedSequencing();
    for (TargetedSequencing ts : maybeManaged) {
      if (ts != null && ts.isSaved()) {
        kitDescriptor.addTargetedSequencing(targetedSequencingService.get(ts.getId()));
      }
    }
  }

  @Override
  public KitDescriptor get(long kitDescriptorId) throws IOException {
    return kitStore.getKitDescriptorById(kitDescriptorId);
  }

  @Override
  public KitDescriptor getByName(String name) throws IOException {
    return kitStore.getKitDescriptorByName(name);
  }

  @Override
  public KitDescriptor getByPartNumber(String partNumber) throws IOException {
    return kitStore.getKitDescriptorByPartNumber(partNumber);
  }

  @Override
  public List<KitDescriptor> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter) throws IOException {
    return kitStore.list(errorHandler, offset, limit, sortDir, sortCol, filter);
  }

  @Override
  public long count(Consumer<String> errorHandler, PaginationFilter... filter) throws IOException {
    return kitStore.count(errorHandler, filter);
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
  public ValidationResult validateDeletion(KitDescriptor object) throws IOException {
    ValidationResult result = new ValidationResult();
    long libUsage = kitStore.getUsageByLibraries(object);
    if (libUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, libUsage, Pluralizer.libraries(libUsage)));
    }
    long containerUsage = kitStore.getUsageByContainers(object);
    if (containerUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, containerUsage, "sequencing " + Pluralizer.containers(containerUsage)));
    }
    long runUsage = kitStore.getUsageByRuns(object);
    if (runUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, runUsage, "sequencing " + Pluralizer.runs(runUsage)));
    }
    long qcTypeUsage = kitStore.getUsageByQcTypes(object);
    if (qcTypeUsage > 0) {
      result.addError(ValidationError.forDeletionUsage(object, qcTypeUsage, "QC " + Pluralizer.types(qcTypeUsage)));
    }
    return result;
  }

  @Override
  public List<KitDescriptor> search(KitType type, String search) throws IOException {
    return kitStore.search(type, search);
  }

}
