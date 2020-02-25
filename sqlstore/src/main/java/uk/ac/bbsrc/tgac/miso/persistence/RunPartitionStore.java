package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;

public interface RunPartitionStore {

  public RunPartition get(Run run, Partition partition) throws IOException;

  public void create(RunPartition runPartition) throws IOException;

  public void update(RunPartition runPartition) throws IOException;

  public void deleteForRun(Run run) throws IOException;

}
