package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocation;


public interface StorageLocationStore extends SaveDao<StorageLocation> {

  StorageLocation getByBarcode(String barcode);

  StorageLocation getByProbeId(String probeId) throws IOException;

  List<StorageLocation> listRooms();

  List<StorageLocation> listFreezers();

  StorageLocation getByServiceRecord(ServiceRecord record) throws IOException;

}
