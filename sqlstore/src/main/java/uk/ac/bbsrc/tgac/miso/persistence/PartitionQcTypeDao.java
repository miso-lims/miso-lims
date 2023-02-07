package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;

public interface PartitionQcTypeDao extends BulkSaveDao<PartitionQCType> {

  PartitionQCType getByDescription(String description) throws IOException;

  long getUsage(PartitionQCType type) throws IOException;

}
