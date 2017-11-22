package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
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
  SequencingParametersService sequencingParametersService;

  @Autowired
  private PoolService poolService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public PoolOrder get(Long poolOrderId) throws IOException {
    return poolOrderDao.getPoolOrder(poolOrderId);
  }

  @Override
  public Long create(PoolOrder poolOrder) throws IOException {
    Pool pool = poolService.get(poolOrder.getPoolId());
    authorizationManager.throwIfNotWritable(pool);
    if (pool == null) {
      throw new IOException("No such pool: " + poolOrder.getPoolId());
    }

    User user = authorizationManager.getCurrentUser();
    poolOrder.setPoolId(pool.getId());
    poolOrder.setSequencingParameter(sequencingParametersService.get(poolOrder.getSequencingParameter().getId()));
    poolOrder.setCreatedBy(user);
    poolOrder.setUpdatedBy(user);
    return poolOrderDao.addPoolOrder(poolOrder);
  }

  @Override
  public void update(PoolOrder poolOrder) throws IOException {
    Pool owner = poolService.get(poolOrder.getPoolId());
    authorizationManager.throwIfNotWritable(owner);
    User user = authorizationManager.getCurrentUser();
    poolOrder.setCreatedBy(user);
    poolOrder.setUpdatedBy(user);
    poolOrderDao.update(poolOrder);
  }

  @Override
  public Set<PoolOrder> getAll() throws IOException {
    authorizationManager.throwIfNonAdmin();
    return Sets.newHashSet(poolOrderDao.getPoolOrder());
  }

  @Override
  public void delete(Long poolOrderId) throws IOException {
    PoolOrder poolOrder = poolOrderDao.getPoolOrder(poolOrderId);
    Pool pool = poolService.get(poolOrder.getPoolId());
    authorizationManager.throwIfNotWritable(pool);
    pool.setLastModifier(authorizationManager.getCurrentUser());
    poolService.save(pool);
    if (poolOrder != null) poolOrderDao.deletePoolOrder(poolOrder);
  }

  @Override
  public Set<PoolOrder> getByPool(Long id) throws AuthorizationException, IOException {
    Pool pool = poolService.get(id);
    authorizationManager.throwIfNotReadable(pool);
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

}
