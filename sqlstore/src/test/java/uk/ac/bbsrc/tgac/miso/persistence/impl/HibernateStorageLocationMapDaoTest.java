package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;

public class HibernateStorageLocationMapDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateStorageLocationMapDao sut;

  @Before
  public void setup() {
    sut = new HibernateStorageLocationMapDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    StorageLocationMap map = sut.get(id);
    assertNotNull(map);
    assertEquals(id, map.getId());
  }

  @Test
  public void testGetByFilename() throws IOException {
    String name = "floor_one.html";
    StorageLocationMap map = sut.getByFilename(name);
    assertNotNull(map);
    assertEquals(name, map.getFilename());
  }

  @Test
  public void testList() throws IOException {
    List<StorageLocationMap> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String filename = "new.html";
    StorageLocationMap map = new StorageLocationMap();
    map.setFilename(filename);
    long savedId = sut.create(map);

    clearSession();

    StorageLocationMap saved = (StorageLocationMap) sessionFactory.getCurrentSession().get(StorageLocationMap.class, savedId);
    assertEquals(filename, saved.getFilename());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String filename = "renamed.html";
    StorageLocationMap map = (StorageLocationMap) sessionFactory.getCurrentSession().get(StorageLocationMap.class, id);
    assertNotEquals(filename, map.getFilename());
    map.setFilename(filename);
    sut.update(map);

    clearSession();

    StorageLocationMap saved = (StorageLocationMap) sessionFactory.getCurrentSession().get(StorageLocationMap.class, id);
    assertEquals(filename, saved.getFilename());
  }

  @Test
  public void testGetUsage() throws IOException {
    StorageLocationMap map = (StorageLocationMap) sessionFactory.getCurrentSession().get(StorageLocationMap.class, 1L);
    assertEquals("floor_one.html", map.getFilename());
    assertEquals(0L, sut.getUsage(map));
  }

  private void clearSession() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }

}
