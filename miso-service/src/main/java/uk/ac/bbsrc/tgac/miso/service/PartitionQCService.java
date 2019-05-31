package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.Run;

public interface PartitionQCService {

  PartitionQC get(Run run, Partition partition) throws IOException;

  void save(PartitionQC qc) throws IOException;

}
