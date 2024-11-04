package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;

public class HibernateBoxUseDaoIT extends AbstractDAOTest {

  private HibernateBoxUseDao sut;

  @Before
  public void setup() {
    sut = new HibernateBoxUseDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    BoxUse stain = sut.get(id);
    assertNotNull(stain);
    assertEquals(id, stain.getId());
  }

  @Test
  public void testGetByAlias() throws IOException {
    String alias = "boxuse2";
    BoxUse boxUse = sut.getByAlias(alias);
    assertNotNull(boxUse);
    assertEquals(alias, boxUse.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<BoxUse> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New BoxUse";
    BoxUse boxUse = new BoxUse();
    boxUse.setAlias(alias);
    long savedId = sut.create(boxUse);

    clearSession();

    BoxUse saved = (BoxUse) currentSession().get(BoxUse.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Alias";
    BoxUse boxUse = (BoxUse) currentSession().get(BoxUse.class, id);
    assertNotEquals(alias, boxUse.getAlias());
    boxUse.setAlias(alias);
    sut.update(boxUse);

    clearSession();

    BoxUse saved = (BoxUse) currentSession().get(BoxUse.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    BoxUse boxUse = (BoxUse) currentSession().get(BoxUse.class, 1L);
    assertEquals("boxuse1", boxUse.getAlias());
    assertEquals(2L, sut.getUsage(boxUse));
  }

}
