package uk.ac.bbsrc.tgac.miso.sqlstore.util;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateDao {

  @Autowired
  private SessionFactory sessionFactory;

  public Session getCurrentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public Criteria getCriteriaForClass(Class<?> clazz) {
    return getCurrentSession().createCriteria(clazz);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

}
