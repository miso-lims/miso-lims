package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

public class SQLLibraryDAOTest extends AbstractDAOTest {

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private MisoNamingScheme<Run> namingScheme;
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
  private ChangeLogStore changeLogStore;
  @Mock
  private MisoNamingScheme<Library> libraryNamingSchema;

  @InjectMocks
  private SQLLibraryDAO dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
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

    mockAutoIncrement();
    when(libraryNamingSchema.validateField(anyString(), anyString())).thenReturn(true);
    when(libraryNamingSchema.generateNameFor("name", library)).thenReturn(libraryName);

    long libraryId = dao.save(library);
    Library insertedLibrary = dao.get(libraryId);
    assertEquals(15, library.getId());
    assertEquals(libraryName, insertedLibrary.getName());
    assertEquals("theAlias", insertedLibrary.getAlias());
    assertEquals("a description", insertedLibrary.getDescription());
    assertEquals(4, library.getSample().getId());
    assertEquals(Long.valueOf(1), library.getLibraryType().getId());
    assertEquals(Long.valueOf(1), library.getLibrarySelectionType().getId());
    assertEquals(Long.valueOf(1), library.getLibraryStrategyType().getId());
  }

  private void mockAutoIncrement() throws Exception {
    List<Library> samples = dao.listAll();
    long max = 0;
    for (Library sample : samples) {
      if (sample.getId() > max) {
        max = sample.getId();
      }
    }
    max++;
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", max);
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
    assertTrue(libraries.size() == 1);
    assertTrue(9 == libraries.get(0).getId());
  }

  @Test
  public void testListBySampleId() throws Exception {
    List<Library> libraries = dao.listBySampleId(1);

    assertTrue(libraries.size() == 1);
    assertTrue(libraries.get(0).getId() == 1);

  }

  @Test
  public void testListByProjectId() throws Exception {
    List<Library> libraries = dao.listByProjectId(1);
    List libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l);
    assertTrue(libraries.size() == 14);
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }
  }

  @Test
  public void testListAll() throws Exception {
    List<Library> libraries = dao.listAll();
    List libraryIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 8l, 9l, 10l, 11l, 12l, 13l, 14l);
    assertTrue(libraries.size() == 14);
    for (Library library : libraries) {
      assertTrue("bad library found", libraryIds.contains(library.getId()));
    }

  }

  @Test
  public void testListAllWithLimit() throws Exception {
    List<Library> libraries = dao.listAllWithLimit(5);
    assertTrue("not within limit", libraries.size() == 5);

  }

  @Test
  public void testCount() throws Exception {
    int count = dao.count();
    assertTrue("count incorrect", dao.count() == 14);
  }

  @Test
  public void testListBySearch() throws Exception {
    String searchStr = "LIB";
    List<Library> libraries = dao.listBySearch(searchStr);
    assertTrue("did not find all libraries", libraries.size() == 14);
  }

  @Test
  public void testListBySearch_NoResults() throws Exception {
    String searchStr = "IJOHEWF";
    List<Library> libraries = dao.listBySearch(searchStr);
    assertTrue("search returned results", libraries.size() == 0);
  }

  @Test
  public void testRemove() throws Exception {
    dao.setCascadeType(CascadeType.ALL);
    Library library = dao.get(3);

    CacheManager cacheManager = mock(CacheManager.class);
    when(cacheManager.getCache(Matchers.anyString())).thenReturn(null);
    dao.setCacheManager(cacheManager);

    boolean remove = dao.remove(library);
    assertTrue("library did not remove successfully", remove);
    Library library1 = dao.get(3);
    assertTrue("library did not remove successfully", library1 == null);
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
    assertTrue(17 == libraryTypes.size());

  }

  @Test
  public void testListAllLibrarySelectionTypes() throws Exception {
    List<LibrarySelectionType> librarySelectionTypes = dao.listAllLibrarySelectionTypes();
    assertTrue(25 == librarySelectionTypes.size());
  }

  @Test
  public void testListAllLibraryStrategyTypes() throws Exception {
    List<LibraryStrategyType> libraryStrategyTypes = dao.listAllLibraryStrategyTypes();
    assertTrue(20 == libraryStrategyTypes.size());
  }

  @Test
  public void testGetTagBarcodeById() throws Exception {
    TagBarcode tagBarcodeById = dao.getTagBarcodeById(8);
    assertEquals(8L, tagBarcodeById.getId());
    assertEquals("ACTTGA", tagBarcodeById.getSequence());
    assertEquals("Index 8", tagBarcodeById.getName());
    assertEquals("TruSeq Single Index", tagBarcodeById.getStrategyName());

  }

  @Test
  public void testGetTagBarcodeByName() throws Exception {
    TagBarcode tagBarcodeByName = dao.getTagBarcodeByName("Index 2");
    assertEquals(2L, tagBarcodeByName.getId());
    assertEquals("CGATGT", tagBarcodeByName.getSequence());
    assertEquals("Index 2", tagBarcodeByName.getName());
    assertEquals("TruSeq Single Index", tagBarcodeByName.getStrategyName());

  }

  @Test
  public void testGetTagBarcodeByLibraryId() throws Exception {
    TagBarcode tagBarcodeByLibraryId = dao.getTagBarcodeByLibraryId(1);
    assertEquals("Index 12", tagBarcodeByLibraryId.getName());
    assertEquals(12, tagBarcodeByLibraryId.getId());
  }

  @Test
  public void testGetTagBarcodesByLibraryId() throws Exception {
    HashMap<Integer, TagBarcode> tagBarcodesByLibraryId = dao.getTagBarcodesByLibraryId(1);
    assertEquals("Index 12", tagBarcodesByLibraryId.get(1).getName());
  }

  @Test
  public void testListTagBarcodesByPlatform() throws Exception {
    List<TagBarcode> illumina = dao.listTagBarcodesByPlatform("Illumina");
    assertTrue(68 == illumina.size());
  }

  @Test
  public void testListTagBarcodesByStrategyName() throws Exception {
    List<TagBarcode> tagBarcodes = dao.listTagBarcodesByStrategyName("Nextera Dual Index");
    assertTrue(20 == tagBarcodes.size());
  }

  @Test
  public void testListAllTagBarcodes() throws Exception {
    List<TagBarcode> tagBarcodes = dao.listAllTagBarcodes();
    assertTrue(80 == tagBarcodes.size());
  }
}
