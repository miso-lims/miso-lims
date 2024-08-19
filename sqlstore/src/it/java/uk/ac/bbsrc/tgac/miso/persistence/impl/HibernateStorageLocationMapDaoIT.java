package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StorageLocationMap;

public class HibernateStorageLocationMapDaoIT extends AbstractDAOTest {

  private HibernateStorageLocationMapDao sut;

  @Before
  public void setup() {
    sut = new HibernateStorageLocationMapDao();
    sut.setEntityManager(getEntityManager());
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

    StorageLocationMap saved =
        (StorageLocationMap) currentSession().get(StorageLocationMap.class, savedId);
    assertEquals(filename, saved.getFilename());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String filename = "renamed.html";
    StorageLocationMap map =
        (StorageLocationMap) currentSession().get(StorageLocationMap.class, id);
    assertNotEquals(filename, map.getFilename());
    map.setFilename(filename);
    sut.update(map);

    clearSession();

    StorageLocationMap saved =
        (StorageLocationMap) currentSession().get(StorageLocationMap.class, id);
    assertEquals(filename, saved.getFilename());
  }

  @Test
  public void testGetUsage() throws IOException {
    StorageLocationMap map =
        (StorageLocationMap) currentSession().get(StorageLocationMap.class, 1L);
    assertEquals("floor_one.html", map.getFilename());
    assertEquals(2L, sut.getUsage(map));
  }

}
