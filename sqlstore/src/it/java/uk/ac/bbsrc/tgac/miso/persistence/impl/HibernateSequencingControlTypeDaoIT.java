package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingControlType;

public class HibernateSequencingControlTypeDaoIT extends AbstractDAOTest {

  private HibernateSequencingControlTypeDao sut;

  @Before
  public void setup() {
    sut = new HibernateSequencingControlTypeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws Exception {
    long id = 1L;
    SequencingControlType type = sut.get(id);
    assertNotNull(type);
    assertEquals(id, type.getId());
  }

  @Test
  public void testList() throws Exception {
    List<SequencingControlType> list = sut.list();
    assertNotNull(list);
    assertEquals(2, list.size());
  }

  @Test
  public void testCreate() throws Exception {
    String alias = "New Control Type";
    SequencingControlType type = new SequencingControlType();
    type.setAlias(alias);
    long savedId = sut.create(type);

    clearSession();

    SequencingControlType saved = (SequencingControlType) currentSession().get(SequencingControlType.class, savedId);
    assertNotNull(saved);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws Exception {
    String newAlias = "Changed";
    SequencingControlType type = (SequencingControlType) currentSession().get(SequencingControlType.class, 1L);
    assertNotEquals(newAlias, type.getAlias());
    type.setAlias(newAlias);
    sut.update(type);

    clearSession();

    SequencingControlType saved = (SequencingControlType) currentSession().get(SequencingControlType.class, 1L);
    assertEquals(newAlias, saved.getAlias());
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Positive";
    SequencingControlType type = sut.getByAlias(alias);
    assertNotNull(type);
    assertEquals(alias, type.getAlias());
  }

  @Test
  public void testGetUsage() throws Exception {
    SequencingControlType type = (SequencingControlType) currentSession().get(SequencingControlType.class, 1L);
    assertEquals(1L, sut.getUsage(type));
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
