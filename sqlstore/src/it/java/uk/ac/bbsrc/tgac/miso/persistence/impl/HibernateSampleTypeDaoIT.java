package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.SampleType;

public class HibernateSampleTypeDaoIT extends AbstractDAOTest {

  private HibernateSampleTypeDao sut;

  @BeforeEach
  public void setup() {
    sut = new HibernateSampleTypeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long typeId = 1L;
    SampleType st = sut.get(typeId);
    assertNotNull(st);
    assertEquals(typeId, st.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    SampleType st = sut.getByName("GENOMIC");
    assertNotNull(st);
    assertEquals("GENOMIC", st.getName());
  }

  @Test
  public void testList() throws IOException {
    List<SampleType> list = sut.list();
    assertNotNull(list);
    assertEquals(8, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String name = "New Sample Type";
    SampleType st = new SampleType();
    st.setName(name);
    long savedId = sut.create(st);

    clearSession();

    SampleType saved = (SampleType) currentSession().find(SampleType.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long typeId = 1L;
    String newName = "New Name";
    SampleType st = (SampleType) currentSession().find(SampleType.class, typeId);
    assertNotEquals(newName, st.getName());
    st.setName(newName);
    assertEquals(typeId, sut.update(st));

    clearSession();

    SampleType saved = (SampleType) currentSession().find(SampleType.class, typeId);
    assertEquals(newName, saved.getName());
  }

  @Test
  public void testGetUsage() throws IOException {
    SampleType genomic = (SampleType) currentSession().find(SampleType.class, 1L);
    assertEquals("GENOMIC", genomic.getName());
    assertTrue(sut.getUsage(genomic) > 10L);

    SampleType synthetic = (SampleType) currentSession().find(SampleType.class, 3L);
    assertEquals("SYNTHETIC", synthetic.getName());
    assertEquals(0L, sut.getUsage(synthetic));
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(2L, 1L, 5L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
