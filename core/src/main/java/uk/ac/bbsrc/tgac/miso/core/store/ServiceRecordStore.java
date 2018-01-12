package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;

public interface ServiceRecordStore extends Store<ServiceRecord>, Remover<ServiceRecord> {
  
  /**
   * @param instrumentId ID of the Instrument to find ServiceRecords for
   * @return all Service Records for the specified Instrument
   */
  Collection<ServiceRecord> listByInstrumentId(long instrumentId);
  
  /**
   * @return a map containing all column names and max lengths from the Service Record table
   * @throws IOException
   */
  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException;
  
}
