package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;

public interface SequencerServiceRecordStore extends Store<SequencerServiceRecord>, Remover<SequencerServiceRecord> {
  
  Collection<SequencerServiceRecord> listBySequencerId(long referenceId);
  
}
