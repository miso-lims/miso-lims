package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    Identity identity = dao.getIdentity(1L);
    assertNotNull(identity);
    assertTrue(identity.getExternalName().equals("externalName1"));
  }

  @Test
  public void testGetIdentityList() {
    List<Identity> identity = dao.getIdentity();
    assertTrue(identity.size() == 2);
  }

  @Test
  public void testGetIdentityByExternalName() {
    Identity identity = dao.getIdentity("externalName1");
    assertTrue(identity.getExternalName().equals("externalName1"));
  }

  @Test
  public void testDeleteIdentity() {
    Identity identity = dao.getIdentity(4L);
    dao.deleteIdentity(identity);

    Identity deleted = dao.getIdentity(4L);
    assertTrue(deleted == null);

  }

}
