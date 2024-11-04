package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HibernateLibraryTypeDaoIT extends AbstractDAOTest {

  private HibernateLibraryTypeDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibraryTypeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    LibraryType type = sut.get(id);
    assertNotNull(type);
    assertEquals(id, type.getId());
  }

  @Test
  public void testGetByPlatformAndDescription() throws IOException {
    PlatformType platform = PlatformType.ILLUMINA;
    String desc = "mRNA Seq";
    LibraryType type = sut.getByPlatformAndDescription(platform, desc);
    assertNotNull(type);
    assertEquals(platform, type.getPlatformType());
    assertEquals(desc, type.getDescription());
  }

  @Test
  public void testList() throws IOException {
    List<LibraryType> list = sut.list();
    assertNotNull(list);
    assertEquals(10, list.size());
  }

  @Test
  public void testListByPlatform() throws IOException {
    List<LibraryType> list = sut.listByPlatform(PlatformType.ILLUMINA);
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(4L, 5L, 6L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

  @Test
  public void testCreate() throws IOException {
    String desc = "New Library Type";
    LibraryType type = new LibraryType();
    type.setDescription(desc);
    type.setPlatformType(PlatformType.ILLUMINA);
    long savedId = sut.create(type);

    clearSession();

    LibraryType saved = (LibraryType) currentSession().get(LibraryType.class, savedId);
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String desc = "New Desc";
    LibraryType type = (LibraryType) currentSession().get(LibraryType.class, id);
    assertNotEquals(desc, type.getDescription());
    type.setDescription(desc);
    sut.update(type);

    clearSession();

    LibraryType saved = (LibraryType) currentSession().get(LibraryType.class, id);
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testGetUsageByLibraries() throws IOException {
    LibraryType type = (LibraryType) currentSession().get(LibraryType.class, 3L);
    assertEquals("mRNA Seq", type.getDescription());
    assertEquals(15L, sut.getUsageByLibraries(type));
  }

  @Test
  public void testGetUsageByLibraryTemplates() throws IOException {
    LibraryType type = (LibraryType) currentSession().get(LibraryType.class, 1L);
    assertEquals("Paired End", type.getDescription());
    assertEquals(0L, sut.getUsageByLibraryTemplates(type));
  }

}
