package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.sf.ehcache.CacheManager;
import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrderImpl;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderDao;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernatePoolOrderDao implements PoolOrderDao {

  protected static final Logger log = LoggerFactory.getLogger(HibernatePoolOrderDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private CacheManager cacheManager;

  private void invalidatePoolCache(long poolId) {
    cacheManager.getCache("poolCache").remove(DbUtils.hashCodeCacheKeyFor(poolId));
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<PoolOrder> getPoolOrder() {
    Query query = currentSession().createQuery("from PoolOrderImpl");
    @SuppressWarnings("unchecked")
    List<PoolOrder> records = query.list();
    return records;
  }

  @Override
  public PoolOrder getPoolOrder(Long id) {
    return (PoolOrder) currentSession().get(PoolOrderImpl.class, id);
  }

  @Override
  public Long addPoolOrder(PoolOrder poolOrder) {
    Date now = new Date();
    poolOrder.setCreationDate(now);
    poolOrder.setLastUpdated(now);
    Long id = (Long) currentSession().save(poolOrder);
    invalidatePoolCache(poolOrder.getPoolId());
    return id;
  }

  @Override
  public void deletePoolOrder(PoolOrder poolOrder) {
    Long poolId = poolOrder.getPoolId();
    currentSession().delete(poolOrder);
    invalidatePoolCache(poolId);
  }

  @Override
  public void update(PoolOrder poolOrder) {
    Date now = new Date();
    poolOrder.setLastUpdated(now);
    currentSession().update(poolOrder);
    invalidatePoolCache(poolOrder.getPoolId());
  }

  @Override
  public List<PoolOrder> getByPool(Long id) {
    Query query = currentSession().createQuery("from PoolOrderImpl where poolId = :id");
    query.setLong("id", id);
    @SuppressWarnings("unchecked")
    List<PoolOrder> records = query.list();
    return records;
  }

}
