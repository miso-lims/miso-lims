package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;

public interface StorageLocationStore {

  public StorageLocation get(long id);

  public StorageLocation getByBarcode(String barcode);

  public List<StorageLocation> listRooms();

  public List<StorageLocation> listFreezers();

  public long save(StorageLocation location);

}
