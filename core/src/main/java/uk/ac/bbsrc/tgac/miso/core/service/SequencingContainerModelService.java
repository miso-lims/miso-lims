package uk.ac.bbsrc.tgac.miso.core.service;

import static uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType.CONTAINER_MODEL;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface SequencingContainerModelService extends BarcodableService<SequencingContainerModel>,
    BulkSaveService<SequencingContainerModel>, DeleterService<SequencingContainerModel>,
    ListService<SequencingContainerModel> {

  @Override
  default EntityType getEntityType() {
    return CONTAINER_MODEL;
  }

  public SequencingContainerModel find(InstrumentModel platform, String search, int partitionCount) throws IOException;

  public List<SequencingContainerModel> find(PlatformType platform, String search) throws IOException;

  public long getUsage(SequencingContainerModel containerModel, InstrumentModel instrumentModel) throws IOException;

}
