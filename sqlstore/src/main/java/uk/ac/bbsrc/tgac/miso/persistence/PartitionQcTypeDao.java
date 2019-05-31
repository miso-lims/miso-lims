package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;

public interface PartitionQcTypeDao {

  public PartitionQCType get(long id) throws IOException;

  public PartitionQCType getByDescription(String description) throws IOException;

  public List<PartitionQCType> list() throws IOException;

  public long create(PartitionQCType type) throws IOException;

  public long update(PartitionQCType type) throws IOException;

  public long getUsage(PartitionQCType type) throws IOException;

}
