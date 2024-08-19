package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;

public class HibernateStainCategoryDaoIT extends AbstractDAOTest {

  private HibernateStainCategoryDao sut;

  @Before
  public void setup() {
    sut = new HibernateStainCategoryDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    StainCategory cat = sut.get(id);
    assertNotNull(cat);
    assertEquals(id, cat.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    String name = "Category Two";
    StainCategory cat = sut.getByName(name);
    assertNotNull(cat);
    assertEquals(name, cat.getName());
  }

  @Test
  public void testList() throws IOException {
    List<StainCategory> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String name = "New Category";
    StainCategory cat = new StainCategory();
    cat.setName(name);
    long savedId = sut.create(cat);

    clearSession();

    StainCategory saved = (StainCategory) currentSession().get(StainCategory.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "New Name";
    StainCategory cat = (StainCategory) currentSession().get(StainCategory.class, id);
    assertNotEquals(name, cat.getName());
    cat.setName(name);
    sut.update(cat);

    clearSession();

    StainCategory saved = (StainCategory) currentSession().get(StainCategory.class, id);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsage() throws IOException {
    StainCategory cat = (StainCategory) currentSession().get(StainCategory.class, 1L);
    assertEquals("Category One", cat.getName());
    assertEquals(2L, sut.getUsage(cat));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
