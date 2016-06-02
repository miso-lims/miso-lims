package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderCompletionDao;

@Transactional
@Repository
public class HibernatePoolOrderCompletionDao implements PoolOrderCompletionDao {

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private PlatformStore platformStore;

  @Autowired
  private PoolStore poolStore;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<PoolOrderCompletion> getForPool(Long poolId) throws HibernateException, IOException {
    Query query = currentSession().createQuery("from PoolOrderCompletion where poolId = :id");
    query.setLong("id", poolId);
    return fetchSqlStore(query.list());
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<PoolOrderCompletion> list() throws HibernateException, IOException {
    Query query = currentSession().createQuery("from PoolOrderCompletion");
    return fetchSqlStore(query.list());
  }

  private Collection<PoolOrderCompletion> fetchSqlStore(Collection<PoolOrderCompletion> list) throws IOException {
    for (PoolOrderCompletion completion : list) {
      fetchSqlStore(completion);
    }
    return list;
  }

  private PoolOrderCompletion fetchSqlStore(PoolOrderCompletion completion) throws IOException {
    completion.getSequencingParameters().setPlatform(platformStore.get(completion.getSequencingParameters().getPlatformId()));
    completion.setPool(poolStore.get(completion.getPoolId()));
    return completion;
  }
}
