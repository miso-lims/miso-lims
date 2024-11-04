package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLoggable;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLoggableStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateChangeLoggableDao implements ChangeLoggableStore {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void update(ChangeLoggable changeLoggable) {
    currentSession().merge(changeLoggable);
    // flush required because we need this change persisted immediately to be consumed by
    // trigger-generated change log entries
    currentSession().flush();
  }

}
