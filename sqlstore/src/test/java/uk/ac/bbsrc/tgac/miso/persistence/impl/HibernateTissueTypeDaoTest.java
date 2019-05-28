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
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateTissueTypeDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateTissueTypeDao sut;

  @Before
  public void setup() {
    sut = new HibernateTissueTypeDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    TissueType tt = sut.get(id);
    assertNotNull(tt);
    assertEquals(id, tt.getId());
  }

  @Test
  public void testGetByAlias() throws IOException {
    String alias = "Test Type";
    TissueType tt = sut.getByAlias(alias);
    assertNotNull(tt);
    assertEquals(alias, tt.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<TissueType> list = sut.list();
    assertNotNull(list);
    assertEquals(1, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New Type";
    TissueType tt = new TissueTypeImpl();
    tt.setAlias(alias);
    tt.setDescription("descriptive stuff");
    User user = (User) sessionFactory.getCurrentSession().get(UserImpl.class, 1L);
    tt.setChangeDetails(user);
    long savedId = sut.create(tt);

    clearSession();

    TissueType saved = (TissueType) sessionFactory.getCurrentSession().get(TissueTypeImpl.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Name";
    TissueType tt = (TissueType) sessionFactory.getCurrentSession().get(TissueTypeImpl.class, id);
    assertNotEquals(alias, tt.getAlias());
    tt.setAlias(alias);
    sut.update(tt);

    clearSession();

    TissueType saved = (TissueType) sessionFactory.getCurrentSession().get(TissueTypeImpl.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    TissueType tt = (TissueType) sessionFactory.getCurrentSession().get(TissueTypeImpl.class, 1L);
    assertEquals("Test Type", tt.getAlias());
    assertEquals(2L, sut.getUsage(tt));
  }

  private void clearSession() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }

}
