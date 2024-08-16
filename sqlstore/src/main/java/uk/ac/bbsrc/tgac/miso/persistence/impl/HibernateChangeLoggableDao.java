package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLoggableStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateChangeLoggableDao implements ChangeLoggableStore {

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public void update(ChangeLoggable changeLoggable) {
    currentSession().merge(changeLoggable);
    // flush required because we need this change persisted immediately to be consumed by
    // trigger-generated change log entries
    currentSession().flush();
  }

}
