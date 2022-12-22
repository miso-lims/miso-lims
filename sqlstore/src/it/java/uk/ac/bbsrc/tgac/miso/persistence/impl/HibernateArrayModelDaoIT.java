package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;

public class HibernateArrayModelDaoIT extends AbstractDAOTest {

  private HibernateArrayModelDao sut;

  @Before
  public void setup() {
    sut = new HibernateArrayModelDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    ArrayModel arrayModel = sut.get(id);
    assertNotNull(arrayModel);
    assertEquals(id, arrayModel.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    String alias = "Test BeadChip";
    ArrayModel arrayModel = sut.getByAlias(alias);
    assertNotNull(arrayModel);
    assertEquals(alias, arrayModel.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<ArrayModel> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New ArrayModel";
    ArrayModel arrayModel = new ArrayModel();
    arrayModel.setAlias(alias);
    arrayModel.setRows(3);
    arrayModel.setRows(3);
    long savedId = sut.create(arrayModel);

    clearSession();

    ArrayModel saved = (ArrayModel) getSessionFactory().getCurrentSession().get(ArrayModel.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Name";
    ArrayModel arrayModel = (ArrayModel) getSessionFactory().getCurrentSession().get(ArrayModel.class, id);
    assertNotEquals(alias, arrayModel.getAlias());
    arrayModel.setAlias(alias);
    sut.update(arrayModel);

    clearSession();

    ArrayModel saved = (ArrayModel) getSessionFactory().getCurrentSession().get(ArrayModel.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    ArrayModel arrayModel = (ArrayModel) getSessionFactory().getCurrentSession().get(ArrayModel.class, 1L);
    assertEquals("Test BeadChip", arrayModel.getAlias());
    assertEquals(1L, sut.getUsage(arrayModel));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(2L, 3L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
