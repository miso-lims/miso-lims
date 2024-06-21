package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
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
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

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
      note.setCreationDate(LocalDate.now(ZoneId.systemDefault()));
      note.setOwner(mockUser);
      note.setText(note.getCreationDate().toString() + " stuff");
      rtn.addNote(note);
    }

    return rtn;
  }

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernatePoolDao dao;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testChangeLogFunctionality() throws Exception {
    PoolImpl testPool = getATestPool(1, new Date(), false, 0);

    dao.create(testPool);

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
    dao.update(testPool);
  }

  @Test
  public void testGetByBarcode() throws Exception {
    final PoolImpl testPool = getATestPool(17, new Date(), false, 3);
    final String idBarcode = testPool.getIdentificationBarcode();
    // non existing pool check
    assertNull(dao.getByBarcode(idBarcode));
    dao.create(testPool);
    Pool result = dao.getByBarcode(idBarcode);
    assertNotNull(result);
    compareFields(testPool, result);
  }

  @Test
  public void testGetByBarcodeNull() throws Exception {
    exception.expect(NullPointerException.class);
    dao.getByBarcode(null);
  }

  @Test
  public void testList() throws IOException {
    assertTrue(dao.list().size() > 0);
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
  public void testSaveEmpty() throws Exception {

    final Date creationDate = new Date();
    final PoolImpl saveMe = getATestPool(1, creationDate, true, 0);
    final long rtn = dao.create(saveMe);

    // check they're actually the same
    Pool freshPool = dao.get(rtn);
    compareFields(saveMe, freshPool);
  }

  @Test
  public void testSaveEmptyWithNotes() throws Exception {

    final Date creationDate = new Date();
    final PoolImpl saveMe = getATestPool(1, creationDate, true, 10);
    final long rtn = dao.create(saveMe);

    // check they're actually the same
    Pool freshPool = dao.get(rtn);
    compareFields(saveMe, freshPool);
  }

  @Test
  public void testSaveNonEmpty() throws Exception {

    final Date creationDate = new Date();
    final Pool saveMe = getATestPool(1, creationDate, false, 0);
    final long rtn = dao.create(saveMe);

    // check they're actually the same
    Pool freshPool = dao.get(rtn);
    compareFields(saveMe, freshPool);
  }

  @Test
  public void testCountIlluminaPools() throws IOException {
    assertTrue(dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA)) > 0);
  }

  @Test
  public void testCountIlluminaPoolsBadSearch() throws IOException {
    assertEquals(0L,
        dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA), PaginationFilter.query("DROP TABLE Pool;")));
  }

  @Test
  public void testCountIlluminaPoolsBySearch() throws IOException {
    assertEquals(2L, dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA), PaginationFilter.query("IPO1*")));
  }

  @Test
  public void testCountIlluminaPoolsEmptySearch() throws IOException {
    assertTrue(dao.count(PaginationFilter.platformType(PlatformType.ILLUMINA)) > 0);
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
  public void testListByIlluminaBadSearchWithLimit() throws IOException {
    List<Pool> pools = dao.list(5, 3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA),
        PaginationFilter.query("; DROP TABLE Pool;"));
    assertEquals(0L, pools.size());
  }

  @Test
  public void testListByIlluminaEmptySearchWithLimit() throws IOException {
    List<Pool> pools = dao.list(5, 3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA));
    assertEquals(3L, pools.size());
  }

  @Test
  public void testListByIlluminaSearchWithLimit() throws IOException {
    List<Pool> pools = dao.list(5, 3, true, "id", PaginationFilter.platformType(PlatformType.ILLUMINA),
        PaginationFilter.query("IPO*"));
    assertEquals(3, pools.size());
    assertEquals(6L, pools.get(0).getId());
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
    pool.getPoolContents().removeIf(pd -> pd.getAliquot().getId() == 10L);
    assertEquals("LDI8 should be removed from collection", originalSize - 1, pool.getPoolContents().size());
    dao.update(pool);

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
    ListLibraryAliquotView ldi =
        (ListLibraryAliquotView) sessionFactory.getCurrentSession().get(ListLibraryAliquotView.class, 14L);
    PoolElement element = new PoolElement(pool, ldi);
    pool.getPoolContents().add(element);
    dao.update(pool);

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
      assertEquals(String.format("Original proportion of %s should be 1", pd.getAliquot().getName()), 1,
          pd.getProportion());
      pd.setProportion(3);
    });
    dao.update(pool);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Pool saved = dao.get(1L);
    saved.getPoolContents().forEach(pd -> {
      assertEquals(String.format("Saved proportion of %s should be 3", pd.getAliquot().getName()), 3,
          pd.getProportion());
    });
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(dao::listByIdList, Arrays.asList(1L, 3L, 5L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(dao::listByIdList);
  }

  @Test
  public void testGetPartitionCount() throws Exception {
    Pool pool = (Pool) currentSession().get(PoolImpl.class, 1L);
    assertEquals(1L, dao.getPartitionCount(pool));
  }

  @Test
  public void testGetPartitionCountNone() throws Exception {
    Pool pool = (Pool) currentSession().get(PoolImpl.class, 3L);
    assertEquals(0L, dao.getPartitionCount(pool));
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "Pool 3";
    Pool pool = dao.getByAlias(alias);
    assertNotNull(pool);
    assertEquals(alias, pool.getAlias());
  }

}
