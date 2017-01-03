package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

public class SQLPoolDAOTest extends AbstractDAOTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private ExperimentStore experimentDAO;
  @Mock
  private PoolQcStore poolQcDAO;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  @Mock
  private ChangeLogStore changeLogDAO;
  @Mock
  private SecurityStore securityDAO;
  @Mock
  private NoteStore noteDAO;
  @Mock
  private BoxStore boxDAO;
  @Mock
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;
  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private SQLPoolDAO dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 11L;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
    mockLibraryDilutionStore();
  }

  private void mockLibraryDilutionStore() throws IOException {
    @SuppressWarnings("unchecked")
    Store<LibraryDilution> ldiStore = Mockito.mock(Store.class);
    Mockito.when(ldiStore.get(Mockito.anyLong())).thenReturn(new LibraryDilution());
  }

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }

  @Test
  public void testListAll() throws IOException {
    assertEquals(10, dao.listAll().size());
  }

  @Test
  public void testCount() throws IOException {
    assertEquals(10, dao.count());
  }

  @Test
  public void testGet() throws IOException {
    Pool pool = dao.get(1L);
    assertNotNull(pool);
    assertEquals(1L, pool.getId());
  }

  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(100L));
  }

  @Test
  public void testLazyGet() throws IOException {
    Pool pool = dao.lazyGet(1L);
    assertNotNull(pool);
    assertEquals(1L, pool.getId());
  }

  @Test
  public void testLazyGetNone() throws IOException {
    assertNull(dao.lazyGet(100L));
  }

  @Test
  public void testListByProjectId() throws IOException {
    assertEquals(10, dao.listByProjectId(1L).size());
  }

  @Test
  public void testListByProjectNone() throws IOException {
    assertEquals(0, dao.listByProjectId(100L).size());
  }

  @Test
  public void testListBySampleId() throws IOException {
    assertEquals(2, dao.listBySampleId(2L).size());
  }

  @Test
  public void testListBySampleIdNone() throws IOException {
    assertEquals(0, dao.listBySampleId(100L).size());
  }

  @Test
  public void testListByLibraryId() throws IOException {
    assertEquals(2, dao.listByLibraryId(1L).size());
  }

  @Test
  public void testListByLibraryIdNone() throws IOException {
    assertEquals(0, dao.listByLibraryId(100L).size());
  }

  @Test
  public void testListAllByPlatform() throws IOException {
    assertEquals(10, dao.listAllByPlatform(PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListAllByPlatformNone() throws IOException {
    assertEquals(0, dao.listAllByPlatform(PlatformType.SOLID).size());
  }

  @Test
  public void testListAllByPlatformNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.listAllByPlatform(null);
  }

  @Test
  public void testListAllByPlatformAndSearchByName() throws IOException {
    assertEquals(1, dao.listAllByPlatformAndSearch(PlatformType.ILLUMINA, "IPO5").size());
  }

  @Test
  public void testListAllByPlatformAndSearchByAlias() throws IOException {
    assertEquals(1, dao.listAllByPlatformAndSearch(PlatformType.ILLUMINA, "Pool 5").size());
  }

  @Test
  public void testListAllByPlatformAndSearchByIdentificationBarcode() throws IOException {
    assertEquals(1, dao.listAllByPlatformAndSearch(PlatformType.ILLUMINA, "IPO5::Illumina").size());
  }

  @Test
  public void testListAllByPlatformAndSearchWithEmptyString() throws IOException {
    assertEquals(10, dao.listAllByPlatformAndSearch(PlatformType.ILLUMINA, "").size());
  }

  @Test
  public void testListAllByPlatformAndSearchNoneForPlatformType() throws IOException {
    assertEquals(0, dao.listAllByPlatformAndSearch(PlatformType.SOLID, "").size());
  }

  @Test
  public void testListAllByPlatformAndSearchNoneForQuery() throws IOException {
    assertEquals(0, dao.listAllByPlatformAndSearch(PlatformType.ILLUMINA, "asdf").size());
  }

  @Test
  public void testListAllByPlatformAndSearchNullPlatformType() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.listAllByPlatformAndSearch(null, "");
  }

  @Test
  public void testListAllByPlatformAndSearchNullQuery() throws IOException {
    assertEquals(10, dao.listAllByPlatformAndSearch(PlatformType.ILLUMINA, null).size());
  }

  @Test
  public void testListReadyByPlatform() throws IOException {
    assertEquals(5, dao.listReadyByPlatform(PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListReadyByPlatformNone() throws IOException {
    assertEquals(0, dao.listReadyByPlatform(PlatformType.SOLID).size());
  }

  @Test
  public void testListReadyByPlatformNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.listReadyByPlatform(null).size();
  }

  @Test
  public void testListReadyByPlatformAndSearchByName() throws IOException {
    assertEquals(1, dao.listReadyByPlatformAndSearch(PlatformType.ILLUMINA, "IPO3").size());
  }

  @Test
  public void testListReadyByPlatformAndSearchByAlias() throws IOException {
    assertEquals(1, dao.listReadyByPlatformAndSearch(PlatformType.ILLUMINA, "Pool 3").size());
  }

  @Test
  public void testListReadyByPlatformAndSearchByIdentificationBarcode() throws IOException {
    assertEquals(1, dao.listReadyByPlatformAndSearch(PlatformType.ILLUMINA, "IPO3::Illumina").size());
  }

  @Test
  public void testListReadyByPlatformAndSearchWithEmptyString() throws IOException {
    assertEquals(5, dao.listReadyByPlatformAndSearch(PlatformType.ILLUMINA, "").size());
  }

  @Test
  public void testListReadyByPlatformAndSearchNoneForPlatformType() throws IOException {
    assertEquals(0, dao.listReadyByPlatformAndSearch(PlatformType.SOLID, "").size());
  }

  @Test
  public void testListReadyByPlatformAndSearchNoneForQuery() throws IOException {
    assertEquals(0, dao.listReadyByPlatformAndSearch(PlatformType.ILLUMINA, "asdf").size());
  }

  @Test
  public void testListReadyByPlatformAndSearchWithNullPlatformType() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.listReadyByPlatformAndSearch(null, "IPO3").size();
  }

  @Test
  public void testListReadyByPlatformAndSearchWithNullQuery() throws IOException {
    assertEquals(5, dao.listReadyByPlatformAndSearch(PlatformType.ILLUMINA, null).size());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testListBySearchWithNullQuery() throws IOException {
    assertEquals(0, dao.listBySearch(null).size());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testListBySearchWithBadQuery() throws IOException {
    assertEquals(0, dao.listBySearch(";DROP TABLE Users;").size());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testListBySearchWithGoodNameQuery() throws IOException {
    assertEquals(2, dao.listBySearch("Pool 1").size());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testListBySearchWithGoodAliasQuery() throws IOException {
    dao.listAll();
    assertEquals(2, dao.listBySearch("IPO1").size());
  }

  @Test
  public void testGetPoolColumnSizes() throws IOException {
    Map<String, Integer> map = dao.getPoolColumnSizes();
    assertNotNull(map);
    assertFalse(map.isEmpty());
  }

  @Test
  public void testListPoolsWithLimitZero() throws IOException {
    assertEquals(0, dao.listAllPoolsWithLimit(0).size());
  }

  @Test
  public void testListPoolsWithLimit() throws IOException {
    assertEquals(10, dao.listAllPoolsWithLimit(10).size());
  }

  @Test
  public void testListIlluminaPoolsWithLimitAndOffset() throws IOException {
    assertEquals(3, dao.listByOffsetAndNumResults(5, 3, "ASC", "id", PlatformType.ILLUMINA).size());
  }

  @Test
  public void testCountIlluminaPools() throws IOException {
    assertEquals(10L, dao.countPoolsByPlatform(PlatformType.ILLUMINA));
  }

  @Test
  public void testCountPacBioPools() throws IOException {
    assertEquals(0L, dao.countPoolsByPlatform(PlatformType.PACBIO));
  }

  @Test
  public void testCountIlluminaPoolsBySearch() throws IOException {
    assertEquals(2L, dao.countPoolsBySearch(PlatformType.ILLUMINA, "IPO1"));
  }

  @Test
  public void testCountPacBioPoolsBySearch() throws IOException {
    assertEquals(0L, dao.countPoolsBySearch(PlatformType.PACBIO, "IPO1"));
  }

  @Test
  public void testCountIlluminaPoolsEmptySearch() throws IOException {
    assertEquals(10L, dao.countPoolsBySearch(PlatformType.ILLUMINA, ""));
  }

  @Test
  public void testCountIlluminaPoolsBadSearch() throws IOException {
    assertEquals(0L, dao.countPoolsBySearch(PlatformType.ILLUMINA, "; DROP TABLE Pool;"));
  }

  @Test
  public void testListByIlluminaSearchWithLimit() throws IOException {
    List<Pool> pools = dao
        .listBySearchOffsetAndNumResultsAndPlatform(5, 3, "IPO", "asc", "id", PlatformType.ILLUMINA);
    assertEquals(3, pools.size());
    assertEquals(6L, pools.get(0).getId());
  }

  @Test
  public void testListByIlluminaEmptySearchWithLimit() throws IOException {
    List<Pool> pools = dao
        .listBySearchOffsetAndNumResultsAndPlatform(5, 3, "", "asc", "id", PlatformType.ILLUMINA);
    assertEquals(3L, pools.size());
  }

  @Test
  public void testListByIlluminaBadSearchWithLimit() throws IOException {
    List<Pool> pools = dao
        .listBySearchOffsetAndNumResultsAndPlatform(5, 3, "; DROP TABLE Pool;", "asc", "id", PlatformType.ILLUMINA);
    assertEquals(0L, pools.size());
  }

  @Test
  public void testListByIlluminaOffsetBadSortDir() throws IOException {
    List<Pool> pools = dao.listByOffsetAndNumResults(5, 3, "BARK", "id", PlatformType.ILLUMINA);
    assertEquals(3, pools.size());
  }

  @Test
  public void testListIlluminaOffsetBadLimit() throws IOException {
    expectedException.expect(IOException.class);
    dao.listByOffsetAndNumResults(5, -3, "asc", "id", PlatformType.ILLUMINA);
  }

  @Test
  public void testListIlluminaOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<Pool> pools = dao.listByOffsetAndNumResults(3, 3, "desc", "lastModified", PlatformType.ILLUMINA);
    assertEquals(3, pools.size());
    assertEquals(7, pools.get(0).getId());
  }

  @Test
  public void testAutoGenerateIdBarcode() {
    Pool p = new PoolImpl();
    p.setName("name");
    p.setPlatformType(PlatformType.ILLUMINA);
    dao.autoGenerateIdBarcode(p);
    assertEquals("name::" + PlatformType.ILLUMINA.getKey(), p.getIdentificationBarcode());
  }

  @Test
  public void testGetPoolByExperiment() throws IOException {
    Experiment exp = new ExperimentImpl();
    exp.setId(1L);
    Platform plat = new PlatformImpl();
    plat.setPlatformType(PlatformType.ILLUMINA);
    exp.setPlatform(plat);
    Pool p = dao.getPoolByExperiment(exp);
    assertNotNull(p);
  }

  @Test
  public void testGetPoolByExperimentNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.getPoolByExperiment(null);
  }

  @Test
  public void testGetPoolByExperimentNullPlatform() throws IOException {
    Experiment exp = new ExperimentImpl();
    assertNull(dao.getPoolByExperiment(exp));
  }

  @Test
  public void testGetPoolByExperimentNoneForPlatform() {
    Experiment exp = new ExperimentImpl();
    exp.setId(1L);
    Platform plat = new PlatformImpl();
    plat.setPlatformType(PlatformType.SOLID);
    exp.setPlatform(plat);
    assertNull(dao.getPoolByExperiment(exp));
  }

  @Test
  public void testGetPoolByExperimentNoneForExperiment() {
    Experiment exp = new ExperimentImpl();
    exp.setId(100L);
    Platform plat = new PlatformImpl();
    plat.setPlatformType(PlatformType.ILLUMINA);
    exp.setPlatform(plat);
    assertNull(dao.getPoolByExperiment(exp));
  }

  @Test
  public void testGetPoolByBarcode() throws IOException {
    assertNotNull(dao.getPoolByBarcode("IPO3::Illumina", PlatformType.ILLUMINA));
  }

  @Test
  public void testGetPoolByBarcodeNone() throws IOException {
    assertNull(dao.getPoolByBarcode("asdf", PlatformType.ILLUMINA));
  }

  @Test
  public void testGetPoolByBarcodeNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.getPoolByBarcode(null, PlatformType.ILLUMINA);
  }

  @Test
  public void testGetPoolByBarcodeNullPlatform() throws IOException {
    assertNull(dao.getPoolByBarcode("", null));
  }

  @Test
  public void testGetByBarcode() throws IOException {
    assertNotNull(dao.getByBarcode("IPO3::Illumina"));
  }

  @Test
  public void testGetByBarcodeNone() throws IOException {
    assertNull(dao.getByBarcode("asdf"));
  }

  @Test
  public void testGetByBarcodeNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.getByBarcode(null);
  }

  @Test
  public void testGetByBarcodeList() throws IOException {
    List<String> barcodes = new ArrayList<>();
    barcodes.add("IPO2::Illumina");
    barcodes.add("IPO3::Illumina");
    assertEquals(2, dao.getByBarcodeList(barcodes).size());
  }

  @Test
  public void testGetByBarcodeListNone() throws IOException {
    List<String> barcodes = new ArrayList<>();
    barcodes.add("asdf");
    barcodes.add("jkl;");
    assertEquals(0, dao.getByBarcodeList(barcodes).size());
  }

  @Test
  public void testGetByBarcodeListNull() throws IOException {
    expectedException.expect(NullPointerException.class);
    dao.getByBarcodeList(null);
  }

  @Test
  public void testGetByBarcodeListEmpty() throws IOException {
    assertEquals(0, dao.getByBarcodeList(new ArrayList<String>()).size());
  }

  @Test
  public void testGetByPositionId() throws IOException {
    assertNotNull(dao.getByPositionId(201L));
  }

  @Test
  public void testGetByPositionIdNone() throws IOException {
    assertNull(dao.getByPositionId(9999L));
  }

  @Test
  public void testRemove() throws Exception {
    Pool pool = new PoolImpl();
    String poolName = "IPO111";
    pool.setName(poolName);
    pool.setAlias("poolAlias");
    pool.setConcentration((double) 3);
    pool.setPlatformType(PlatformType.ILLUMINA);
    User mockUser = Mockito.mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);
    pool.setLastModifier(mockUser);

    mockAutoIncrement(nextAutoIncrementId);
    when(namingScheme.generateNameFor(pool)).thenReturn(poolName);
    when(namingScheme.validateName(pool.getName())).thenReturn(ValidationResult.success());

    long poolId = dao.save(pool);
    Pool insertedPool = dao.get(poolId);
    assertNotNull(insertedPool);
    insertedPool.setExperiments(new ArrayList<Experiment>());
    insertedPool.setPoolableElements(null);
    assertTrue(dao.remove(insertedPool));
    Mockito.verify(changeLogDAO, Mockito.times(1)).deleteAllById("Pool", insertedPool.getId());
    assertNull(dao.get(insertedPool.getId()));
    nextAutoIncrementId++;
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    long autoIncrementId = nextAutoIncrementId;
    assertNull(dao.get(autoIncrementId));
    Pool pool = new PoolImpl();
    pool.setAlias("Test Pool");
    pool.setPlatformType(PlatformType.ILLUMINA);
    User user = new UserImpl();
    user.setUserId(1L);
    pool.setLastModifier(user);
    Mockito.when(namingScheme.validateName(Mockito.anyString())).thenReturn(ValidationResult.success());
    mockAutoIncrement(autoIncrementId);
    Long poolId = dao.save(pool);
    assertEquals(Long.valueOf(autoIncrementId), Long.valueOf(poolId));
    assertNotNull(dao.get(autoIncrementId));
    nextAutoIncrementId++;
  }

  @Test
  public void testSaveEdit() throws IOException, MisoNamingException {
    Pool oldPool = dao.get(1L);
    assertNotNull(oldPool);
    oldPool.setAlias("New Alias");
    oldPool.setVolume(20.5D);
    SecurityProfile sp = new SecurityProfile();
    sp.setProfileId(2L);
    oldPool.setSecurityProfile(sp);
    User user = new UserImpl();
    user.setUserId(1L);
    oldPool.setLastModifier(user);
    oldPool.setPoolableElements(null);
    Mockito.when(namingScheme.validateName(Mockito.anyString())).thenReturn(ValidationResult.success());

    assertEquals(1L, dao.save(oldPool));
    Pool newPool = dao.get(1L);
    assertNotNull(newPool);
    assertEquals(oldPool.getAlias(), newPool.getAlias());
    assertEquals(oldPool.getVolume(), newPool.getVolume());
  }

}
