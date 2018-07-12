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

}
