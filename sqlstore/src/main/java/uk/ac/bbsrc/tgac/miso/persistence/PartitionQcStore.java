package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

public interface PartitionQcStore {

  PartitionQC get(Run run, Partition partition) throws IOException;

  void create(PartitionQC qc) throws IOException;

  void update(PartitionQC managedQc) throws IOException;

}
