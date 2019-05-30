package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateDetailedQcStatusDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateDetailedQcStatusDao sut;

  @Before
  public void setup() {
    sut = new HibernateDetailedQcStatusDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    DetailedQcStatus status = sut.get(id);
    assertNotNull(status);
    assertEquals(id, status.getId());
  }

  @Test
  public void testGetByDescription() throws IOException {
    String desc = "Failed";
    DetailedQcStatus status = sut.getByDescription(desc);
    assertNotNull(status);
    assertEquals(desc, status.getDescription());
  }

  @Test
  public void testList() throws IOException {
    List<DetailedQcStatus> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String desc = "Unknown";
    DetailedQcStatus status = new DetailedQcStatusImpl();
    status.setDescription(desc);
    status.setStatus(null);
    status.setNoteRequired(true);
    User user = (User) sessionFactory.getCurrentSession().get(UserImpl.class, 1L);
    status.setChangeDetails(user);
    long savedId = sut.create(status);

    clearSession();

    DetailedQcStatus saved = (DetailedQcStatus) sessionFactory.getCurrentSession().get(DetailedQcStatusImpl.class, savedId);
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String desc = "New Desc";
    DetailedQcStatus status = (DetailedQcStatus) sessionFactory.getCurrentSession().get(DetailedQcStatusImpl.class, id);
    assertNotEquals(desc, status.getDescription());
    status.setDescription(desc);
    sut.update(status);

    clearSession();

    DetailedQcStatus saved = (DetailedQcStatus) sessionFactory.getCurrentSession().get(DetailedQcStatusImpl.class, id);
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testGetUsage() throws IOException {
    DetailedQcStatus status = (DetailedQcStatus) sessionFactory.getCurrentSession().get(DetailedQcStatusImpl.class, 1L);
    assertEquals("Passed", status.getDescription());
    assertEquals(0L, sut.getUsage(status));
  }

  private void clearSession() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }

}
