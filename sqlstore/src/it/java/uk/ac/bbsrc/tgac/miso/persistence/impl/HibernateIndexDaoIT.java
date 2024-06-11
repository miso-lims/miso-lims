package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateIndexDaoIT extends AbstractDAOTest {

  private HibernateIndexDao dao;

  @Before
  public void setup() {
    dao = new HibernateIndexDao();
    dao.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws Exception {
    Index indexById = dao.get(8);
    assertEquals(8L, indexById.getId());
    assertEquals("ACTTGA", indexById.getSequence());
    assertEquals("Index 8", indexById.getName());
    assertEquals("TruSeq Single Index", indexById.getFamily().getName());

  }

  @Test
  public void testListIndicesByPlatform() throws Exception {
    Collection<Index> illumina = dao.list(0, 10, true, "indexId", PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertTrue(illumina.size() > 0);
  }

  @Test
  public void testList() throws Exception {
    List<Index> list = dao.list();
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
    IndexFamily family = (IndexFamily) currentSession().get(IndexFamily.class, 3L);
    int position = 1;
    String name = "N710";
    Index index = dao.getByFamilyPositionAndName(family, position, name);
    assertNotNull(index);
    assertEquals(family.getId(), index.getFamily().getId());
    assertEquals(position, index.getPosition());
    assertEquals(name, index.getName());
  }

  @Test
  public void testGetUsage() throws Exception {
    Index index = (Index) currentSession().get(Index.class, 12L);
    assertEquals(1L, dao.getUsage(index));
  }

  @Test
  public void testCreate() throws Exception {
    IndexFamily family = (IndexFamily) currentSession().get(IndexFamily.class, 1L);
    Index index = new Index();
    index.setFamily(family);
    index.setName("New Index");
    index.setPosition(1);
    index.setSequence("AAAAAA");
    long savedId = dao.create(index);

    clearSession();
    Index saved = (Index) currentSession().get(Index.class, savedId);
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
    Index index = (Index) currentSession().get(Index.class, indexId);
    assertNotEquals(newName, index.getName());
    index.setName(newName);
    dao.update(index);

    clearSession();

    Index updated = (Index) currentSession().get(Index.class, indexId);
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
