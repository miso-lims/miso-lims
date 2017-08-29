package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

public interface PartitionQcStore {

  Collection<PartitionQCType> listTypes() throws IOException;

  PartitionQC get(Run run, Partition partition) throws IOException;

  PartitionQCType getType(long qcTypeId) throws IOException;

  void create(PartitionQC qc) throws IOException;

  void update(PartitionQC managedQc) throws IOException;

}
