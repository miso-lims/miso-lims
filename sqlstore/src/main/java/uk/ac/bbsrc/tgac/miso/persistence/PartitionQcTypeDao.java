package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;

public interface PartitionQcTypeDao extends SaveDao<PartitionQCType> {

  PartitionQCType getByDescription(String description) throws IOException;

  List<PartitionQCType> listByIdList(List<Long> idList) throws IOException;

  long getUsage(PartitionQCType type) throws IOException;

}
