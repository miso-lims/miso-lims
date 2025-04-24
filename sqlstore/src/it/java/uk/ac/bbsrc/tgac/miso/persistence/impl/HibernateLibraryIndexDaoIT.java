package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateLibraryIndexDaoIT extends AbstractDAOTest {

  private HibernateLibraryIndexDao dao;

  @Before
  public void setup() {
    dao = new HibernateLibraryIndexDao();
    dao.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws Exception {
    LibraryIndex indexById = dao.get(8);
    assertEquals(8L, indexById.getId());
    assertEquals("ACTTGA", indexById.getSequence());
    assertEquals("Index 8", indexById.getName());
    assertEquals("TruSeq Single Index", indexById.getFamily().getName());

  }

  @Test
  public void testListIndicesByPlatform() throws Exception {
    Collection<LibraryIndex> illumina =
        dao.list(0, 10, true, "indexId", PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertTrue(illumina.size() > 0);
  }

  @Test
  public void testList() throws Exception {
    List<LibraryIndex> list = dao.list();
    assertEquals(80, list.size());
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("Index 1"));
    testSearch(PaginationFilter.query("TGCATGCA"));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(dao.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "name", filter));
  }

  @Test
  public void testGetByFamilyPositionAndName() throws Exception {
    LibraryIndexFamily family = (LibraryIndexFamily) currentSession().get(LibraryIndexFamily.class, 3L);
    int position = 1;
    String name = "N710";
    LibraryIndex index = dao.getByFamilyPositionAndName(family, position, name);
    assertNotNull(index);
    assertEquals(family.getId(), index.getFamily().getId());
    assertEquals(position, index.getPosition());
    assertEquals(name, index.getName());
  }

  @Test
  public void testGetUsage() throws Exception {
    LibraryIndex index = (LibraryIndex) currentSession().get(LibraryIndex.class, 12L);
    assertEquals(1L, dao.getUsage(index));
  }

  @Test
  public void testCreate() throws Exception {
    LibraryIndexFamily family = (LibraryIndexFamily) currentSession().get(LibraryIndexFamily.class, 1L);
    LibraryIndex index = new LibraryIndex();
    index.setFamily(family);
    index.setName("New Index");
    index.setPosition(1);
    index.setSequence("AAAAAA");
    long savedId = dao.create(index);

    clearSession();
    LibraryIndex saved = (LibraryIndex) currentSession().get(LibraryIndex.class, savedId);
    assertNotNull(saved);
    assertEquals(index.getFamily().getId(), saved.getFamily().getId());
    assertEquals(index.getName(), saved.getName());
    assertEquals(index.getPosition(), saved.getPosition());
    assertEquals(index.getSequence(), saved.getSequence());
  }

  @Test
  public void testUpdate() throws Exception {
    long indexId = 15L;
    String newName = "changed";
    LibraryIndex index = (LibraryIndex) currentSession().get(LibraryIndex.class, indexId);
    assertNotEquals(newName, index.getName());
    index.setName(newName);
    dao.update(index);

    clearSession();

    LibraryIndex updated = (LibraryIndex) currentSession().get(LibraryIndex.class, indexId);
    assertEquals(newName, updated.getName());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(dao::listByIdList, Arrays.asList(20L, 21L, 22L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(dao::listByIdList);
  }

}
