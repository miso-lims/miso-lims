package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;

@Transactional
public class HibernatePoolDaoTest extends AbstractDAOTest {

  private static void compareFields(Pool expected, Pool actual) {
    assertEquals(expected.getConcentration(), actual.getConcentration());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getIdentificationBarcode(), actual.getIdentificationBarcode());
    assertEquals(0, (expected.getCreationDate().getTime() - actual.getCreationDate().getTime()) / 84600000);
    assertEquals(expected.getSecurityProfile().getProfileId(), actual.getSecurityProfile().getProfileId());
    assertEquals(expected.getExperiments().size(), actual.getExperiments().size());
    assertEquals(expected.getPlatformType(), actual.getPlatformType());
    assertEquals(expected.getReadyToRun(), actual.getReadyToRun());
    assertEquals(expected.getAlias(), actual.getAlias());
    assertEquals(expected.getLastModifier().getUserId(), actual.getLastModifier().getUserId());
    assertEquals(expected.isDiscarded(), actual.isDiscarded());
    if (!expected.isDiscarded()) {
      assertEquals(expected.getVolume(), actual.getVolume());
    } else {
      assertTrue(0.0 == actual.getVolume());
    }
    assertEquals(expected.getQcPassed(), actual.getQcPassed());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertEquals(expected.getNotes().size(), actual.getNotes().size());
  }

  final SecurityProfile mockSecurityProfile = new SecurityProfile();

  private PoolImpl getATestPool(int counter, Date creationDate, boolean discarded, int notes) {
    final PoolImpl rtn = new PoolImpl();
    final User mockUser = new UserImpl();

    mockSecurityProfile.setProfileId(1L);
    mockUser.setUserId(1L);
    mockUser.setLoginName("franklin");

    rtn.setConcentration((double) counter);
    rtn.setName("Test Pool " + counter);
    rtn.setIdentificationBarcode("BOOP" + counter);
    rtn.setCreationDate(creationDate);
    rtn.setSecurityProfile(mockSecurityProfile);
    rtn.setPlatformType(PlatformType.ILLUMINA);
    rtn.setReadyToRun(true);
    rtn.setAlias("Alias " + counter);
    rtn.setLastModifier(mockUser);
    rtn.setDiscarded(discarded);
    rtn.setVolume(discarded ? 0.0 : counter);
    rtn.setQcPassed(false);
    rtn.setDescription("Description " + counter);
    for (int i = 0; i < notes; i++) {
      Note note = new Note();
      note.setCreationDate(new Date());
      note.setOwner(mockUser);
      note.setText(note.getCreationDate().toString() + " stuff");
      rtn.addNote(note);
    }

    return rtn;
  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  BoxStore boxStore;
  @Mock
  private SecurityStore securityStore;

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private JdbcTemplate template;

  @InjectMocks
  private HibernatePoolDao dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    dao.setJdbcTemplate(template);
    dao.setBoxStore(boxStore);
  }

  @Test
  public void testChangeLogFunctionality() throws Exception {
    User newModifier = Mockito.mock(User.class);
    PoolImpl testPool = getATestPool(1, new Date(), false, 0);
    Mockito.when(newModifier.getLoginName()).thenReturn("Nick Cage");

    dao.save(testPool);

    testPool.setConcentration(5D);
    testPool.setName("Test Pool xxx");
    testPool.setIdentificationBarcode("Foob");
    testPool.setCreationDate(new Date());
    testPool.setSecurityProfile(Mockito.mock(SecurityProfile.class));
    testPool.setExperiments(new ArrayList<Experiment>());
    testPool.setPlatformType(PlatformType.IONTORRENT);
    testPool.setReadyToRun(false);
    testPool.setAlias("Alias changed");
    testPool.setLastModifier(newModifier);
    testPool.setDiscarded(true);
    testPool.setVolume(10D);
    testPool.setQcPassed(true);
    testPool.setDescription("Description changed");
    dao.save(testPool);
  }

  @Test
  public void testGetByBarcode() throws Exception {
    final PoolImpl testPool = getATestPool(17, new Date(), false, 3);
    final String idBarcode = testPool.getIdentificationBarcode();
    // non existing pool check
    assertNull(dao.getByBarcode(idBarcode));
    dao.save(testPool);
    Pool result = dao.getByBarcode(idBarcode);
    assertNotNull(result);
    compareFields(testPool, result);
  }

  @Test
  public void testGetByBarcodeList() throws IOException {
    List<String> barcodes = new ArrayList<>();
    barcodes.add("IPO2::Illumina");
    barcodes.add("IPO3::Illumina");
    assertEquals(2, dao.getByBarcodeList(barcodes).size());
  }

  @Test
  public void testGetByBarcodeListEmpty() throws IOException {
    assertEquals(0, dao.getByBarcodeList(new ArrayList<String>()).size());
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
    exception.expect(NullPointerException.class);
    dao.getByBarcodeList(null);
  }

  @Test
  public void testGetByBarcodeNull() throws Exception {
    exception.expect(NullPointerException.class);
    dao.getByBarcode(null);
  }

  @Test
  public void testListAll() throws IOException {
    assertTrue(dao.listAll().size() > 0);
  }

  @Test
  public void testListAllByPlatform() throws IOException {
    assertTrue(dao.listAllByCriteria(PlatformType.ILLUMINA, null, null, false).size() > 0);
  }

  @Test
  public void testListAllByPlatformAndSearch_alias() throws Exception {
    // search on name, alias, identificationBarcode and description
    PoolImpl test1 = getATestPool(81, new Date(), false, 3);
    PoolImpl test2 = getATestPool(82, new Date(), true, 2);
    PoolImpl test3 = getATestPool(83, new Date(), false, 1);

    dao.save(test1);
    dao.save(test2);
    dao.save(test3);
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "alias 8", null, false).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "alias 81", null, false).size());
  }

  @Test
  public void testListAllByPlatformAndSearch_description() throws Exception {
    // search on name, alias, identificationBarcode and description
    PoolImpl test1 = getATestPool(61, new Date(), false, 3);
    PoolImpl test2 = getATestPool(62, new Date(), true, 2);
    PoolImpl test3 = getATestPool(63, new Date(), false, 1);

    dao.save(test1);
    dao.save(test2);
    dao.save(test3);
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "description 6", null, false).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "description 61", null, false).size());
  }

  @Test
  public void testListAllByPlatformAndSearch_identificationBarcode() throws Exception {
    // search on name, alias, identificationBarcode and description
    PoolImpl test1 = getATestPool(71, new Date(), false, 3);
    PoolImpl test2 = getATestPool(72, new Date(), true, 2);
    PoolImpl test3 = getATestPool(73, new Date(), false, 1);

    dao.save(test1);
    dao.save(test2);
    dao.save(test3);
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "boop7", null, false).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "boop71", null, false).size());
  }

  @Test
  public void testListAllByPlatformAndSearch_Name() throws Exception {
    // search on name, alias, identificationBarcode and description
    PoolImpl test1 = getATestPool(91, new Date(), false, 3);
    PoolImpl test2 = getATestPool(92, new Date(), true, 2);
    PoolImpl test3 = getATestPool(93, new Date(), false, 1);

    dao.save(test1);
    dao.save(test2);
    dao.save(test3);
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "test pool 9", null, false).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "test pool 91", null, false).size());
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
  public void testListByProjectId() throws IOException {
    assertEquals(10, dao.listByProjectId(1L).size());
  }

  @Test
  public void testListByProjectNone() throws IOException {
    assertEquals(0, dao.listByProjectId(100L).size());
  }

  @Test
  public void testListReadyByPlatform() throws IOException {
    assertTrue(dao.listAllByCriteria(PlatformType.ILLUMINA, null, null, true).size() > 0);
  }

  @Test
  public void testListReadyByPlatformNone() throws IOException {
    assertEquals(0, dao.listAllByCriteria(PlatformType.SOLID, null, null, true).size());
  }

  @Test
  public void testSaveEmpty() throws Exception {

    final Date creationDate = new Date();
    final PoolImpl saveMe = getATestPool(1, creationDate, true, 0);
    final long rtn = dao.save(saveMe);
    Mockito.verifyZeroInteractions(boxStore);

    // check they're actually the same
    Pool freshPool = dao.get(rtn);
    compareFields(saveMe, freshPool);
  }

  @Test
  public void testSaveEmptyWithNotes() throws Exception {

    final Date creationDate = new Date();
    final PoolImpl saveMe = getATestPool(1, creationDate, true, 10);
    final long rtn = dao.save(saveMe);
    Mockito.verifyZeroInteractions(boxStore);

    // check they're actually the same
    Pool freshPool = dao.get(rtn);
    compareFields(saveMe, freshPool);
  }

  @Test
  public void testSaveNonEmpty() throws Exception {

    final Date creationDate = new Date();
    final Pool saveMe = getATestPool(1, creationDate, false, 0);
    final long rtn = dao.save(saveMe);
    Mockito.verifyZeroInteractions(boxStore);

    // check they're actually the same
    Pool freshPool = dao.get(rtn);
    compareFields(saveMe, freshPool);
  }

  @Test
  public void testCount() throws IOException {
    assertTrue(dao.count() > 0);
  }

  @Test
  public void testCountIlluminaPools() throws IOException {
    assertTrue(dao.countPoolsBySearch(PlatformType.ILLUMINA, null) > 0);
  }

  @Test
  public void testCountIlluminaPoolsBadSearch() throws IOException {
    assertEquals(0L, dao.countPoolsBySearch(PlatformType.ILLUMINA, "; DROP TABLE Pool;"));
  }

  @Test
  public void testCountIlluminaPoolsBySearch() throws IOException {
    assertEquals(2L, dao.countPoolsBySearch(PlatformType.ILLUMINA, "IPO1"));
  }

  @Test
  public void testCountIlluminaPoolsEmptySearch() throws IOException {
    assertTrue(dao.countPoolsBySearch(PlatformType.ILLUMINA, "") > 0);
  }

  @Test
  public void testCountPacBioPools() throws IOException {
    assertEquals(0L, dao.countPoolsBySearch(PlatformType.PACBIO, null));
  }

  @Test
  public void testCountPacBioPoolsBySearch() throws IOException {
    assertEquals(0L, dao.countPoolsBySearch(PlatformType.PACBIO, "IPO1"));
  }

  @Test
  public void testGetByBarcodeNone() throws IOException {
    assertNull(dao.getByBarcode("asdf"));
  }

  @Test
  public void testGetNone() throws IOException {
    assertNull(dao.get(100L));
  }

  @Test
  public void testGet() throws IOException {
    Pool pool = dao.get(1L);
    assertNotNull(pool);
    assertEquals(1L, pool.getId());
  }

  @Test
  public void testGetPoolColumnSizes() throws IOException {
    Map<String, Integer> map = dao.getPoolColumnSizes();
    assertNotNull(map);
    assertFalse(map.isEmpty());
  }

  @Test
  public void testListAllByPlatformAndSearchNoneForPlatformType() throws IOException {
    assertEquals(0, dao.listAllByCriteria(PlatformType.SOLID, "", null, false).size());
  }

  @Test
  public void testListAllByPlatformAndSearchNoneForQuery() throws IOException {
    assertEquals(0, dao.listAllByCriteria(PlatformType.ILLUMINA, "asdf", null, false).size());
  }

  @Test
  public void testListAllByPlatformAndSearchNullQuery() throws IOException {
    assertTrue(dao.listAllByCriteria(PlatformType.ILLUMINA, null, null, false).size() > 0);
  }

  @Test
  public void testListAllByPlatformAndSearchWithEmptyString() throws IOException {
    assertTrue(dao.listAllByCriteria(PlatformType.ILLUMINA, "", null, false).size() > 0);
  }

  @Test
  public void testListAllByPlatformNone() throws IOException {
    assertEquals(0, dao.listAllByCriteria(PlatformType.SOLID, null, null, false).size());
  }

  @Test
  public void testListByIlluminaBadSearchWithLimit() throws IOException {
    List<Pool> pools = dao
        .listBySearchOffsetAndNumResultsAndPlatform(5, 3, "; DROP TABLE Pool;", "asc", "id", PlatformType.ILLUMINA);
    assertEquals(0L, pools.size());
  }

  @Test
  public void testListByIlluminaEmptySearchWithLimit() throws IOException {
    List<Pool> pools = dao
        .listBySearchOffsetAndNumResultsAndPlatform(5, 3, "", "asc", "id", PlatformType.ILLUMINA);
    assertEquals(3L, pools.size());
  }

  @Test
  public void testListByIlluminaOffsetBadSortDir() throws IOException {
    List<Pool> pools = dao.listBySearchOffsetAndNumResultsAndPlatform(5, 3, null, "BARK", "id", PlatformType.ILLUMINA);
    assertEquals(3, pools.size());
  }

  @Test
  public void testListByIlluminaSearchWithLimit() throws IOException {
    List<Pool> pools = dao
        .listBySearchOffsetAndNumResultsAndPlatform(5, 3, "IPO", "asc", "id", PlatformType.ILLUMINA);
    assertEquals(3, pools.size());
    assertEquals(6L, pools.get(0).getId());
  }

  @Test
  public void testListBySearchWithBadQuery() throws IOException {
    assertEquals(0, dao.listAllByCriteria(null, ";DROP TABLE Users;", null, false).size());
  }

  @Test
  public void testListBySearchWithGoodAliasQuery() throws IOException {
    dao.listAll();
    assertTrue(dao.listAllByCriteria(null, "IPO1", null, false).size() > 0);
  }

  @Test
  public void testListBySearchWithGoodNameQuery() throws IOException {
    assertTrue(dao.listAllByCriteria(null, "Pool 1", null, false).size() > 0);
  }

  @Test
  public void testListIlluminaOffsetBadLimit() throws IOException {
    exception.expect(IOException.class);
    dao.listBySearchOffsetAndNumResultsAndPlatform(5, -3, null, "asc", "id", PlatformType.ILLUMINA);
  }

  @Test
  public void testListIlluminaOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<Pool> pools = dao.listBySearchOffsetAndNumResultsAndPlatform(3, 3, null, "desc", "lastModified", PlatformType.ILLUMINA);
    assertEquals(3, pools.size());
    assertEquals(7, pools.get(0).getId());
  }

  @Test
  public void testListIlluminaPoolsWithLimitAndOffset() throws IOException {
    assertEquals(3, dao.listBySearchOffsetAndNumResultsAndPlatform(5, 3, null, "ASC", "id", PlatformType.ILLUMINA).size());
  }

  @Test
  public void testListPoolsWithLimit() throws IOException {
    assertEquals(10, dao.listAllByCriteria(null, null, 10, false).size());
  }

  @Test
  public void testListPoolsWithLimitZero() throws IOException {
    assertEquals(0, dao.listAllByCriteria(null, null, 0, false).size());
  }

  @Test
  public void testLastModified() throws IOException {
    Pool pool = dao.get(1L);
    assertNotNull(pool);
    assertEquals(1L, pool.getId());
    assertNotNull(pool.getLastModified());
  }

}
