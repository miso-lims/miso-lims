package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.KitService;
import uk.ac.bbsrc.tgac.miso.persistence.KitStore;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultKitService implements KitService {

  @Autowired
  private KitStore kitStore;
  @Autowired
  private AuthorizationManager authorizationManager;

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public List<Kit> list() throws IOException {
    return kitStore.listAll();
  }

  @Override
  public void deleteKitNote(Kit kit, Long noteId) throws IOException {
    if (noteId == null) {
      throw new IllegalArgumentException("Cannot delete an unsaved Note");
    }
    Kit managed = kitStore.get(kit.getId());
    Note deleteNote = null;
    for (Note note : managed.getNotes()) {
      if (note.getId() == noteId.longValue()) {
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
      Kit original = get(kit.getId());
      original.setIdentificationBarcode(kit.getIdentificationBarcode());
      original.setKitDate(kit.getKitDate());
      original.setKitDescriptor(kitStore.getKitDescriptorById(kit.getKitDescriptor().getId()));
      original.setLocationBarcode(kit.getLocationBarcode());
      original.setLotNumber(kit.getLotNumber());
      kit = original;
    }
    return kitStore.save(kit);
  }

  @Override
  public Kit get(long kitId) throws IOException {
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

}
