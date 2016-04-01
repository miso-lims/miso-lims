package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

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
  public void testAddIdentity() {
    Identity identity = new IdentityImpl();
    identity.setCreationDate(new Date());
    String externalName = "newIdentityExternalName";
    String internalName = "newIdentityInternalName";
    identity.setExternalName(externalName);
    identity.setInternalName(internalName);
    Sample sample = new SampleImpl();
    sample.setId(2L);
    identity.setSample(sample);
    User user = new UserImpl();
    user.setUserId(1L);
    identity.setUpdatedBy(user);
    identity.setCreatedBy(user);
    Long aLong = dao.addIdentity(identity);
    assertNotNull(aLong);

    Identity returnedIdentity = dao.getIdentity(aLong);

    assertEquals(externalName, returnedIdentity.getExternalName());
    assertEquals(internalName, returnedIdentity.getInternalName());
    assertEquals(2, returnedIdentity.getSample().getId());

  }

  @Test
  public void testDeleteIdentity() {
    Identity identity = dao.getIdentity(2L);
    dao.deleteIdentity(identity);

    Identity deleted = dao.getIdentity(2L);
    assertTrue(deleted == null);

  }

  @Test
  public void testUpdateIdentity() {
    Identity identity = dao.getIdentity(1L);
    identity.setInternalName("updatedInternal");
    identity.setExternalName("updatedExternal");

    dao.update(identity);

    Identity updated = dao.getIdentity("updatedExternal");
    assertEquals(new Long(1), dao.getIdentity().get(0).getIdentityId());

  }

}
