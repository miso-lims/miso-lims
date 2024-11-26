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
    entityManager.detach(entity);
  }

}
