package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;

public interface StorageLocationStore {

  public StorageLocation get(long id);

  public StorageLocation getByBarcode(String barcode);

  public List<StorageLocation> listRooms();

  public List<StorageLocation> listFreezers();

  public Map<String, Integer> getColumnSizes() throws IOException;

  public long save(StorageLocation location);

}
