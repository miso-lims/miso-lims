package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;

public interface RunPartitionStore extends ProviderDao<RunPartition> {

  public RunPartition get(long runId, long partitionId) throws IOException;

  public void create(RunPartition runPartition) throws IOException;

  public void update(RunPartition runPartition) throws IOException;

  public void deleteForRun(Run run) throws IOException;

  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException;

}
