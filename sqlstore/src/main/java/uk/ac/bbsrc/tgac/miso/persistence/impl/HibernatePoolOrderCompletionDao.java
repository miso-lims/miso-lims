package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderCompletionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolOrderCompletionDao implements PoolOrderCompletionDao {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Collection<PoolOrderCompletion> getForPool(Long poolId) throws HibernateException, IOException {
    Criteria criteria = currentSession().createCriteria(PoolOrderCompletion.class);
    criteria.add(Restrictions.eq("id.pool.id", poolId));
    @SuppressWarnings("unchecked")
    List<PoolOrderCompletion> result = criteria.list();
    return result;
  }

  @Override
  public Collection<PoolOrderCompletion> list() throws HibernateException, IOException {
    Criteria criteria = currentSession().createCriteria(PoolOrderCompletion.class);
    @SuppressWarnings("unchecked")
    List<PoolOrderCompletion> result = criteria.list();
    return result;
  }

}
