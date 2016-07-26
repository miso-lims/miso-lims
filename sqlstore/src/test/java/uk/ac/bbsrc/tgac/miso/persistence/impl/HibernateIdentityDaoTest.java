package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;

public class HibernateIdentityDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateIdentityDao dao;

  @Before
  public void setup() {
    dao = new HibernateIdentityDao();
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGetIdentity() {
    Identity identity = dao.getIdentity(15L);
    assertNotNull(identity);
    assertTrue(identity.getExternalName().equals("EXT1"));
  }

  @Test
  public void testGetIdentityList() {
    List<Identity> identity = dao.getIdentity();
    assertTrue(identity.size() == 1);
  }

  @Test
  public void testGetIdentityByExternalName() {
    Identity identity = dao.getIdentity("EXT1");
    assertEquals("EXT1", identity.getExternalName());
    assertEquals("INT1", identity.getInternalName());
  }

  @Test
  public void testDeleteIdentity() {
    Identity identity = dao.getIdentity(15L);
    dao.deleteIdentity(identity);

    Identity deleted = dao.getIdentity(15L);
    assertTrue(deleted == null);

  }

}
