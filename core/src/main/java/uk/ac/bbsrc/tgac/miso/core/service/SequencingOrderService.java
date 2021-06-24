package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;

public interface SequencingOrderService
    extends DeleterService<SequencingOrder>, ListService<SequencingOrder>, BulkSaveService<SequencingOrder> {

  Set<SequencingOrder> getByPool(Pool pool) throws AuthorizationException, IOException;

  List<SequencingOrder> listByAttributes(Pool pool, RunPurpose purpose, SequencingContainerModel containerModel,
      SequencingParameters parameters, Integer partitions) throws IOException;

}
