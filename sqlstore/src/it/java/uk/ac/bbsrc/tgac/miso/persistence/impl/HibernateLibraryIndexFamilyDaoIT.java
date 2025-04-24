package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public class HibernateLibraryIndexFamilyDaoIT extends AbstractDAOTest {

  private HibernateLibraryIndexFamilyDao sut;

  @Before
  public void setup() {
    sut = new HibernateLibraryIndexFamilyDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testGet() throws IOException {
    long id = 1L;
    LibraryIndexFamily fam = sut.get(id);
    assertNotNull(fam);
    assertEquals(id, fam.getId());
  }

  @Test
  public void testGetByName() throws IOException {
    String name = "NEXTflex 8bp";
    LibraryIndexFamily fam = sut.getByName(name);
    assertNotNull(fam);
    assertEquals(name, fam.getName());
  }

  @Test
  public void testList() throws IOException {
    List<LibraryIndexFamily> list = sut.list();
    assertNotNull(list);
    assertEquals(12, list.size());
  }

  @Test
  public void testCreate() throws IOException {
    String name = "New Family";
    LibraryIndexFamily fam = new LibraryIndexFamily();
    fam.setName(name);
    fam.setPlatformType(PlatformType.ILLUMINA);
    fam.setArchived(false);
    long savedId = sut.create(fam);

    clearSession();

    LibraryIndexFamily saved = (LibraryIndexFamily) currentSession().get(LibraryIndexFamily.class, savedId);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String name = "New Name";
    LibraryIndexFamily fam = (LibraryIndexFamily) currentSession().get(LibraryIndexFamily.class, id);
    assertNotEquals(name, fam.getName());
    fam.setName(name);
    sut.update(fam);

    clearSession();

    LibraryIndexFamily saved = (LibraryIndexFamily) currentSession().get(LibraryIndexFamily.class, id);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testGetUsage() throws IOException {
    LibraryIndexFamily fam = (LibraryIndexFamily) currentSession().get(LibraryIndexFamily.class, 1L);
    assertEquals("TruSeq Single Index", fam.getName());
    assertEquals(14L, sut.getUsage(fam));
  }

}
