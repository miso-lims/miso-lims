package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

public interface RunPartitionStore {

  RunPartition get(Run run, Partition partition) throws IOException;

  void create(RunPartition runPartition) throws IOException;

  void update(RunPartition runPartition) throws IOException;

}
