package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

public interface RunPartitionService {

  RunPartition get(Run run, Partition partition) throws IOException;

  void save(RunPartition runPartition) throws IOException;

}
