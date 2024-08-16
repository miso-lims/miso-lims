package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;

public interface SequencingOrderDao extends BulkSaveDao<SequencingOrder> {

  List<SequencingOrder> listByPool(Pool pool);

  List<SequencingOrder> listByAttributes(Pool pool, RunPurpose purpose, SequencingContainerModel containerModel,
      SequencingParameters parameters, Integer partitions) throws IOException;

}
