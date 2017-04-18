package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class BaseHibernateDao<T> {
  protected final Class<? extends T> dataClass;
  @Autowired
  private SessionFactory sessionFactory;

  public BaseHibernateDao(Class<? extends T> dataClass) {
    super();
    this.dataClass = dataClass;
  }

  public Criteria createCriteria() {
    return currentSession().createCriteria(dataClass);
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public Class<? extends T> getDataClass() {
    return dataClass;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
