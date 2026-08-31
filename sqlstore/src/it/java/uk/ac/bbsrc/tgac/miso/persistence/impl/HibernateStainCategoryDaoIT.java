package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.StainCategory;

public class HibernateStainCategoryDaoIT extends AbstractDAOTest {

  private HibernateStainCategoryDao sut;

  @BeforeEach
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

    StainCategory saved = (StainCategory) currentSession().find(StainCategory.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "New Name";
    StainCategory cat = (StainCategory) currentSession().find(StainCategory.class, id);
    assertNotEquals(name, cat.getName());
    cat.setName(name);
    sut.update(cat);

    clearSession();

    StainCategory saved = (StainCategory) currentSession().find(StainCategory.class, id);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsage() throws IOException {
    StainCategory cat = (StainCategory) currentSession().find(StainCategory.class, 1L);
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
