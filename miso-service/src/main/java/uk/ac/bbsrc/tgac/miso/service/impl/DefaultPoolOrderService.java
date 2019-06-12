package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderDao;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultPoolOrderService implements PoolOrderService {

  @Autowired
  private PoolOrderDao poolOrderDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  SequencingParametersService sequencingParametersService;

  @Autowired
  private PoolService poolService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public PoolOrder get(long poolOrderId) throws IOException {
    return poolOrderDao.getPoolOrder(poolOrderId);
  }

  @Override
  public Long create(PoolOrder poolOrder) throws IOException {
    Pool pool = poolService.get(poolOrder.getPool().getId());
    if (pool == null) {
      throw new IOException("No such pool: " + poolOrder.getPool().getId());
    }

    User user = authorizationManager.getCurrentUser();
    poolOrder.setPool(pool);
    poolOrder.setSequencingParameters(sequencingParametersService.get(poolOrder.getSequencingParameter().getId()));
    poolOrder.setCreatedBy(user);
    poolOrder.setUpdatedBy(user);
    return poolOrderDao.addPoolOrder(poolOrder);
  }

  @Override
  public void update(PoolOrder poolOrder) throws IOException {
    User user = authorizationManager.getCurrentUser();
    poolOrder.setCreatedBy(user);
    poolOrder.setUpdatedBy(user);
    poolOrderDao.update(poolOrder);
  }

  @Override
  public List<PoolOrder> list() throws IOException {
    authorizationManager.throwIfNonAdmin();
    return poolOrderDao.getPoolOrder();
  }

  @Override
  public Set<PoolOrder> getByPool(Long id) throws AuthorizationException, IOException {
    return Sets.newHashSet(poolOrderDao.getByPool(id));
  }

  public void setPoolOrderDao(PoolOrderDao poolOrderDao) {
    this.poolOrderDao = poolOrderDao;
  }

  public void setSequencingParametersService(SequencingParametersService sequencingParametersService) {
    this.sequencingParametersService = sequencingParametersService;
  }

  public void setPoolService(PoolService poolService) {
    this.poolService = poolService;
  }

  public void setAuthorizationManager(AuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
  }

  @Override
  public DeletionStore getDeletionStore() {
    return deletionStore;
  }

  @Override
  public AuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  @Override
  public void authorizeDeletion(PoolOrder order) throws IOException {
    authorizationManager.throwIfNotInternal();
  }

  @Override
  public void beforeDelete(PoolOrder object) throws IOException {
    Pool pool = poolService.get(object.getPool().getId());
    pool.setLastModifier(authorizationManager.getCurrentUser());
    poolService.update(pool);
  }

}
