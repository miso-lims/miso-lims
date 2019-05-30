package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;

public class HibernateBoxSizeDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateBoxSizeDao sut;

  @Before
  public void setup() {
    sut = new HibernateBoxSizeDao();
    sut.setSessionFactory(sessionFactory);
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
    assertEquals(1, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    int rows = 3;
    int cols = 2;
    BoxSize boxSize = new BoxSize();
    boxSize.setRows(rows);
    boxSize.setColumns(cols);
    long savedId = sut.create(boxSize);

    clearSession();

    BoxSize saved = (BoxSize) sessionFactory.getCurrentSession().get(BoxSize.class, savedId);
    assertEquals(rows, saved.getRows().intValue());
    assertEquals(cols, saved.getColumns().intValue());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    int rows = 7;
    int cols = 5;
    BoxSize boxSize = (BoxSize) sessionFactory.getCurrentSession().get(BoxSize.class, id);
    assertNotEquals(rows, boxSize.getRows().intValue());
    assertNotEquals(cols, boxSize.getColumns().intValue());
    boxSize.setRows(rows);
    boxSize.setColumns(cols);
    sut.update(boxSize);

    clearSession();

    BoxSize saved = (BoxSize) sessionFactory.getCurrentSession().get(BoxSize.class, id);
    assertEquals(rows, saved.getRows().intValue());
    assertEquals(cols, saved.getColumns().intValue());
  }

  @Test
  public void testGetUsage() throws IOException {
    BoxSize boxSize = (BoxSize) sessionFactory.getCurrentSession().get(BoxSize.class, 1L);
    assertNotNull(boxSize);
    assertEquals(2L, sut.getUsage(boxSize));
  }

  private void clearSession() {
    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();
  }

}
