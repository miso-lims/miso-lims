package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface PoolOrderService extends DeleterService<PoolOrder>, ListService<PoolOrder> {

  Long create(PoolOrder poolOrder) throws IOException;

  void update(PoolOrder sample) throws IOException;

  Set<PoolOrder> getByPool(Long id) throws AuthorizationException, IOException;
}
