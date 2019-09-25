package uk.ac.bbsrc.tgac.miso.core.service;

import static uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType.CONTAINER_MODEL;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public interface SequencingContainerModelService extends BarcodableService<SequencingContainerModel>,
    DeleterService<SequencingContainerModel>, ListService<SequencingContainerModel>, SaveService<SequencingContainerModel> {

  @Override
  default EntityType getEntityType() {
    return CONTAINER_MODEL;
  }

  public SequencingContainerModel find(InstrumentModel platform, String search, int partitionCount) throws IOException;

}
