package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;

public interface StorageLocationService {

  public StorageLocation get(long id);

  public StorageLocation getByBarcode(String barcode);

  public List<StorageLocation> listRooms();

  public List<StorageLocation> listFreezers();

  public Map<String, Integer> getColumnSizes() throws IOException;

  public long createRoom(StorageLocation room) throws IOException;

  public long saveFreezer(StorageLocation freezer) throws IOException;

  public long addFreezerStorage(StorageLocation storage) throws IOException;

  public long updateStorageComponent(StorageLocation location) throws IOException;

  /**
   * Gets a freezer associated with a given barcode. If the given barcode is associated with a storage component inside
   * the freezer, the freezer is returned. If the given barcode is associated with a room, no StorageLocation is returned.
   * 
   * @param barcode of interest
   * @return StorageLocation freezer identified by or containing storage component identified by the given barcode
   */
  public StorageLocation getFreezerForBarcodedStorageLocation(String barcode);

}
