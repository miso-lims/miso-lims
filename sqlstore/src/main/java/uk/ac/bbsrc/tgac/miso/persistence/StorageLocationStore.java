package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;


public interface StorageLocationStore {

  StorageLocation get(long id);

  StorageLocation getByBarcode(String barcode);

  StorageLocation getByProbeId(String probeId) throws IOException;

  List<StorageLocation> listRooms();

  List<StorageLocation> listFreezers();

  long save(StorageLocation location);

  StorageLocation getByServiceRecord(ServiceRecord record) throws IOException;

}
