package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;

public class HibernateAttachmentCategoryDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateAttachmentCategoryDao sut;

  @Before
  public void setup() {
    sut = new HibernateAttachmentCategoryDao();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void testGet() throws IOException {
    AttachmentCategory cat = sut.get(1L);
    assertNotNull(cat);
    assertEquals(1L, cat.getId());
    assertEquals("Category One", cat.getAlias());
  }

  @Test
  public void testGetByAlias() throws IOException {
    String alias = "Category Two";
    AttachmentCategory cat = sut.getByAlias(alias);
    assertNotNull(cat);
    assertEquals(2L, cat.getId());
    assertEquals(alias, cat.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<AttachmentCategory> list = sut.list();
    assertEquals(2, list.size());
    assertTrue(list.stream().anyMatch(cat -> cat.getAlias().equals("Category One")));
    assertTrue(list.stream().anyMatch(cat -> cat.getAlias().equals("Category Two")));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "test";
    AttachmentCategory cat = new AttachmentCategory();
    cat.setAlias(alias);
    assertFalse(cat.isSaved());
    sut.create(cat);
    assertTrue(cat.isSaved());

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

    AttachmentCategory saved =
        (AttachmentCategory) entityManager.unwrap(Session.class).get(AttachmentCategory.class, cat.getId());
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    String alias = "changed";
    AttachmentCategory cat = (AttachmentCategory) entityManager.unwrap(Session.class).get(AttachmentCategory.class, 1L);
    assertNotEquals(alias, cat.getAlias());
    cat.setAlias(alias);
    assertTrue(cat.isSaved());
    sut.update(cat);

    entityManager.unwrap(Session.class).flush();
    entityManager.unwrap(Session.class).clear();

    AttachmentCategory saved =
        (AttachmentCategory) entityManager.unwrap(Session.class).get(AttachmentCategory.class, cat.getId());
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() {
    AttachmentCategory cat = (AttachmentCategory) entityManager.unwrap(Session.class).get(AttachmentCategory.class, 1L);
    assertNotNull(cat);
    long usage = sut.getUsage(cat);
    assertEquals(3L, usage);
  }

}
