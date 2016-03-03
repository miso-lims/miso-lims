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
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderDao;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional
@Service
public class DefaultPoolOrderService implements PoolOrderService {

  @Autowired
  private PoolOrderDao poolOrderDao;

  @Autowired
  SequencingParametersDao sequencingParametersDao;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public PoolOrder get(Long poolOrderId) throws IOException {
    return poolOrderDao.getPoolOrder(poolOrderId);
  }

  @Override
  public Long create(PoolOrderDto poolOrderDto) throws IOException {
    Pool<?> pool = requestManager.getPoolById(poolOrderDto.getPoolId());
    authorizationManager.throwIfNotWritable(pool);
    if (pool == null) {
      throw new IOException("No such pool: " + poolOrderDto.getPoolId());
    }

    User user = authorizationManager.getCurrentUser();
    PoolOrder poolOrder = Dtos.to(poolOrderDto);
    poolOrder.setPoolId(pool.getId());
    poolOrder.setSequencingParameter(sequencingParametersDao.getSequencingParameters(poolOrderDto.getParameters().getId()));
    poolOrder.setCreatedBy(user);
    poolOrder.setUpdatedBy(user);
    return poolOrderDao.addPoolOrder(poolOrder);
  }

  @Override
  public void update(PoolOrder poolOrder) throws IOException {
    authorizationManager.throwIfNonAdmin();
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
    Pool<?> pool = requestManager.getPoolById(poolOrder.getPoolId());
    authorizationManager.throwIfNotWritable(pool);
    if (poolOrder != null) poolOrderDao.deletePoolOrder(poolOrder);
  }

  @Override
  public Set<PoolOrder> getByPool(Long id) throws AuthorizationException, IOException {
    Pool<?> pool = requestManager.getPoolById(id);
    authorizationManager.throwIfNotReadable(pool);
    return Sets.newHashSet(poolOrderDao.getByPool(id));
  }

}
