package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

public class HibernateLibraryDesignDaoIT extends AbstractDAOTest {

  private HibernateLibraryDesignDao dao;

  @BeforeEach
  public void setup() throws IOException {
    dao = new HibernateLibraryDesignDao();
    dao.setEntityManager(getEntityManager());
  }

  @Test
  public void testList() throws IOException {
    List<LibraryDesign> list = dao.list();
    assertEquals(2, list.size());
  }

  @Test
  public void testListByClass() throws IOException {
    SampleClass sc = new SampleClassImpl();
    sc.setId(1L);
    List<LibraryDesign> list = dao.listByClass(sc);
    assertEquals(1L, list.size());
  }

  @Test
  public void testListByClassNull() throws IOException {
    List<LibraryDesign> list = dao.listByClass(null);
    assertNotNull(list);
    assertEquals(0, list.size());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(dao::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(dao::listByIdList);
  }

  @Test
  public void testGet() throws IOException {
    LibraryDesign ld = dao.get(1L);
    assertNotNull(ld);
    assertEquals(1L, ld.getId());
    assertEquals("DESIGN1", ld.getName());
  }

  @Test
  public void testGetByNameAndSampleClass() throws IOException {
    String name = "DESIGN2";
    SampleClass sampleClass = (SampleClass) currentSession().find(SampleClassImpl.class, 2L);
    LibraryDesign design = dao.getByNameAndSampleClass(name, sampleClass);
    assertNotNull(design);
    assertEquals(name, design.getName());
    assertEquals(2L, design.getSampleClass().getId());
  }

  @Test
  public void testGetUsage() throws IOException {
    LibraryDesign design = (LibraryDesign) currentSession().find(LibraryDesign.class, 1L);
    assertEquals("DESIGN1", design.getName());
    assertEquals(0L, dao.getUsage(design));
  }

  @Test
  public void testCreate() throws IOException {
    String name = "NU";
    LibraryDesign design = new LibraryDesign();
    design.setName(name);
    design.setSampleClass((SampleClass) currentSession().find(SampleClassImpl.class, 1L));
    design.setLibrarySelectionType((LibrarySelectionType) currentSession().find(LibrarySelectionType.class, 1L));
    design.setLibraryStrategyType((LibraryStrategyType) currentSession().find(LibraryStrategyType.class, 1L));
    design.setLibraryDesignCode((LibraryDesignCode) currentSession().find(LibraryDesignCode.class, 1L));
    long savedId = dao.create(design);

    clearSession();

    LibraryDesign saved = (LibraryDesign) currentSession().find(LibraryDesign.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "NU";
    LibraryDesign design = (LibraryDesign) currentSession().find(LibraryDesign.class, id);
    assertNotEquals(name, design.getName());
    design.setName(name);
    dao.update(design);

    clearSession();

    LibraryDesign saved = (LibraryDesign) currentSession().find(LibraryDesign.class, id);
    assertEquals(name, saved.getName());
  }

}
