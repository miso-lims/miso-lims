package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;

public class HibernatePartitionQcTypeDaoIT extends AbstractDAOTest {

  private HibernatePartitionQcTypeDao sut;

  @Before
  public void setup() {
    sut = new HibernatePartitionQcTypeDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws IOException {
    long typeId = 1L;
    PartitionQCType type = sut.get(typeId);
    assertNotNull(type);
    assertEquals(typeId, type.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    String desc = "Failed: Instrument problem";
    PartitionQCType st = sut.getByDescription(desc);
    assertNotNull(st);
    assertEquals(desc, st.getDescription());
  }

  @Test
  public void testList() throws IOException {
    List<PartitionQCType> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());
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
    String desc = "New Type";
    PartitionQCType st = new PartitionQCType();
    st.setDescription(desc);
    long savedId = sut.create(st);

    clearSession();

    PartitionQCType saved = (PartitionQCType) getSessionFactory().getCurrentSession().get(PartitionQCType.class, savedId);
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testUpdate() throws IOException {
    long typeId = 1L;
    String newDesc = "New Desc";
    PartitionQCType st = (PartitionQCType) getSessionFactory().getCurrentSession().get(PartitionQCType.class, typeId);
    assertNotEquals(newDesc, st.getDescription());
    st.setDescription(newDesc);
    assertEquals(typeId, sut.update(st));

    clearSession();

    PartitionQCType saved = (PartitionQCType) getSessionFactory().getCurrentSession().get(PartitionQCType.class, typeId);
    assertEquals(newDesc, saved.getDescription());
  }

  @Test
  public void testGetUsage() throws IOException {
    PartitionQCType ok = (PartitionQCType) getSessionFactory().getCurrentSession().get(PartitionQCType.class, 1L);
    assertEquals("OK", ok.getDescription());
    assertEquals(1L, sut.getUsage(ok));
  }

}
