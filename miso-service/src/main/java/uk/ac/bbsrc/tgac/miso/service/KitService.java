package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;

public interface KitService extends ListService<Kit> {

  public void deleteKitNote(Kit kit, Long noteId) throws IOException;

  public void saveKitNote(Kit kit, Note note) throws IOException;

  public long saveKit(Kit kit) throws IOException;

  public Kit getKitByIdentificationBarcode(String barcode) throws IOException;

  public Kit getKitByLotNumber(String lotNumber) throws IOException;

}
