package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.DilutionPaginationFilter;

public class HibernateLibraryDilutionDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateLibraryDilutionDao dao;

  private final User emptyUser = new UserImpl();

  @Before
  public void setUp() throws MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    emptyUser.setUserId(1L);
  }

  @Test
  public void testGet() throws IOException {
    final LibraryDilution ld = dao.get(1L);
    assertNotNull(ld);
    assertEquals(1L, ld.getId());
  }

  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(100L));
  }

  @Test
  public void testCount() throws IOException {
    assertEquals(14, dao.count());
  }

  @Test
  public void testGetLibraryDilutionByBarcode() throws IOException {
    final LibraryDilution ld = dao.getLibraryDilutionByBarcode("LDI2::TEST_0001_Bn_R_PE_300_WG");
    assertNotNull(ld);
  }

  @Test
  public void testGetLibraryDilutionByBarcodeNone() throws IOException {
    final LibraryDilution ld = dao.getLibraryDilutionByBarcode("nonexistant barcode");
    assertNull(ld);
  }

  @Test
  public void testGetLibraryDilutionByBarcodeNull() throws IOException {
    expectedException.expect(IOException.class);
    dao.getLibraryDilutionByBarcode(null);
  }

  @Test
  public void testListAll() throws IOException {
    assertEquals(14, dao.listAll().size());
  }

  @Test
  public void testListAllWithLimit() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    assertEquals(14, dao.list(filter, 0, 9999, false, "id").size());
    assertEquals(10, dao.list(filter, 0, 10, false, "id").size());
    assertEquals(5, dao.list(filter, 0, 5, false, "id").size());
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchByName() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("LDI3");
    filter.setPlatformType(PlatformType.ILLUMINA);
    final Collection<LibraryDilution> list = dao.list(filter, 0, 0, false, "id");
    assertEquals(1, list.size());
    assertEquals("LDI3", list.iterator().next().getName());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchByIdentificationBarcode() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("LDI1::TEST_0001_Bn_P_PE_300_WG");
    filter.setPlatformType(PlatformType.ILLUMINA);
    final Collection<LibraryDilution> list = dao.list(filter, 0, 0, false, "id");
    assertEquals(1, list.size());
    assertEquals("LDI1::TEST_0001_Bn_P_PE_300_WG", list.iterator().next().getIdentificationBarcode());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchWithEmptyQueryString() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("");
    filter.setPlatformType(PlatformType.ILLUMINA);
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchWithNullQueryString() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setPlatformType(PlatformType.ILLUMINA);
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchNoneForPlatform() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("");
    filter.setPlatformType(PlatformType.SOLID);
    assertEquals(0, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyByName() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("LDI3");
    assertEquals(1, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyByIdentificationBarcode() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("LDI1::TEST_0001_Bn_P_PE_300_WG");
    assertEquals(1, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyWithEmptyQueryString() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("");
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyWithNullQueryString() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListByLibraryId() throws IOException {
    assertEquals(1, dao.listByLibraryId(1L).size());
  }

  @Test
  public void testListByLibraryIdNone() throws IOException {
    assertEquals(0, dao.listByLibraryId(100L).size());
  }

  @Test
  public void testListAllLibraryDilutionsByPlatform() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setPlatformType(PlatformType.ILLUMINA);
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsByPlatformNone() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setPlatformType(PlatformType.SOLID);
    assertEquals(0, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectId() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setProjectId(1L);
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectIdNone() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setProjectId(100L);
    assertEquals(0, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatform() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setProjectId(1L);
    filter.setPlatformType(PlatformType.ILLUMINA);
    assertEquals(14, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatformNoneForProject() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setProjectId(100L);
    filter.setPlatformType(PlatformType.ILLUMINA);
    assertEquals(0, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatformNoneForPlatform() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setProjectId(1L);
    filter.setPlatformType(PlatformType.SOLID);

    assertEquals(0, dao.list(filter, 0, 0, false, "id").size());
  }

  @Test
  public void testRemove() throws IOException {
    final LibraryDilution ld = dao.get(1L);
    assertNotNull(ld);
    assertTrue(dao.remove(ld));
    assertNull(dao.get(1L));
  }

  @Test
  public void testSaveNew() throws IOException {

    final LibraryDilution ld = new LibraryDilution();
    final Library lib = new LibraryImpl();
    lib.setId(1L);
    ld.setLibrary(lib);
    ld.setConcentration(12.5D);
    ld.setCreationDate(new Date());
    ld.setDilutionCreator("moi");
    ld.setName("nom de plume");
    Long newId = dao.save(ld);
    final LibraryDilution saved = dao.get(newId);
    assertNotNull(saved);
    assertEquals(Double.valueOf(12.5D), saved.getConcentration());
  }

  @Test
  public void testSaveEdit() throws IOException {
    final LibraryDilution oldLd = dao.get(1L);
    oldLd.setConcentration(1.23D);
    oldLd.setSecurityProfile(new SecurityProfile());
    final Library lib = new LibraryImpl();
    lib.setId(1L);
    oldLd.setLibrary(lib);
    assertEquals(oldLd.getId(), dao.save(oldLd));
    final LibraryDilution newLd = dao.get(1L);
    assertEquals(oldLd.getId(), newLd.getId());
    assertEquals(oldLd.getConcentration(), newLd.getConcentration());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformNoSearch_100() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setPlatformType(PlatformType.ILLUMINA);
    final List<LibraryDilution> results = dao.list(filter, 0, 100, false, "name");
    assertNotNull(results);
    assertEquals(14, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformNoSearch_5() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setPlatformType(PlatformType.ILLUMINA);
    final List<LibraryDilution> results = dao.list(filter, 0, 5, false, "name");
    assertNotNull(results);
    assertEquals(5, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformSearchGeneral() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("LDI");
    filter.setPlatformType(PlatformType.ILLUMINA);
    final List<LibraryDilution> results = dao.list(filter, 0, 100, true, "name");
    assertNotNull(results);
    assertEquals(14, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformSearch_1() throws IOException {
    DilutionPaginationFilter filter = new DilutionPaginationFilter();
    filter.setQuery("LDI2");
    filter.setPlatformType(PlatformType.ILLUMINA);
    final List<LibraryDilution> results = dao.list(filter, 0, 100, true, "name");
    assertNotNull(results);
    assertEquals(1, results.size());
  }
}
