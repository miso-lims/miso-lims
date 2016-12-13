package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderCompletionDao;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderCompletionService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service

public class DefaultPoolOrderCompletionService implements PoolOrderCompletionService {
  @Autowired
  private PoolOrderCompletionDao poolOrderCompletionDao;

  @Autowired
  private PoolStore poolDao;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public Collection<PoolOrderCompletion> getOrderCompletionForPool(long id) throws AuthorizationException, IOException {
    Pool p = poolDao.get(id);
    authorizationManager.throwIfNotReadable(p);
    return poolOrderCompletionDao.getForPool(id);
  }

  @Override
  public Collection<PoolOrderCompletion> getAllOrders() throws AuthorizationException, IOException {
    authorizationManager.throwIfUnauthenticated();
    return poolOrderCompletionDao.list();
  }

}
