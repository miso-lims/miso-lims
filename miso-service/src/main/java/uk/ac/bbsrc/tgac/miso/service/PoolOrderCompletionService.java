package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface PoolOrderCompletionService {
  public Collection<PoolOrderCompletion> getOrderCompletionForPool(long id) throws AuthorizationException, IOException;

  public Collection<PoolOrderCompletion> getAllOrders() throws AuthorizationException, IOException;
}
