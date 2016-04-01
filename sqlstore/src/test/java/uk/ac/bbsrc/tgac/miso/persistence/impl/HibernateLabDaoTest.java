package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateLabDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateLabDao dao;

  @Before
  public void setup() {
    dao = new HibernateLabDao();
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGetLabList() {
    List<Lab> list = dao.getLabs();
    assertNotNull(list);
    assertEquals(4, list.size());
  }

  @Test
  public void testGetSingleLab() {
    Lab l = dao.getLab(1L);
    assertNotNull(l);
    assertEquals(Long.valueOf(1L), l.getId());
    assertEquals("Lab A1", l.getAlias());
    assertEquals(Long.valueOf(1L), l.getInstitute().getId());
  }

  @Test
  public void testGetSingleLabNull() {
    Lab l = dao.getLab(100L);
    assertNull(l);
  }

  @Test
  public void testAddLab() {
    Lab l = new LabImpl();
    Institute i = new InstituteImpl();
    i.setId(1L);
    l.setInstitute(i);
    l.setAlias("NewLab");
    User user = new UserImpl();
    user.setUserId(1L);
    l.setCreatedBy(user);
    l.setUpdatedBy(user);
    Date now = new Date();
    l.setCreationDate(now);

    final Long newId = dao.addLab(l);
    Lab saved = dao.getLab(newId);
    assertNotNull(saved);
    assertEquals(l.getAlias(), saved.getAlias());
    assertEquals(l.getInstitute().getId(), saved.getInstitute().getId());
  }

  @Test
  public void testUpdateLab() {
    Lab l = dao.getLab(1L);
    final Date oldDate = l.getLastUpdated();
    final String newAlias = "Changed Alias";
    l.setAlias(newAlias);

    dao.update(l);
    Lab updated = dao.getLab(1L);
    assertEquals(newAlias, updated.getAlias());
    assertFalse(oldDate.equals(updated.getLastUpdated()));
  }

  @Test
  public void testDeleteLab() {
    Lab l = dao.getLab(1L);
    assertNotNull(l);
    dao.deleteLab(l);
    assertNull(dao.getLab(1L));
  }

}
