package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;

public interface ContainerService extends BarcodableService<SequencerPartitionContainer>, DeleterService<SequencerPartitionContainer>,
    SaveService<SequencerPartitionContainer> {
  @Override
  default EntityType getEntityType() {
    return EntityType.CONTAINER;
  }

  void applyChanges(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException;

  Collection<SequencerPartitionContainer> listByBarcode(String barcode) throws IOException;

  SequencerPartitionContainer save(SequencerPartitionContainer container) throws IOException;

  Collection<SequencerPartitionContainer> listByRunId(long runId) throws IOException;

  Collection<Partition> listPartitionsByPoolId(long poolId) throws IOException;

  Partition getPartition(long partitionId) throws IOException;

  void update(Partition partition) throws IOException;

  List<PoreVersion> listPoreVersions() throws IOException;
}
