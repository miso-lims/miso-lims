package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RunPurposeService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingOrderService;
import uk.ac.bbsrc.tgac.miso.core.service.SequencingParametersService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationError;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.store.DeletionStore;
import uk.ac.bbsrc.tgac.miso.core.util.IndexChecker;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderDao;

@Transactional(rollbackFor = Exception.class)
@Service
public class DefaultSequencingOrderService implements SequencingOrderService {

  @Value("${miso.pools.strictIndexChecking:false}")
  private Boolean strictPools;

  @Autowired
  private SequencingOrderDao sequencingOrderDao;

  @Autowired
  private DeletionStore deletionStore;

  @Autowired
  private SequencingParametersService sequencingParametersService;

  @Autowired
  private RunPurposeService runPurposeService;

  @Autowired
  private PoolService poolService;

  @Autowired
  private AuthorizationManager authorizationManager;

  @Autowired
  private IndexChecker indexChecker;

  @Override
  public SequencingOrder get(long id) throws IOException {
    return sequencingOrderDao.get(id);
  }

  @Override
  public long create(SequencingOrder seqOrder) throws ValidationException, IOException {
    Pool pool = poolService.get(seqOrder.getPool().getId());
    if (pool == null) {
      throw new IOException("No such pool: " + seqOrder.getPool().getId());
    }

    if(strictPools &&
            (indexChecker.getDuplicateIndicesSequences(pool).size() > 0
                    || indexChecker.getNearDuplicateIndicesSequences(pool).size() > 0)){
      throw new ValidationException("Cannot create a sequencing order for a pool which contains duplicate or " +
              "near-duplicate indices. Please resolve index problems in pool " + pool.getAlias());
    }

    User user = authorizationManager.getCurrentUser();
    seqOrder.setPool(pool);
    seqOrder.setSequencingParameters(sequencingParametersService.get(seqOrder.getSequencingParameter().getId()));
    seqOrder.setPurpose(runPurposeService.get(seqOrder.getPurpose().getId()));
    seqOrder.setCreatedBy(user);
    seqOrder.setUpdatedBy(user);
    validateChange(seqOrder, null);
    return sequencingOrderDao.create(seqOrder);
  }

  @Override
  public long update(SequencingOrder seqOrder) throws IOException {
    SequencingOrder managed = sequencingOrderDao.get(seqOrder.getId());
    managed.setPartitions(seqOrder.getPartitions());
    managed.setUpdatedBy(authorizationManager.getCurrentUser());
    validateChange(managed, seqOrder);
    return sequencingOrderDao.update(managed);
  }

  private void validateChange(SequencingOrder order, SequencingOrder beforeChange) {
    List<ValidationError> errors = new ArrayList<>();

    if (order.getPartitions() < 1) {
      errors.add(new ValidationError("partitions", "Must be 1 or greater"));
    }

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
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
    // update pool to affect last modifier and last modified time
    poolService.update(object.getPool());
  }

  @Override
  public List<SequencingOrder> listByAttributes(Pool pool, RunPurpose purpose, SequencingParameters parameters, Integer partitions)
      throws IOException {
    return sequencingOrderDao.listByAttributes(pool, purpose, parameters, partitions);
  }

}
