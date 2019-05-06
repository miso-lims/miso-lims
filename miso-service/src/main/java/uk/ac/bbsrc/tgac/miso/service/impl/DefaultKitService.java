package uk.ac.bbsrc.tgac.miso.service.impl;

import static uk.ac.bbsrc.tgac.miso.service.impl.ValidationUtils.isSetAndChanged;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultKitService implements KitService {

  @Autowired
  private KitStore kitStore;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public Collection<Kit> listKits() throws IOException {
    return kitStore.listAll();
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptors() throws IOException {
    return kitStore.listAllKitDescriptors();
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    return kitStore.listKitDescriptorsByType(kitType);
  }

  @Override
  public void deleteKitNote(Kit kit, Long noteId) throws IOException {
    if (noteId == null || noteId.equals(Note.UNSAVED_ID)) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Kit managed = kitStore.get(kit.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getNoteId().equals(noteId)) {
        deleteNote = note;
        break;
      }
    }
    if (deleteNote == null) {
      throw new IOException("Note " + noteId + " not found for Kit " + kit.getId());
    }
    authorizationManager.throwIfNonAdminOrMatchingOwner(deleteNote.getOwner());
    managed.getNotes().remove(deleteNote);
    kitStore.save(managed);
  }

  @Override
  public void saveKitNote(Kit kit, Note note) throws IOException {
    Kit managed = kitStore.get(kit.getId());
    note.setCreationDate(new Date());
    note.setOwner(authorizationManager.getCurrentUser());
    managed.addNote(note);
    kitStore.save(managed);
  }

  @Override
  public long saveKit(Kit kit) throws IOException {
    authorizationManager.throwIfNotInternal();
    if (kit.isSaved()) {
      Kit original = getKitById(kit.getId());
      original.setIdentificationBarcode(kit.getIdentificationBarcode());
      original.setKitDate(kit.getKitDate());
      original.setKitDescriptor(getKitDescriptorById(kit.getKitDescriptor().getId()));
      original.setLocationBarcode(kit.getLocationBarcode());
      original.setLotNumber(kit.getLotNumber());
      kit = original;
    }
    return kitStore.save(kit);
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException {
    authorizationManager.throwIfNonAdmin();
    if (kitDescriptor.isSaved()) {
      KitDescriptor original = getKitDescriptorById(kitDescriptor.getId());
      validateChange(kitDescriptor, original);
      original.setName(kitDescriptor.getName());
      original.setDescription(kitDescriptor.getDescription());
      original.setVersion(kitDescriptor.getVersion());
      original.setManufacturer(kitDescriptor.getManufacturer());
      original.setPartNumber(kitDescriptor.getPartNumber());
      original.setStockLevel(kitDescriptor.getStockLevel());
      original.setDescription(kitDescriptor.getDescription());
      kitDescriptor = original;
    } else {
      validateChange(kitDescriptor, null);
    }
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
  public long saveKitDescriptorTargetedSequencingRelationships(KitDescriptor kitDescriptor) throws IOException {
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
      List<LibraryDilution> affectedDilutions = kitStore.getDilutionsForKdTsRelationship(managed, ts);
      if (!affectedDilutions.isEmpty()) {
        errors.add(new ValidationError(
            String.format("Cannot unlink targeted sequencing '%s' from kit '%s' as %d dilutions already use this link.", ts.getAlias(),
                managed.getName(), affectedDilutions.size())));
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
  public Kit getKitById(long kitId) throws IOException {
    authorizationManager.throwIfNotInternal();
    return kitStore.get(kitId);
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    authorizationManager.throwIfNotInternal();
    return kitStore.getKitByIdentificationBarcode(barcode);
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    authorizationManager.throwIfNotInternal();
    return kitStore.getKitByLotNumber(lotNumber);
  }

  @Override
  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException {
    return kitStore.getKitDescriptorById(kitDescriptorId);
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
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

}
