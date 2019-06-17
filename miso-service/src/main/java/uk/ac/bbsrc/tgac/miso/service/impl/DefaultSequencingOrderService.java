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
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderDao;
import uk.ac.bbsrc.tgac.miso.service.SequencingOrderService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingOrderService implements SequencingOrderService {

  @Autowired
  private SequencingOrderDao sequencingOrderDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  SequencingParametersService sequencingParametersService;

  @Autowired
  private PoolService poolService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Override
  public SequencingOrder get(long id) throws IOException {
    return sequencingOrderDao.get(id);
  }

  @Override
  public long create(SequencingOrder seqOrder) throws IOException {
    Pool pool = poolService.get(seqOrder.getPool().getId());
    if (pool == null) {
      throw new IOException("No such pool: " + seqOrder.getPool().getId());
    }

    User user = authorizationManager.getCurrentUser();
    seqOrder.setPool(pool);
    seqOrder.setSequencingParameters(sequencingParametersService.get(seqOrder.getSequencingParameter().getId()));
    seqOrder.setCreatedBy(user);
    seqOrder.setUpdatedBy(user);
    return sequencingOrderDao.create(seqOrder);
  }

  @Override
  public long update(SequencingOrder seqOrder) throws IOException {
    User user = authorizationManager.getCurrentUser();
    seqOrder.setCreatedBy(user);
    seqOrder.setUpdatedBy(user);
    sequencingOrderDao.update(seqOrder);
    return seqOrder.getId();
  }

  @Override
  public List<SequencingOrder> list() throws IOException {
    authorizationManager.throwIfNonAdmin();
    return sequencingOrderDao.list();
  }

  @Override
  public Set<SequencingOrder> getByPool(Pool pool) throws AuthorizationException, IOException {
    return Sets.newHashSet(sequencingOrderDao.listByPool(pool));
  }

  public void setSequencingOrderDao(SequencingOrderDao sequencingOrderDao) {
    this.sequencingOrderDao = sequencingOrderDao;
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
  public void authorizeDeletion(SequencingOrder order) throws IOException {
    authorizationManager.throwIfNotInternal();
  }

  @Override
  public void beforeDelete(SequencingOrder object) throws IOException {
    Pool pool = poolService.get(object.getPool().getId());
    pool.setLastModifier(authorizationManager.getCurrentUser());
    poolService.update(pool);
  }

}
