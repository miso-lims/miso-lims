package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
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

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateLibraryAliquotDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateLibraryAliquotDao dao;

  private final User emptyUser = new UserImpl();

  @Before
  public void setUp() throws MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    emptyUser.setId(1L);
  }

  @Test
  public void testGet() throws IOException {
    final LibraryAliquot ld = dao.get(1L);
    assertNotNull(ld);
    assertEquals(1L, ld.getId());
  }

  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(100L));
  }

  @Test
  public void testCount() throws IOException {
    assertEquals(15, dao.count());
  }

  @Test
  public void testGetLibraryAliquotByBarcode() throws IOException {
    final LibraryAliquot ld = dao.getByBarcode("LDI2::TEST_0001_Bn_R_PE_300_WG");
    assertNotNull(ld);
  }

  @Test
  public void testGetLibraryAliquotByBarcodeNone() throws IOException {
    final LibraryAliquot ld = dao.getByBarcode("nonexistant barcode");
    assertNull(ld);
  }

  @Test
  public void testGetLibraryAliquotByBarcodeNull() throws IOException {
    expectedException.expect(IOException.class);
    dao.getByBarcode(null);
  }

  @Test
  public void testListAll() throws IOException {
    assertEquals(15, dao.listAll().size());
  }

  @Test
  public void testListAllWithLimit() throws IOException {
    assertEquals(15, dao.list(0, 9999, false, "id").size());
    assertEquals(10, dao.list(0, 10, false, "id").size());
    assertEquals(5, dao.list(0, 5, false, "id").size());
    assertEquals(15, dao.list(0, 0, false, "id").size());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchByName() throws IOException {
    final Collection<LibraryAliquot> list = dao.list(0, 0, false, "id", PaginationFilter.query("LDI3"),
        PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertEquals(1, list.size());
    assertEquals("LDI3", list.iterator().next().getName());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchByIdentificationBarcode() throws IOException {
    final Collection<LibraryAliquot> list = dao.list(0, 0, false, "id", PaginationFilter.query("LDI1::TEST_0001_Bn_P_PE_300_WG"),
        PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertEquals(1, list.size());
    assertEquals("LDI1::TEST_0001_Bn_P_PE_300_WG", list.iterator().next().getIdentificationBarcode());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchWithEmptyQueryString() throws IOException {
    assertEquals(15, dao.list(0, 0, false, "id", PaginationFilter.query(""),
        PaginationFilter.platformType(PlatformType.ILLUMINA)).size());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchWithNullQueryString() throws IOException {
    assertEquals(15, dao.list(0, 0, false, "id",
        PaginationFilter.platformType(PlatformType.ILLUMINA)).size());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchNoneForPlatform() throws IOException {
    assertEquals(0, dao.list(0, 0, false, "id", PaginationFilter.query(""),
        PaginationFilter.platformType(PlatformType.SOLID)).size());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchOnlyByName() throws IOException {
    assertEquals(1, dao.list(0, 0, false, "id", PaginationFilter.query("LDI3")).size());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchOnlyByIdentificationBarcode() throws IOException {
    assertEquals(1, dao.list(0, 0, false, "id", PaginationFilter.query("LDI1::TEST_0001_Bn_P_PE_300_WG")).size());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchOnlyWithEmptyQueryString() throws IOException {
    assertEquals(15, dao.list(0, 0, false, "id", PaginationFilter.query("")).size());
  }

  @Test
  public void testListAllLibraryAliquotsBySearchOnlyWithNullQueryString() throws IOException {
    assertEquals(15, dao.list(0, 0, false, "id", PaginationFilter.query(null)).size());
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
  public void testListAllLibraryAliquotsByPlatform() throws IOException {
    assertEquals(15, dao.list(0, 0, false, "id",
        PaginationFilter.platformType(PlatformType.ILLUMINA)).size());
  }

  @Test
  public void testListAllLibraryAliquotsByPlatformNone() throws IOException {
    assertEquals(0, dao.list(0, 0, false, "id",
        PaginationFilter.platformType(PlatformType.SOLID)).size());
  }

  @Test
  public void testListAllLibraryAliquotsByProjectId() throws IOException {
    assertEquals(15, dao.list(0, 0, false, "id", PaginationFilter.project(1L)).size());
  }

  @Test
  public void testListAllLibraryAliquotsByProjectIdNone() throws IOException {
    assertEquals(0, dao.list(0, 0, false, "id", PaginationFilter.project(100L)).size());
  }

  @Test
  public void testListAllLibraryAliquotsByProjectAndPlatform() throws IOException {
    assertEquals(15, dao.list(0, 0, false, "id", PaginationFilter.project(1L),
        PaginationFilter.platformType(PlatformType.ILLUMINA)).size());
  }

  @Test
  public void testListAllLibraryAliquotsByProjectAndPlatformNoneForProject() throws IOException {
    assertEquals(0, dao.list(0, 0, false, "id", PaginationFilter.project(100L),
        PaginationFilter.platformType(PlatformType.ILLUMINA)).size());
  }

  @Test
  public void testListAllLibraryAliquotsByProjectAndPlatformNoneForPlatform() throws IOException {
    assertEquals(0, dao.list(0, 0, false, "id", PaginationFilter.project(1L),
        PaginationFilter.platformType(PlatformType.SOLID)).size());
  }

  @Test
  public void testSaveNew() throws IOException {

    final LibraryAliquot ld = new LibraryAliquot();
    final Library lib = new LibraryImpl();
    UserImpl user = new UserImpl();
    user.setId(1L);
    user.setFullName("moi");
    lib.setId(1L);
    ld.setCreator(user);
    ld.setLastModifier(user);
    ld.setLibrary(lib);
    ld.setConcentration(new BigDecimal("12.5"));
    ld.setCreationDate(new Date());
    ld.setCreationTime(new Date());
    ld.setName("nom de plume");
    ld.setAlias("TEST");
    Long newId = dao.save(ld);
    final LibraryAliquot saved = dao.get(newId);
    assertNotNull(saved);
    assertEquals(new BigDecimal("12.5"), saved.getConcentration());
  }

  @Test
  public void testSaveEdit() throws IOException {
    final LibraryAliquot oldLd = dao.get(1L);
    oldLd.setConcentration(new BigDecimal("1.23"));
    final Library lib = new LibraryImpl();
    lib.setId(1L);
    oldLd.setLibrary(lib);
    assertEquals(oldLd.getId(), dao.save(oldLd));
    final LibraryAliquot newLd = dao.get(1L);
    assertEquals(oldLd.getId(), newLd.getId());
    assertEquals(oldLd.getConcentration(), newLd.getConcentration());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformNoSearch_100() throws IOException {
    final List<LibraryAliquot> results = dao.list(0, 100, false, "name",
        PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertNotNull(results);
    assertEquals(15, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformNoSearch_5() throws IOException {
    final List<LibraryAliquot> results = dao.list(0, 5, false, "name",
        PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertNotNull(results);
    assertEquals(5, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformSearchGeneral() throws IOException {
    final List<LibraryAliquot> results = dao.list(0, 100, true, "name", PaginationFilter.query("LDI"),
        PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertNotNull(results);
    assertEquals(15, results.size());
  }

  @Test
  public void testListBySearchOffsetAndNumResultsAndPlatformSearch_1() throws IOException {
    final List<LibraryAliquot> results = dao.list(0, 100, true, "name", PaginationFilter.query("LDI2"),
        PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertNotNull(results);
    assertEquals(1, results.size());
  }
  
  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("LDI1"));
  }

  @Test
  public void testSearchByCreated() throws IOException {
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2017-01-01"), LimsUtils.parseDate("2018-01-01"), DateType.CREATE));
  }

  @Test
  public void testSearchByEntered() throws IOException {
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2017-01-01"), LimsUtils.parseDate("2018-01-01"), DateType.ENTERED));
  }

  @Test
  public void testSearchByCreator() throws IOException {
    testSearch(PaginationFilter.user("admin", true));
  }

  @Test
  public void testSearchByModifier() throws IOException {
    testSearch(PaginationFilter.user("admin", false));
  }

  @Test
  public void testSearchByPlatform() throws IOException {
    testSearch(PaginationFilter.platformType(PlatformType.ILLUMINA));
  }

  @Test
  public void testSearchByIndices() throws IOException {
    testSearch(PaginationFilter.index("A501"));
    testSearch(PaginationFilter.index("ACGTACGT"));
  }

  @Test
  public void testSearchByBox() throws IOException {
    testSearch(PaginationFilter.box("BOX1"));
  }

  @Test
  public void testSearchByFreezer() throws Exception {
    testSearch(PaginationFilter.freezer("freezer1"));
  }

  @Test
  public void testSearchByPool() throws Exception {
    testSearch(PaginationFilter.pool(1L));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(dao.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "name", filter));
  }

}
