package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.persistence.ProviderDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public abstract class HibernateProviderDao<T> implements ProviderDao<T> {

  @Autowired
  private SessionFactory sessionFactory;

  private final Class<? extends T> entityClass;

  public HibernateProviderDao(Class<? extends T> entityClass) {
    this.entityClass = entityClass;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  protected Class<? extends T> getEntityClass() {
    return entityClass;
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public T get(long id) throws IOException {
    return entityClass.cast(currentSession().get(entityClass, id));
  }

  @Override
  public List<T> list() throws IOException {
    @SuppressWarnings("unchecked")
    List<T> results = currentSession().createCriteria(entityClass).list();
    return results;
  }

  protected <V> T getBy(String property, V value) {
    return entityClass.cast(currentSession().createCriteria(entityClass)
        .add(Restrictions.eq(property, value))
        .uniqueResult());
  }

  protected List<T> listByIdList(String idProperty, Collection<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<T> results = currentSession().createCriteria(entityClass)
        .add(Restrictions.in(idProperty, ids))
        .list();
    return results;
  }

  protected <U> long getUsageBy(Class<U> user, String property, T value) {
    return (long) currentSession().createCriteria(user)
        .add(Restrictions.eq(property, value))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
