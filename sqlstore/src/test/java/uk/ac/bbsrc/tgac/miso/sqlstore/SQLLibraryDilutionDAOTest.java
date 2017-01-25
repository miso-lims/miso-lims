package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDilutionDao;

public class SQLLibraryDilutionDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;
  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private HibernateLibraryDilutionDao dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 15L;

  @Before
  public void setUp() throws MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    Mockito.when(namingScheme.validateName(Mockito.anyString())).thenReturn(ValidationResult.success());
  }

  private void mockAutoIncrement(long value) {
    final Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
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
    expectedException.expect(NullPointerException.class);
    dao.getLibraryDilutionByBarcode(null);
  }

  @Test
  public void testListAll() throws IOException {
    assertEquals(14, dao.listAll().size());
  }

  @Test
  public void testListAllWithLimit() throws IOException {
    assertEquals(14, dao.listAllWithLimit(9999).size());
    assertEquals(10, dao.listAllWithLimit(10L).size());
    assertEquals(5, dao.listAllWithLimit(5L).size());
    assertEquals(0, dao.listAllWithLimit(0L).size());
    assertEquals(14, dao.listAllWithLimit(-1L).size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchByName() throws IOException {
    final Collection<LibraryDilution> list = dao.listAllLibraryDilutionsBySearchAndPlatform("LDI3", PlatformType.ILLUMINA);
    assertEquals(1, list.size());
    assertEquals("LDI3", list.iterator().next().getName());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchByIdentificationBarcode() throws IOException {
    final Collection<LibraryDilution> list = dao.listAllLibraryDilutionsBySearchAndPlatform("LDI1::TEST_0001_Bn_P_PE_300_WG",
        PlatformType.ILLUMINA);
    assertEquals(1, list.size());
    assertEquals("LDI1::TEST_0001_Bn_P_PE_300_WG", list.iterator().next().getIdentificationBarcode());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchWithEmptyQueryString() throws IOException {
    assertEquals(14, dao.listAllLibraryDilutionsBySearchAndPlatform("", PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchWithNullQueryString() throws IOException {
    assertEquals(14, dao.listAllLibraryDilutionsBySearchAndPlatform(null, PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchNoneForPlatform() throws IOException {
    assertEquals(0, dao.listAllLibraryDilutionsBySearchAndPlatform("", PlatformType.SOLID).size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchWithNullPlatformType() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.listAllLibraryDilutionsBySearchAndPlatform("", null).size();
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyByName() throws IOException {
    assertEquals(1, dao.listAllLibraryDilutionsBySearchOnly("LDI3").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyByIdentificationBarcode() throws IOException {
    assertEquals(1, dao.listAllLibraryDilutionsBySearchOnly("LDI1::TEST_0001_Bn_P_PE_300_WG").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyWithEmptyQueryString() throws IOException {
    assertEquals(14, dao.listAllLibraryDilutionsBySearchOnly("").size());
  }

  @Test
  public void testListAllLibraryDilutionsBySearchOnlyWithNullQueryString() throws IOException {
    assertEquals(14, dao.listAllLibraryDilutionsBySearchOnly(null).size());
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
    assertEquals(14, dao.listAllLibraryDilutionsByPlatform(PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListAllLibraryDilutionsByPlatformNone() throws IOException {
    assertEquals(0, dao.listAllLibraryDilutionsByPlatform(PlatformType.SOLID).size());
  }

  @Test
  public void testListAllLibraryDilutionsByPlatformNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.listAllLibraryDilutionsByPlatform(null);
  }

  @Test
  public void testListAllLibraryDilutionsByProjectId() throws IOException {
    assertEquals(14, dao.listAllLibraryDilutionsByProjectId(1L).size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectIdNone() throws IOException {
    assertEquals(0, dao.listAllLibraryDilutionsByProjectId(100L).size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatform() throws IOException {
    assertEquals(14, dao.listAllLibraryDilutionsByProjectAndPlatform(1L, PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatformNoneForProject() throws IOException {
    assertEquals(0, dao.listAllLibraryDilutionsByProjectAndPlatform(100L, PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatformNoneForPlatform() throws IOException {
    assertEquals(0, dao.listAllLibraryDilutionsByProjectAndPlatform(1L, PlatformType.SOLID).size());
  }

  @Test
  public void testListAllLibraryDilutionsByProjectAndPlatformNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.listAllLibraryDilutionsByProjectAndPlatform(1L, null);
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
    final long autoIncrementId = nextAutoIncrementId;
    assertNull(dao.get(autoIncrementId));

    final LibraryDilution ld = new LibraryDilution();
    final Library lib = new LibraryImpl();
    lib.setId(1L);
    ld.setLibrary(lib);
    ld.setConcentration(12.5D);
    mockAutoIncrement(autoIncrementId);
    assertEquals(autoIncrementId, dao.save(ld));
    final LibraryDilution saved = dao.get(autoIncrementId);
    assertNotNull(saved);
    assertEquals(Double.valueOf(12.5D), saved.getConcentration());
    nextAutoIncrementId++;
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
    final List<LibraryDilution> results = dao.listBySearchOffsetAndNumResultsAndPlatform(0, 100, null, "DESC", "name",
        PlatformType.ILLUMINA);
    assertNotNull(results);
    assertEquals(14, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformNoSearch_5() throws IOException {
    final List<LibraryDilution> results = dao.listBySearchOffsetAndNumResultsAndPlatform(0, 5, null, "DESC", "name", PlatformType.ILLUMINA);
    assertNotNull(results);
    assertEquals(5, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformSearchGeneral() throws IOException {
    final List<LibraryDilution> results = dao.listBySearchOffsetAndNumResultsAndPlatform(0, 100, "LDI", "ASC", "name",
        PlatformType.ILLUMINA);
    assertNotNull(results);
    assertEquals(14, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformSearch_1() throws IOException {
    final List<LibraryDilution> results = dao.listBySearchOffsetAndNumResultsAndPlatform(0, 100, "LDI2", "ASC", "name",
        PlatformType.ILLUMINA);
    assertNotNull(results);
    assertEquals(1, results.size());
  }
}
