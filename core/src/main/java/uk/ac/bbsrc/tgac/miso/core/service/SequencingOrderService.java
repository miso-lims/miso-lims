package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;

public interface SequencingOrderService extends DeleterService<SequencingOrder>, ListService<SequencingOrder>, SaveService<SequencingOrder> {

  Set<SequencingOrder> getByPool(Pool pool) throws AuthorizationException, IOException;
}
