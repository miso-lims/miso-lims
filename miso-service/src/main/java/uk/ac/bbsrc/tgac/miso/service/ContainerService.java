package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FlowCellVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ContainerService extends PaginatedDataSource<SequencerPartitionContainer>, BarcodableService<SequencerPartitionContainer> {
  @Override
  default EntityType getEntityType() {
    return EntityType.CONTAINER;
  }

  void applyChanges(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException;

  List<SequencerPartitionContainer> list() throws IOException;

  Collection<SequencerPartitionContainer> listByBarcode(String barcode) throws IOException;

  SequencerPartitionContainer save(SequencerPartitionContainer container) throws IOException;

  SequencerPartitionContainer create(SequencerPartitionContainer container) throws IOException;

  SequencerPartitionContainer update(SequencerPartitionContainer container) throws IOException;

  Collection<SequencerPartitionContainer> listByRunId(long runId) throws IOException;

  Partition getPartition(long partitionId) throws IOException;

  void update(Partition partition) throws IOException;

  FlowCellVersion getFlowCellVersion(long id) throws IOException;

  List<FlowCellVersion> listFlowCellVersions() throws IOException;

  PoreVersion getPoreVersion(long id) throws IOException;

  List<PoreVersion> listPoreVersions() throws IOException;

  SequencingContainerModel getModel(long id) throws IOException;

  SequencingContainerModel findModel(Platform platform, String search, int partitionCount) throws IOException;

  List<SequencingContainerModel> listModels() throws IOException;

}
