package uk.ac.bbsrc.tgac.miso.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateUtilDao {

  @PersistenceContext
  EntityManager entityManager;

  public void detach(Object entity) {
    // Need to flush before detaching - otherwise a previously-persisted entity wouldn't be persisted,
    // and Hibernate also throws an exception for this reason
    entityManager.flush();
    entityManager.detach(entity);
  }

}
