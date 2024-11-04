package uk.ac.bbsrc.tgac.miso.persistence.impl;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLogStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateChangeLogDao implements ChangeLogStore {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Long create(ChangeLog changeLog) {
    currentSession().persist(changeLog);
    return changeLog.getId();
  }

}
