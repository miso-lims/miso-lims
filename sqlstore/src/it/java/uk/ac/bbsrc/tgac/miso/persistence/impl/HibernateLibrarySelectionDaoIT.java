package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;

public class HibernateLibrarySelectionDaoIT extends AbstractDAOTest {

  private HibernateLibrarySelectionDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibrarySelectionDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    LibrarySelectionType type = sut.get(id);
    assertNotNull(type);
    assertEquals(id, type.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    String name = "ChIP";
    LibrarySelectionType type = sut.getByName(name);
    assertNotNull(type);
    assertEquals(name, type.getName());
  }

  @Test
  public void testList() throws IOException {
    List<LibrarySelectionType> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String name = "New Selection";
    LibrarySelectionType type = new LibrarySelectionType();
    type.setName(name);
    type.setDescription("desc");
    long savedId = sut.create(type);

    clearSession();

    LibrarySelectionType saved = (LibrarySelectionType) getSessionFactory().getCurrentSession().get(LibrarySelectionType.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "New Name";
    LibrarySelectionType type = (LibrarySelectionType) getSessionFactory().getCurrentSession().get(LibrarySelectionType.class, id);
    assertNotEquals(name, type.getName());
    type.setName(name);
    sut.update(type);

    clearSession();

    LibrarySelectionType saved = (LibrarySelectionType) getSessionFactory().getCurrentSession().get(LibrarySelectionType.class, id);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsageByLibraries() throws IOException {
    LibrarySelectionType type = (LibrarySelectionType) getSessionFactory().getCurrentSession().get(LibrarySelectionType.class, 1L);
    assertEquals("RT-PCR", type.getName());
    assertEquals(15L, sut.getUsageByLibraries(type));
  }

  @Test
  public void testGetUsageByLibraryDesigns() throws IOException {
    LibrarySelectionType type = (LibrarySelectionType) getSessionFactory().getCurrentSession().get(LibrarySelectionType.class, 1L);
    assertEquals(2L, sut.getUsageByLibraryDesigns(type));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(1L, 10L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
