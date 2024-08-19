package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateDeletionDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateDeletionDao sut;

  @Before
  public void setup() {
    sut = new HibernateDeletionDao();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void testDeleteSample() {
    testDelete(SampleImpl.class, 20L);
  }

  @Test
  public void testDeletePrinter() {
    testDelete(Printer.class, 1L);
  }

  @Test
  public void testDeleteServiceRecord() {
    testDelete(ServiceRecord.class, 3L);
  }

  @Test
  public void testDeleteStudy() {
    testDelete(StudyImpl.class, 6L);
  }

  private <T extends Deletable> void testDelete(Class<T> targetClass, long targetId) {
    Session session = entityManager.unwrap(Session.class);
    @SuppressWarnings("unchecked")
    T deletable = (T) session.get(targetClass, targetId);
    assertNotNull(deletable);
    User user = (User) session.get(UserImpl.class, 1L);
    assertNotNull(user);
    String targetType = deletable.getDeleteType();

    QueryBuilder<Deletion, Deletion> builder = new QueryBuilder<>(session, Deletion.class, Deletion.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Deletion_.targetType), targetType));
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(Deletion_.targetId), targetId));

    assertNull(builder.getSingleResultOrNull());

    sut.delete(deletable, user);
    assertNull(session.get(targetClass, targetId));
    assertNotNull(builder.getSingleResultOrNull());
  }

}
