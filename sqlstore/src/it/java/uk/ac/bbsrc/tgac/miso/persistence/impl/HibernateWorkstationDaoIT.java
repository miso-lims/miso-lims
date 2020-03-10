package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;

public class HibernateWorkstationDaoIT extends AbstractDAOTest {

  private HibernateWorkstationDao sut;

  @Before
  public void setup() {
    sut = new HibernateWorkstationDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    Workstation workstation = sut.get(id);
    assertNotNull(workstation);
    assertEquals(id, workstation.getId());
  }

  @Test
  public void testList() throws IOException {
    List<Workstation> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New Workstation";
    Workstation workstation = new Workstation();
    workstation.setAlias(alias);
    long savedId = sut.create(workstation);

    clearSession();

    Workstation saved = (Workstation) getSessionFactory().getCurrentSession().get(Workstation.class, savedId);
    assertNotNull(saved);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Alias";
    Workstation workstation = (Workstation) getSessionFactory().getCurrentSession().get(Workstation.class, id);
    assertNotEquals(alias, workstation.getAlias());
    workstation.setAlias(alias);
    sut.update(workstation);

    clearSession();

    Workstation saved = (Workstation) getSessionFactory().getCurrentSession().get(Workstation.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    Workstation workstation = (Workstation) getSessionFactory().getCurrentSession().get(Workstation.class, 1L);
    assertEquals(4L, sut.getUsage(workstation));
  }

}
