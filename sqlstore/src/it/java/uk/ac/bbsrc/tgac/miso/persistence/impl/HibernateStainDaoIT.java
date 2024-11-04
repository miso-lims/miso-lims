package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Stain;

public class HibernateStainDaoIT extends AbstractDAOTest {

  private HibernateStainDao sut;

  @Before
  public void setup() {
    sut = new HibernateStainDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    Stain stain = sut.get(id);
    assertNotNull(stain);
    assertEquals(id, stain.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    String name = "Stain One";
    Stain stain = sut.getByName(name);
    assertNotNull(stain);
    assertEquals(name, stain.getName());
  }

  @Test
  public void testList() throws IOException {
    List<Stain> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String name = "New Stain";
    Stain stain = new Stain();
    stain.setName(name);
    long savedId = sut.create(stain);

    clearSession();

    Stain saved = (Stain) currentSession().get(Stain.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "New Name";
    Stain stain = (Stain) currentSession().get(Stain.class, id);
    assertNotEquals(name, stain.getName());
    stain.setName(name);
    sut.update(stain);

    clearSession();

    Stain saved = (Stain) currentSession().get(Stain.class, id);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsage() throws IOException {
    Stain stain = (Stain) currentSession().get(Stain.class, 1L);
    assertEquals("Stain One", stain.getName());
    assertEquals(0L, sut.getUsage(stain));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(2L, 3L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
