package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateInstituteDaoTest extends AbstractDAOTest {

  private HibernateInstituteDao dao;

  @Autowired
  private SessionFactory sessionFactory;

  @Before
  public void setup() {
    dao = new HibernateInstituteDao();
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGetInstituteList() {
    List<Institute> list = dao.getInstitute();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testGetSingleInstitute() {
    Institute i = dao.getInstitute(1L);
    assertNotNull(i);
    assertEquals(1L, i.getId());
    assertEquals("Institute A", i.getAlias());
  }

  @Test
  public void testGetSingleInstituteNull() {
    Institute i = dao.getInstitute(100L);
    assertNull(i);
  }

  @Test
  public void testAddInstitute() {
    Institute i = makeInstitute("Test Institute");
    final Long newId = dao.addInstitute(i);
    Institute saved = dao.getInstitute(newId);
    assertEquals(i.getAlias(), saved.getAlias());
    assertNotNull(i.getCreationDate());
    assertNotNull(i.getLastUpdated());
  }

  @Test
  public void testDeleteInstitute() {
    Institute i = makeInstitute("Test Institute");
    final Long newId = dao.addInstitute(i);
    Institute saved = dao.getInstitute(newId);
    assertNotNull(saved);
    dao.deleteInstitute(saved);
    assertNull(dao.getInstitute(newId));
  }

  @Test
  public void testUpdate() {
    Institute i = dao.getInstitute(1L);
    final Date oldDate = i.getLastUpdated();
    final String newAlias = "Changed Alias";
    i.setAlias(newAlias);

    dao.update(i);
    Institute updated = dao.getInstitute(1L);
    assertEquals(newAlias, updated.getAlias());
    assertFalse(oldDate.equals(updated.getLastUpdated()));
  }

  private Institute makeInstitute(String alias) {
    Institute i = new InstituteImpl();
    i.setAlias(alias);
    User user = new UserImpl();
    user.setUserId(1L);
    i.setCreatedBy(user);
    i.setUpdatedBy(user);
    Date now = new Date();
    i.setCreationDate(now);
    return i;
  }

}
