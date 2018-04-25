package uk.ac.bbsrc.tgac.miso.service;

import static uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType.CONTAINER_MODEL;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public interface ContainerModelService extends BarcodableService<SequencingContainerModel> {
  @Override
  default EntityType getEntityType() {
    return CONTAINER_MODEL;
  }

  SequencingContainerModel find(Platform platform, String search, int partitionCount) throws IOException;

  List<SequencingContainerModel> list() throws IOException;
}
