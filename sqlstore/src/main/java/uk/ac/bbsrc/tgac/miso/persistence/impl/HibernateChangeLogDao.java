package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLogStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateChangeLogDao implements ChangeLogStore {

  @Autowired
  private SessionFactory sessionFactory;

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Long create(ChangeLog changeLog) {
    currentSession().persist(changeLog);
    return changeLog.getId();
  }

}
