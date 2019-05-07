package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;

public interface KitService extends ProviderService<Kit> {

  public Collection<Kit> listKits() throws IOException;

  public void deleteKitNote(Kit kit, Long noteId) throws IOException;

  public void saveKitNote(Kit kit, Note note) throws IOException;

  public long saveKit(Kit kit) throws IOException;

  public Kit getKitByIdentificationBarcode(String barcode) throws IOException;

  public Kit getKitByLotNumber(String lotNumber) throws IOException;

}
