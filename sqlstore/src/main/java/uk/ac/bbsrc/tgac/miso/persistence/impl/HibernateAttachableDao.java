package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Attachable;
import uk.ac.bbsrc.tgac.miso.core.store.AttachableStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateAttachableDao implements AttachableStore {

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public Attachable getManaged(Attachable object) {
    return (Attachable) currentSession().get(object.getClass(), object.getId());
  }

  @Override
  public void save(Attachable object) {
    currentSession().save(object);
  }

}
