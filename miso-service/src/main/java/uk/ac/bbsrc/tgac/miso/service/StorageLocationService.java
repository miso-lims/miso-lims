package uk.ac.bbsrc.tgac.miso.service;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;

public interface StorageLocationService {

  public StorageLocation get(long id);

  public StorageLocation getByBarcode(String barcode);

  public List<StorageLocation> listFreezers();

}
