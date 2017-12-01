package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

public interface PartitionQCService {
  PartitionQC get(Run run, Partition partition) throws IOException;

  Collection<PartitionQCType> listTypes() throws IOException;

  void save(PartitionQC qc) throws IOException;

  PartitionQCType getType(long qcTypeId) throws IOException;

}
