
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.persistence.IndexStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;

public class HibernateLibraryDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;
  @Mock
  private IndexStore indexStore;
  @Mock
  private SampleStore sampleStore;
  @Mock
  private ChangeLogStore changeLogDAO;

  @InjectMocks
  private HibernateLibraryDao dao;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testSave() throws Exception {

    Library library = new LibraryImpl();
    String libraryName = "newLibrary";
    library.setName(libraryName);
    library.setAlias("theAlias");
    library.setDescription("a description");
    library.setPlatformType(PlatformType.ILLUMINA);
    Sample sample = new SampleImpl();
    sample.setId(4);
    library.setSample(sample);
    LibraryType libraryType = new LibraryType();
    libraryType.setId(1L);
    library.setLibraryType(libraryType);
    LibrarySelectionType librarySelectionType = new LibrarySelectionType();
    librarySelectionType.setId(1L);
    library.setLibrarySelectionType(librarySelectionType);
    LibraryStrategyType libraryStrategyType = new LibraryStrategyType();
    libraryStrategyType.setId(1L);
    library.setLibraryStrategyType(libraryStrategyType);
    User mockUser = new UserImpl();
    mockUser.setId(1L);
    Date now = new Date();
    library.setCreator(mockUser);
    library.setCreationTime(now);
    library.setLastModifier(mockUser);
    library.setLastModified(now);

    long libraryId = dao.save(library);
    Library insertedLibrary = dao.get(libraryId);
    assertEquals(libraryName, insertedLibrary.getName());
    assertEquals("theAlias", insertedLibrary.getAlias());
    assertEquals("a description", insertedLibrary.getDescription());
    assertEquals(4, library.getSample().getId());
    assertEquals(1L, library.getLibraryType().getId());
    assertEquals(1L, library.getLibrarySelectionType().getId());
    assertEquals(1L, library.getLibraryStrategyType().getId());
  }

  @Test
  public void testGet() throws Exception {
    Library library = dao.get(3);
    assertNotNull(library);
    assertEquals("library name is incorrect", "LIB3", library.getName());
    assertEquals("library description is incorrect", "Inherited from TEST_0002", library.getDescription());
  }

  @Test
  public void testGetByBarcode() throws Exception {
    String barcode = "LIB8::TEST_0004_Bn_R_PE_300_WG";
    Library byBarcode = dao.getByBarcode(barcode);
    assertNotNull(byBarcode);
    assertEquals("barcode does not match", barcode, byBarcode.getIdentificationBarcode());
  }

  @Test
  public void testGetByBarcodeList() throws Exception {
    List<String> barcodes = Arrays.asList("LIB8::TEST_0004_Bn_R_PE_300_WG", "LIB13::TEST_0007_Bn_P_PE_300_WG");
    List<Library> byBarcodeList = dao.getByBarcodeList(barcodes);
    assertNotNull(byBarcodeList);
    for (Library library : byBarcodeList) {
      assertTrue("does not contain correct barcode", barcodes.contains(library.getIdentificationBarcode()));
    }

  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "TEST_0006_Bn_R_PE_300_WG";
    Collection<Library> byAlias = dao.listByAlias(alias);
    assertNotNull(byAlias);
    assertEquals(1, byAlias.size());
    assertEquals("alias name does not match", alias, byAlias.iterator().next().getAlias());
  }

  @Test
  public void testGetByIdentificationBarcode() throws Exception {
    String barcode = "LIB7::TEST_0004_Bn_P_PE_300_WG";
    Library byIdentificationBarcode = dao.getByBarcode("LIB7::TEST_0004_Bn_P_PE_300_WG");
    assertEquals("identiification barcode does not match", barcode, byIdentificationBarcode.getIdentificationBarcode());
  }

  @Test
  public void testListBySampleId() throws Exception {
    List<Library> libraries = dao.listBySampleId(1);

    assertEquals(1, libraries.size());
    assertEquals(1, libraries.get(0).getId());

  }

  @Test
  public void testListByProjectId() throws Exception {
    List<Library> libraries = dao.listByProjectId(1);
    List<Long> libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l);
    assertEquals(15, libraries.size());
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }
  }

  @Test
  public void testListAll() throws Exception {
    List<Library> libraries = dao.listAll();
    List<Long> libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l, 15l);
    assertEquals(15, libraries.size());
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }

  }

  @Test
  public void testCount() throws Exception {
    assertEquals("count incorrect", 15, dao.count());
  }

  @Test
  public void testListBySearch() throws Exception {
    String searchStr = "LIB";
    List<Library> libraries = dao.listBySearch(searchStr);
    assertEquals("did not find all libraries", 15, libraries.size());
  }

  @Test
  public void testListBySearch_NoResults() throws Exception {
    String searchStr = "IJOHEWF";
    List<Library> libraries = dao.listBySearch(searchStr);
    assertEquals("search returned results", 0, libraries.size());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    assertEquals(3, dao.list(5, 3, true, "id").size());
  }

  @Test
  public void testCountBySearch() throws IOException {
    assertEquals(7L, dao.countLibrariesBySearch("LIB1"));
  }

  @Test
  public void testCountByEmptySearch() throws IOException {
    assertEquals(15L, dao.countLibrariesBySearch(""));
  }

  @Test
  public void testCountByBadSearch() throws IOException {
    assertEquals(0L, dao.countLibrariesBySearch("; DROP TABLE Library;"));
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<Library> libraries = dao.list(2, 3, false, "lastModified", PaginationFilter.query("Bn_R"));
    assertEquals(3, libraries.size());
    assertEquals(10L, libraries.get(0).getId());
  }

  @Test
  public void testListByIlluminaBadSearchWithLimit() throws IOException {
    List<Library> libraries = dao.list(5, 3, true, "id", PaginationFilter.query("; DROP TABLE Library;"));
    assertEquals(0L, libraries.size());
  }

  @Test(expected = IOException.class)
  public void testListIlluminaOffsetBadLimit() throws IOException {
    dao.list(5, -3, true, "id");
  }

  @Test
  public void testListOffsetThreeWithThreeLibsPerPageOrderLastMod() throws IOException {
    List<Library> libraries = dao.list(3, 3, false, "lastModified");
    assertEquals(3, libraries.size());
    assertEquals(12, libraries.get(0).getId());
  }

  @Test
  public void testGetLibraryTypeById() throws Exception {
    LibraryType libraryTypeById = dao.getLibraryTypeById(5);

    assertEquals(5L, libraryTypeById.getId());
    assertEquals(PlatformType.LS454, libraryTypeById.getPlatformType());
    assertEquals("Rapid Shotgun", libraryTypeById.getDescription());

  }

  @Test
  public void testGetLibraryTypeByDescriptionAndPlatform() throws Exception {
    LibraryType libraryTypeByDescriptionAndPlatform = dao.getLibraryTypeByDescriptionAndPlatform("8kbp Paired End", PlatformType.LS454);
    assertEquals(4L, libraryTypeByDescriptionAndPlatform.getId());
    assertEquals(PlatformType.LS454, libraryTypeByDescriptionAndPlatform.getPlatformType());
    assertEquals("8kbp Paired End", libraryTypeByDescriptionAndPlatform.getDescription());
  }

  @Test
  public void testListLibraryTypesByPlatform() throws Exception {
    List<LibraryType> libraryTypes = dao.listLibraryTypesByPlatform(PlatformType.SOLID);
    assertEquals(5, libraryTypes.size());
    List<String> platforms = Arrays.asList("Small RNA", "Whole Transcriptome", "SAGE", "Long Mate Pair", "Fragment");
    for (LibraryType libraryType : libraryTypes) {
      assertTrue("not all platforms found", platforms.contains(libraryType.getDescription()));
    }
  }

  @Test
  public void testListAllLibraryTypes() throws Exception {
    List<LibraryType> libraryTypes = dao.listAllLibraryTypes();
    assertEquals(10, libraryTypes.size());
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("LIB1"));
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
  public void testSearchByKitName() throws IOException {
    testSearch(PaginationFilter.kitName("Test Kit"));
  }

  @Test
  public void testSearchByGroupId() throws IOException {
    testSearch(PaginationFilter.groupId("ID of group"));
  }

  @Test
  public void testSearchByDistributed() throws Exception {
    testSearch(PaginationFilter.distributed());
  }

  @Test
  public void testSearchByDistributionDate() throws Exception {
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2019-01-01"), LimsUtils.parseDate("2020-01-01"), DateType.DISTRIBUTED));
  }

  @Test
  public void testSearchByDistributionRecipient() throws Exception {
    testSearch(PaginationFilter.distributedTo("far away"));
  }

  @Test
  public void testSearchByTissueOrigin() throws Exception {
    testSearch(PaginationFilter.tissueOrigin("Ly"));
  }

  @Test
  public void testSearchByTissueType() throws Exception {
    testSearch(PaginationFilter.tissueType("P"));
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
