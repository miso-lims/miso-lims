package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;

public class HibernateLibraryDesignCodeDaoIT extends AbstractDAOTest {

  private HibernateLibraryDesignCodeDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibraryDesignCodeDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    LibraryDesignCode designCode = sut.get(id);
    assertNotNull(designCode);
    assertEquals(id, designCode.getId());
  }

  @Test
  public void testGetByCode() throws IOException {
    String code = "TT";
    LibraryDesignCode designCode = sut.getByCode(code);
    assertNotNull(designCode);
    assertEquals(code, designCode.getCode());
  }

  @Test
  public void testList() throws IOException {
    List<LibraryDesignCode> list = sut.list();
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
    String code = "NU";
    LibraryDesignCode designCode = new LibraryDesignCode();
    designCode.setCode(code);
    designCode.setDescription("New Desc");
    designCode.setTargetedSequencingRequired(false);
    long savedId = sut.create(designCode);

    clearSession();

    LibraryDesignCode saved =
        (LibraryDesignCode) currentSession().get(LibraryDesignCode.class, savedId);
    assertEquals(code, saved.getCode());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String code = "NU";
    LibraryDesignCode designCode =
        (LibraryDesignCode) currentSession().get(LibraryDesignCode.class, id);
    assertNotEquals(code, designCode.getCode());
    designCode.setCode(code);
    sut.update(designCode);

    clearSession();

    LibraryDesignCode saved =
        (LibraryDesignCode) currentSession().get(LibraryDesignCode.class, id);
    assertEquals(code, saved.getCode());
  }

  @Test
  public void testGetUsageByLibraries() throws IOException {
    LibraryDesignCode designCode =
        (LibraryDesignCode) currentSession().get(LibraryDesignCode.class, 1L);
    assertEquals("TT", designCode.getCode());
    assertEquals(1L, sut.getUsageByLibraries(designCode));
  }

  @Test
  public void testGetUsageByLibraryDesigns() throws IOException {
    LibraryDesignCode designCode =
        (LibraryDesignCode) currentSession().get(LibraryDesignCode.class, 1L);
    assertEquals("TT", designCode.getCode());
    assertEquals(2L, sut.getUsageByLibraryDesigns(designCode));
  }

}
