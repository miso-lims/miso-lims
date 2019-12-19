package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.BoxStore;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;

@Transactional
public class HibernatePoolDaoIT extends AbstractDAOTest {

  private static void compareFields(Pool expected, Pool actual) {
    assertEquals(expected.getConcentration(), actual.getConcentration());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getIdentificationBarcode(), actual.getIdentificationBarcode());
    assertEquals(0, (expected.getCreationTime().getTime() - actual.getCreationTime().getTime()) / 84600000);
    assertEquals(expected.getPlatformType(), actual.getPlatformType());
    assertEquals(expected.getAlias(), actual.getAlias());
    assertEquals(expected.getLastModifier().getId(), actual.getLastModifier().getId());
    assertEquals(expected.isDiscarded(), actual.isDiscarded());
    if (!expected.isDiscarded()) {
      assertEquals(expected.getVolume(), actual.getVolume());
    } else {
      assertTrue(actual.getVolume().compareTo(BigDecimal.ZERO) == 0);
    }
    assertEquals(expected.getQcPassed(), actual.getQcPassed());
    assertEquals(expected.getDescription(), actual.getDescription());
    assertEquals(expected.getNotes().size(), actual.getNotes().size());
  }

  private PoolImpl getATestPool(int counter, Date creationDate, boolean discarded, int notes) {
    final PoolImpl rtn = new PoolImpl();
    final User mockUser = new UserImpl();

    mockUser.setId(1L);
    mockUser.setLoginName("franklin");

    rtn.setConcentration(new BigDecimal(counter));
    rtn.setName("Test Pool " + counter);
    rtn.setIdentificationBarcode("BOOP" + counter);
    rtn.setCreationTime(creationDate);
    rtn.setLastModified(creationDate);
    rtn.setPlatformType(PlatformType.ILLUMINA);
    rtn.setAlias("Alias " + counter);
    rtn.setLastModifier(mockUser);
    rtn.setCreator(mockUser);
    rtn.setDiscarded(discarded);
    rtn.setVolume(discarded ? BigDecimal.ZERO : new BigDecimal(counter));
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

  @InjectMocks
  private HibernatePoolDao dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    dao.setBoxStore(boxStore);
  }

  @Test
  public void testChangeLogFunctionality() throws Exception {
    PoolImpl testPool = getATestPool(1, new Date(), false, 0);

    dao.save(testPool);

    testPool.setConcentration(new BigDecimal("5"));
    testPool.setName("Test Pool xxx");
    testPool.setIdentificationBarcode("Foob");
    testPool.setCreationTime(new Date());
    testPool.setPlatformType(PlatformType.IONTORRENT);
    testPool.setAlias("Alias changed");
    testPool.setDiscarded(true);
    testPool.setVolume(new BigDecimal("10"));
    testPool.setQcPassed(true);
    testPool.setDescription("Description changed");

    User user = new UserImpl();
    user.setId(1L);
    testPool.setLastModifier(user);
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
    assertTrue(dao.listAllByCriteria(PlatformType.ILLUMINA, null, null).size() > 0);
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
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "alias 8", null).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "alias 81", null).size());
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
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "description 6", null).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "description 61", null).size());
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
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "boop7", null).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "boop71", null).size());
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
    assertEquals(3, dao.listAllByCriteria(PlatformType.ILLUMINA, "test pool 9", null).size());
    assertEquals(1, dao.listAllByCriteria(PlatformType.ILLUMINA, "test pool 91", null).size());
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
  public void testListByLibraryAliquotId() throws Exception {
    assertEquals(2, dao.listByLibraryAliquotId(1L).size());
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
    assertTrue(dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA)) > 0);
  }

  @Test
  public void testCountIlluminaPoolsBadSearch() throws IOException {
    assertEquals(0L, dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA), PaginationFilter.query("DROP TABLE Pool;")));
  }

  @Test
  public void testCountIlluminaPoolsBySearch() throws IOException {
    assertEquals(2L, dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA), PaginationFilter.query("IPO1")));
  }

  @Test
  public void testCountIlluminaPoolsEmptySearch() throws IOException {
    assertTrue(dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA), PaginationFilter.query("")) > 0);
  }

  @Test
  public void testCountPacBioPools() throws IOException {
    assertEquals(0L, dao.count(PaginationFilter.platformType(PlatformType.PACBIO)));
  }

  @Test
  public void testCountPacBioPoolsBySearch() throws IOException {
    assertEquals(0L, dao.count(PaginationFilter.platformType(PlatformType.PACBIO), PaginationFilter.query("IPO1")));
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
  public void testListAllByPlatformAndSearchNoneForPlatformType() throws IOException {
    assertEquals(0, dao.listAllByCriteria(PlatformType.SOLID, "", null).size());
  }

  @Test
  public void testListAllByPlatformAndSearchNoneForQuery() throws IOException {
    assertEquals(0, dao.listAllByCriteria(PlatformType.ILLUMINA, "asdf", null).size());
  }

  @Test
  public void testListAllByPlatformAndSearchNullQuery() throws IOException {
    assertTrue(dao.listAllByCriteria(PlatformType.ILLUMINA, null, null).size() > 0);
  }

  @Test
  public void testListAllByPlatformAndSearchWithEmptyString() throws IOException {
    assertTrue(dao.listAllByCriteria(PlatformType.ILLUMINA, "", null).size() > 0);
  }

  @Test
  public void testListAllByPlatformNone() throws IOException {
    assertEquals(0, dao.listAllByCriteria(PlatformType.SOLID, null, null).size());
  }

  @Test
  public void testListByIlluminaBadSearchWithLimit() throws IOException {
    List<Pool> pools = dao.list(5, 3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA),
        PaginationFilter.query("; DROP TABLE Pool;"));
    assertEquals(0L, pools.size());
  }

  @Test
  public void testListByIlluminaEmptySearchWithLimit() throws IOException {
    List<Pool> pools = dao.list(5, 3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA), PaginationFilter.query(""));
    assertEquals(3L, pools.size());
  }

  @Test
  public void testListByIlluminaSearchWithLimit() throws IOException {
    List<Pool> pools = dao.list(5, 3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA), PaginationFilter.query("IPO"));
    assertEquals(3, pools.size());
    assertEquals(6L, pools.get(0).getId());
  }

  @Test
  public void testListBySearchWithBadQuery() throws IOException {
    assertEquals(0, dao.listAllByCriteria(null, ";DROP TABLE Users;", null).size());
  }

  @Test
  public void testListBySearchWithGoodAliasQuery() throws IOException {
    dao.listAll();
    assertTrue(dao.listAllByCriteria(null, "IPO1", null).size() > 0);
  }

  @Test
  public void testListBySearchWithGoodNameQuery() throws IOException {
    assertTrue(dao.listAllByCriteria(null, "Pool 1", null).size() > 0);
  }

  @Test
  public void testListIlluminaOffsetBadLimit() throws IOException {
    exception.expect(IOException.class);
    dao.list(5, -3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA));
  }

  @Test
  public void testListIlluminaOffsetThreeWithThreeSamplesPerPageOrderLastMod() throws IOException {
    List<Pool> pools = dao.list(3, 3, false, "lastModified", PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertEquals(3, pools.size());
    assertEquals(7, pools.get(0).getId());
  }

  @Test
  public void testListIlluminaPoolsWithLimitAndOffset() throws IOException {
    assertEquals(3, dao.list(5, 3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA)).size());
  }

  @Test
  public void testListPoolsWithLimit() throws IOException {
    assertEquals(10, dao.listAllByCriteria(null, null, 10).size());
  }

  @Test
  public void testListPoolsWithLimitZero() throws IOException {
    assertEquals(0, dao.listAllByCriteria(null, null, 0).size());
  }

  @Test
  public void testLastModified() throws IOException {
    Pool pool = dao.get(1L);
    assertNotNull(pool);
    assertEquals(1L, pool.getId());
    assertNotNull(pool.getLastModified());
  }

  @Test
  public void testRemoveAliquot() throws IOException {
    Pool pool = dao.get(3L);
    int originalSize = pool.getPoolContents().size();
    pool.getPoolContents().removeIf(pd -> pd.getPoolableElementView().getAliquotId() == 10L);
    assertEquals("LDI8 should be removed from collection", originalSize - 1, pool.getPoolContents().size());
    dao.save(pool);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Pool saved = dao.get(3L);
    int savedSize = saved.getPoolContents().size();
    assertEquals("LDI8 should not be present in saved collection", originalSize - 1, savedSize);
  }

  @Test
  public void testAddAliquot() throws IOException {
    Pool pool = dao.get(1L);
    int originalSize = pool.getPoolContents().size();
    PoolableElementView ldi = (PoolableElementView) sessionFactory.getCurrentSession().get(PoolableElementView.class, 14L);
    PoolElement element = new PoolElement(pool, ldi);
    pool.getPoolContents().add(element);
    dao.save(pool);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Pool saved = dao.get(1L);
    int savedSize = saved.getPoolContents().size();
    assertEquals("LDI14 should present in saved collection", originalSize + 1, savedSize);
  }

  @Test
  public void testEditProportions() throws IOException {
    Pool pool = dao.get(1L);
    assertFalse("Test pool should contain aliquots", pool.getPoolContents().isEmpty());
    pool.getPoolContents().forEach(pd -> {
      assertEquals(String.format("Original proportion of %s should be 1", pd.getPoolableElementView().getAliquotName()), 1,
          pd.getProportion());
      pd.setProportion(3);
    });
    dao.save(pool);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Pool saved = dao.get(1L);
    saved.getPoolContents().forEach(pd -> {
      assertEquals(String.format("Saved proportion of %s should be 3", pd.getPoolableElementView().getAliquotName()), 3,
          pd.getProportion());
    });
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("IPO1"));
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
  public void testSearchByBox() throws IOException {
    testSearch(PaginationFilter.box("BOX1"));
  }

  @Test
  public void testSearchByFreezer() throws Exception {
    testSearch(PaginationFilter.freezer("freezer1"));
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
