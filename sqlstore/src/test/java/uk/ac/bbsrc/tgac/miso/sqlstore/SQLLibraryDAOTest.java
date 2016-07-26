package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import net.sf.ehcache.CacheManager;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.TagBarcodeStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAdditionalInfoDao;

public class SQLLibraryDAOTest extends AbstractDAOTest {

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private SecurityStore securityDAO;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  @Mock
  private LibraryDilutionStore libraryDilutionStore;
  @Mock
  private SQLLibraryQCDAO libraryQCDAO;
  @Mock
  private NoteStore noteStore;
  @Mock
  private MisoNamingScheme<Library> libraryNamingSchema;
  @Mock
  private TagBarcodeStore tagBarcodeStore;
  @Mock
  private SampleStore sampleStore;
  @Mock
  private LibraryAdditionalInfoDao libraryAdditionalInfoDao;
  @Mock
  private SQLChangeLogDAO changeLogDAO;

  @InjectMocks
  private SQLLibraryDAO dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 15L;

  @Before
  public void setup() throws IOException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
    when(tagBarcodeStore.getTagBarcodeById(Matchers.anyLong())).thenReturn(new TagBarcode());
    when(sampleStore.get(Matchers.anyLong())).thenReturn(new SampleImpl());
    when(libraryAdditionalInfoDao.getLibraryAdditionalInfoByLibraryId(Matchers.anyLong())).thenReturn(new LibraryAdditionalInfoImpl());
  }

  @Test
  public void testSave() throws Exception {

    Library library = new LibraryImpl();
    String libraryName = "newLibrary";
    library.setName(libraryName);
    library.setAlias("theAlias");
    library.setDescription("a description");
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
    User mockUser = mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);
    library.setLastModifier(mockUser);

    mockAutoIncrement(nextAutoIncrementId);
    when(libraryNamingSchema.validateField(anyString(), anyString())).thenReturn(true);
    when(libraryNamingSchema.generateNameFor("name", library)).thenReturn(libraryName);

    long libraryId = dao.save(library);
    Library insertedLibrary = dao.get(libraryId);
    assertEquals(libraryName, insertedLibrary.getName());
    assertEquals("theAlias", insertedLibrary.getAlias());
    assertEquals("a description", insertedLibrary.getDescription());
    assertEquals(4, library.getSample().getId());
    assertEquals(Long.valueOf(1), library.getLibraryType().getId());
    assertEquals(Long.valueOf(1), library.getLibrarySelectionType().getId());
    assertEquals(Long.valueOf(1), library.getLibraryStrategyType().getId());
    nextAutoIncrementId++;
  }

  private void mockAutoIncrement(long value) throws Exception {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
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
    Library byAlias = dao.getByAlias(alias);
    assertNotNull(byAlias);
    assertEquals("alias name does not match", alias, byAlias.getAlias());
  }

  @Test
  public void testGetByPositionId() throws Exception {
  }

  @Test
  public void testLazyGet() throws Exception {
    Library library = dao.lazyGet(13);
    assertEquals(13, library.getId());
    assertEquals("Inherited from TEST_0007", library.getDescription());
  }

  @Test
  public void testGetByIdentificationBarcode() throws Exception {
    String barcode = "LIB7::TEST_0004_Bn_P_PE_300_WG";
    Library byIdentificationBarcode = dao.getByIdentificationBarcode("LIB7::TEST_0004_Bn_P_PE_300_WG");
    assertEquals("identiification barcode does not match", barcode, byIdentificationBarcode.getIdentificationBarcode());
  }

  @Test
  public void testListByLibraryDilutionId() throws Exception {

    List<Library> libraries = dao.listByLibraryDilutionId(9);
    assertEquals(1, libraries.size());
    assertEquals(9, libraries.get(0).getId());
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
    List libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l);
    assertEquals(14, libraries.size());
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }
  }

  @Test
  public void testListAll() throws Exception {
    List<Library> libraries = dao.listAll();
    List libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l);
    assertEquals(14, libraries.size());
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }

  }

  @Test
  public void testListAllWithLimit() throws Exception {
    List<Library> libraries = dao.listAllWithLimit(5);
    assertEquals("not within limit", 5, libraries.size());

  }

  @Test
  public void testCount() throws Exception {
    assertEquals("count incorrect", 14, dao.count());
  }

  @Test
  public void testListBySearch() throws Exception {
    String searchStr = "LIB";
    List<Library> libraries = dao.listBySearch(searchStr);
    assertEquals("did not find all libraries", 14, libraries.size());
  }

  @Test
  public void testListBySearch_NoResults() throws Exception {
    String searchStr = "IJOHEWF";
    List<Library> libraries = dao.listBySearch(searchStr);
    assertEquals("search returned results", 0, libraries.size());
  }

  @Test
  public void testListWithLimitAndOffset() throws IOException {
    assertEquals(3, dao.listByOffsetAndNumResults(5, 3, "ASC", "id").size());
  }

  @Test
  public void testCountBySearch() throws IOException {
    assertEquals(6L, dao.countLibrariesBySearch("LIB1"));
  }

  @Test
  public void testCountByEmptySearch() throws IOException {
    assertEquals(14L, dao.countLibrariesBySearch(""));
  }

  @Test
  public void testCountByBadSearch() throws IOException {
    assertEquals(0L, dao.countLibrariesBySearch("; DROP TABLE Library;"));
  }

  @Test
  public void testListBySearchWithLimit() throws IOException {
    List<Library> libraries = dao.listBySearchOffsetAndNumResults(2, 3, "Bn_R", "desc", "lastModified");
    assertEquals(3, libraries.size());
    assertEquals(10L, libraries.get(0).getId());
  }

  @Test
  public void testListByIlluminaBadSearchWithLimit() throws IOException {
    List<Library> libraries = dao.listBySearchOffsetAndNumResults(5, 3, "; DROP TABLE Library;", "asc", "id");
    assertEquals(0L, libraries.size());
  }

  @Test
  public void testListOffsetBadSortDir() throws IOException {
    List<Library> libraries = dao.listByOffsetAndNumResults(5, 3, "BARK", "id");
    assertEquals(3, libraries.size());
  }

  @Test(expected = IOException.class)
  public void testListIlluminaOffsetBadLimit() throws IOException {
    dao.listByOffsetAndNumResults(5, -3, "asc", "id");
  }

  @Test
  public void testListOffsetThreeWithThreeLibsPerPageOrderLastMod() throws IOException {
    List<Library> libraries = dao.listByOffsetAndNumResults(3, 3, "desc", "lastModified");
    assertEquals(3, libraries.size());
    assertEquals(11, libraries.get(0).getId());
  }

  @Test
  public void testRemove() throws Exception {
    dao.setCascadeType(CascadeType.ALL);
    CacheManager cacheManager = null;
    dao.setCacheManager(cacheManager);

    Library library = new LibraryImpl();
    String libraryName = "LIB111";
    library.setName(libraryName);
    library.setAlias("libraryAlias");
    library.setDescription("test");
    library.setSample(new SampleImpl());
    library.setPaired(true);
    library.setLibraryType(dao.getLibraryTypeById(1L));
    library.setLibrarySelectionType(dao.getLibrarySelectionTypeById(1L));
    library.setLibraryStrategyType(dao.getLibraryStrategyTypeById(1L));
    User mockUser = Mockito.mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);
    library.setLastModifier(mockUser);

    mockAutoIncrement(nextAutoIncrementId);
    when(libraryNamingSchema.generateNameFor("name", library)).thenReturn(libraryName);
    when(libraryNamingSchema.validateField("name", library.getName())).thenReturn(true);
    when(libraryNamingSchema.validateField("alias", library.getAlias())).thenReturn(true);

    long libraryId = dao.save(library);
    Library insertedLibrary = dao.get(libraryId);

    assertNotNull(insertedLibrary);
    assertTrue(dao.remove(insertedLibrary));
    Mockito.verify(changeLogDAO, Mockito.times(1)).deleteAllById("Library", insertedLibrary.getId());
    nextAutoIncrementId++;
  }

  @Test
  public void testGetLibraryTypeById() throws Exception {
    LibraryType libraryTypeById = dao.getLibraryTypeById(5);

    assertEquals(Long.valueOf(5), libraryTypeById.getId());
    assertEquals("LS454", libraryTypeById.getPlatformType());
    assertEquals("Rapid Shotgun", libraryTypeById.getDescription());

  }

  @Test
  public void testGetLibraryTypeByDescription() throws Exception {
    LibraryType libraryTypeByDescription = dao.getLibraryTypeByDescription("Mate Pair");
    assertEquals("Illumina", libraryTypeByDescription.getPlatformType());
    assertEquals(Long.valueOf(2), libraryTypeByDescription.getId());
    assertEquals("Illumina", libraryTypeByDescription.getPlatformType());

  }

  @Test
  public void testGetLibraryTypeByDescriptionAndPlatform() throws Exception {
    LibraryType libraryTypeByDescriptionAndPlatform = dao.getLibraryTypeByDescriptionAndPlatform("8kbp Paired End", PlatformType.LS454);
    assertEquals(Long.valueOf(8), libraryTypeByDescriptionAndPlatform.getId());
    assertEquals("LS454", libraryTypeByDescriptionAndPlatform.getPlatformType());
    assertEquals("8kbp Paired End", libraryTypeByDescriptionAndPlatform.getDescription());
  }

  @Test
  public void testGetLibrarySelectionTypeById() throws Exception {
    LibrarySelectionType librarySelectionTypeById = dao.getLibrarySelectionTypeById(1l);
    assertEquals("RT-PCR", librarySelectionTypeById.getName());
    assertEquals("Source material was selected by reverse transcription PCR", librarySelectionTypeById.getDescription());
  }

  @Test
  public void testGetLibrarySelectionTypeByName() throws Exception {
    LibrarySelectionType chIP = dao.getLibrarySelectionTypeByName("ChIP");
    assertEquals(Long.valueOf(10), chIP.getId());
    assertEquals("Chromatin Immunoprecipitation", chIP.getDescription());
    assertEquals("ChIP", chIP.getName());
  }

  @Test
  public void testGetLibraryStrategyTypeById() throws Exception {
    LibraryStrategyType libraryStrategyTypeById = dao.getLibraryStrategyTypeById(14);
    assertEquals(Long.valueOf(14), libraryStrategyTypeById.getId());
    assertEquals("Concatenated Tag Sequencing", libraryStrategyTypeById.getDescription());
    assertEquals("CTS", libraryStrategyTypeById.getName());
  }

  @Test
  public void testGetLibraryStrategyTypeByName() throws Exception {
    LibraryStrategyType est = dao.getLibraryStrategyTypeByName("EST");
    assertEquals(Long.valueOf(12), est.getId());
    assertEquals("Single pass sequencing of cDNA templates", est.getDescription());
    assertEquals("EST", est.getName());
  }

  @Test
  public void testListLibraryTypesByPlatform() throws Exception {
    List<LibraryType> libraryTypes = dao.listLibraryTypesByPlatform("Solid");
    assertEquals(5, libraryTypes.size());
    List<String> platforms = Arrays.asList("Small RNA", "Whole Transcriptome", "SAGE", "Long Mate Pair", "Fragment");
    for (LibraryType libraryType : libraryTypes) {
      assertTrue("not all platforms found", platforms.contains(libraryType.getDescription()));
    }
  }

  @Test
  public void testListAllLibraryTypes() throws Exception {
    List<LibraryType> libraryTypes = dao.listAllLibraryTypes();
    assertEquals(18, libraryTypes.size());

  }

  @Test
  public void testListAllLibrarySelectionTypes() throws Exception {
    List<LibrarySelectionType> librarySelectionTypes = dao.listAllLibrarySelectionTypes();
    assertEquals(25, librarySelectionTypes.size());
  }

  @Test
  public void testListAllLibraryStrategyTypes() throws Exception {
    List<LibraryStrategyType> libraryStrategyTypes = dao.listAllLibraryStrategyTypes();
    assertEquals(20, libraryStrategyTypes.size());
  }

}
