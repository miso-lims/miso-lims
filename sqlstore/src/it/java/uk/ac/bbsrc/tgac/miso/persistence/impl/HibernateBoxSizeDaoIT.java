package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize.BoxType;

public class HibernateBoxSizeDaoIT extends AbstractDAOTest {

  private HibernateBoxSizeDao sut;

  @Before
  public void setup() {
    sut = new HibernateBoxSizeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    BoxSize stain = sut.get(id);
    assertNotNull(stain);
    assertEquals(id, stain.getId());
  }

  @Test
  public void testList() throws IOException {
    List<BoxSize> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    int rows = 3;
    int cols = 2;
    BoxSize boxSize = new BoxSize();
    boxSize.setRows(rows);
    boxSize.setColumns(cols);
    boxSize.setBoxType(BoxType.STORAGE);
    long savedId = sut.create(boxSize);

    clearSession();

    BoxSize saved = (BoxSize) currentSession().get(BoxSize.class, savedId);
    assertEquals(rows, saved.getRows().intValue());
    assertEquals(cols, saved.getColumns().intValue());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    int rows = 7;
    int cols = 5;
    BoxSize boxSize = (BoxSize) currentSession().get(BoxSize.class, id);
    assertNotEquals(rows, boxSize.getRows().intValue());
    assertNotEquals(cols, boxSize.getColumns().intValue());
    boxSize.setRows(rows);
    boxSize.setColumns(cols);
    sut.update(boxSize);

    clearSession();

    BoxSize saved = (BoxSize) currentSession().get(BoxSize.class, id);
    assertEquals(rows, saved.getRows().intValue());
    assertEquals(cols, saved.getColumns().intValue());
  }

  @Test
  public void testGetUsage() throws IOException {
    BoxSize boxSize = (BoxSize) currentSession().get(BoxSize.class, 1L);
    assertNotNull(boxSize);
    assertEquals(2L, sut.getUsage(boxSize));
  }

}
