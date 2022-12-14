package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

public class HibernateLibraryStrategyDaoIT extends AbstractDAOTest {

  private HibernateLibraryStrategyDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibraryStrategyDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    LibraryStrategyType type = sut.get(id);
    assertNotNull(type);
    assertEquals(id, type.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    String name = "CTS";
    LibraryStrategyType type = sut.getByName(name);
    assertNotNull(type);
    assertEquals(name, type.getName());
  }

  @Test
  public void testList() throws IOException {
    List<LibraryStrategyType> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String name = "New Strategy";
    LibraryStrategyType type = new LibraryStrategyType();
    type.setName(name);
    type.setDescription("desc");
    long savedId = sut.create(type);

    clearSession();

    LibraryStrategyType saved = (LibraryStrategyType) getSessionFactory().getCurrentSession().get(LibraryStrategyType.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "New Name";
    LibraryStrategyType type = (LibraryStrategyType) getSessionFactory().getCurrentSession().get(LibraryStrategyType.class, id);
    assertNotEquals(name, type.getName());
    type.setName(name);
    sut.update(type);

    clearSession();

    LibraryStrategyType saved = (LibraryStrategyType) getSessionFactory().getCurrentSession().get(LibraryStrategyType.class, id);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsageByLibraries() throws IOException {
    LibraryStrategyType type = (LibraryStrategyType) getSessionFactory().getCurrentSession().get(LibraryStrategyType.class, 1L);
    assertEquals("WGS", type.getName());
    assertEquals(15L, sut.getUsageByLibraries(type));
  }

  @Test
  public void testGetUsageByLibraryDesigns() throws IOException {
    LibraryStrategyType type = (LibraryStrategyType) getSessionFactory().getCurrentSession().get(LibraryStrategyType.class, 1L);
    assertEquals("WGS", type.getName());
    assertEquals(2L, sut.getUsageByLibraryDesigns(type));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(12L, 14L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
