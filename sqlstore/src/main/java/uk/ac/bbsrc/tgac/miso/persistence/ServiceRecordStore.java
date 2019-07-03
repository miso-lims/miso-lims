package uk.ac.bbsrc.tgac.miso.persistence;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;

public interface ServiceRecordStore extends Store<ServiceRecord> {
  
  /**
   * @param instrumentId ID of the Instrument to find ServiceRecords for
   * @return all Service Records for the specified Instrument
   */
  Collection<ServiceRecord> listByInstrumentId(long instrumentId);
  
}
