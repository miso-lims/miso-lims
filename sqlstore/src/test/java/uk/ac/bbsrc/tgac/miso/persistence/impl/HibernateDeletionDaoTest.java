package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;
import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deletion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateDeletionDaoTest extends AbstractDAOTest {

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateDeletionDao sut;

  @Before
  public void setup() {
    sut = new HibernateDeletionDao();
    sut.setSessionFactory(sessionFactory);
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
    Session session = sessionFactory.getCurrentSession();
    @SuppressWarnings("unchecked")
    T deletable = (T) session.get(targetClass, targetId);
    assertNotNull(deletable);
    User user = (User) session.get(UserImpl.class, 1L);
    assertNotNull(user);
    String targetType = deletable.getDeleteType();

    Criteria getExpectedDeletion = session.createCriteria(Deletion.class)
        .add(Restrictions.eq("targetType", targetType))
        .add(Restrictions.eq("targetId", targetId));
    assertNull(getExpectedDeletion.uniqueResult());

    sut.delete(deletable, user);
    assertNull(session.get(targetClass, targetId));
    assertNotNull(getExpectedDeletion.uniqueResult());
  }

}
