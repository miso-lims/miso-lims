package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface ContainerService extends PaginatedDataSource<SequencerPartitionContainer> {

  void applyChanges(SequencerPartitionContainer source, SequencerPartitionContainer managed) throws IOException;

  SequencerPartitionContainer get(long containerId) throws IOException, AuthorizationException;

}
