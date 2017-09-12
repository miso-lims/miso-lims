package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface ContainerService extends PaginatedDataSource<SequencerPartitionContainer> {

  void applyChanges(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException;

  SequencerPartitionContainer get(long containerId) throws IOException, AuthorizationException;

  List<SequencerPartitionContainer> list() throws IOException;

  Collection<SequencerPartitionContainer> listByBarcode(String barcode) throws IOException;

  SequencerPartitionContainer save(SequencerPartitionContainer container) throws IOException;

  SequencerPartitionContainer create(SequencerPartitionContainer container) throws IOException;

  SequencerPartitionContainer update(SequencerPartitionContainer container) throws IOException;

  void delete(Long containerId) throws IOException;

  Collection<SequencerPartitionContainer> listByRunId(long runId) throws IOException;

  Partition getPartition(long partitionId) throws IOException;

  void update(Partition partition) throws IOException;

}
