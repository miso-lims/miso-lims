package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;

public interface SequencingControlTypeDao extends BulkSaveDao<SequencingControlType> {

  SequencingControlType getByAlias(String alias) throws IOException;

  long getUsage(SequencingControlType sequencingControlType) throws IOException;

}
