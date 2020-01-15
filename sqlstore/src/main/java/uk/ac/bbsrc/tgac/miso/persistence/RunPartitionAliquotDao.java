package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;

public interface RunPartitionAliquotDao {

  public RunPartitionAliquot get(Run run, Partition partition, LibraryAliquot aliquot) throws IOException;

  public List<RunPartitionAliquot> listByRunId(long runId) throws IOException;

  public void create(RunPartitionAliquot runPartitionAliquot) throws IOException;

  public void update(RunPartitionAliquot runPartitionAliquot) throws IOException;

}
