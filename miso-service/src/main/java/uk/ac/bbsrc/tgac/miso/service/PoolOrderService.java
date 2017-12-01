package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface PoolOrderService {

  PoolOrder get(Long poolOrderId) throws IOException;

  Long create(PoolOrder poolOrder) throws IOException;

  void update(PoolOrder sample) throws IOException;

  Set<PoolOrder> getAll() throws IOException;

  void delete(Long poolOrderId) throws IOException;

  Set<PoolOrder> getByPool(Long id) throws AuthorizationException, IOException;
}
