package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;

public interface SequencerServiceRecordStore extends Store<SequencerServiceRecord>, Remover<SequencerServiceRecord> {
  
  /**
   * @param referenceId ID of the SequencerReference to find SequencerServiceRecords for
   * @return all Service Records for the specified SequencerReference
   */
  Collection<SequencerServiceRecord> listBySequencerId(long referenceId);
  
  /**
   * @return a map containing all column names and max lengths from the Service Record table
   * @throws IOException
   */
  public Map<String, Integer> getServiceRecordColumnSizes() throws IOException;
  
}
