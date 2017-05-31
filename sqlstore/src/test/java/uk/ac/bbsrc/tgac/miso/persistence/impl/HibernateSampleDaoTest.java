package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateSampleDaoTest extends AbstractDAOTest {

  private static final Logger log = LoggerFactory.getLogger(HibernateSampleDaoTest.class);
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateSampleDao dao;

  @Before
  public void setup() {
    dao = new HibernateSampleDao();
    dao.setSessionFactory(sessionFactory);
    dao.setJdbcTemplate(template);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<Sample> samples = dao.listAll();
    samples.iterator().next().getScientificName().equals("Homo sapiens");
    assertEquals(17, samples.size());
  }

  @Test
  public void testSaveNew() throws Exception {
    SecurityProfile profile = new SecurityProfile();
    profile.setProfileId(1L);

    Sample sample = new SampleImpl();
    sample.setSecurityProfile(profile);
    String sampleName = "latestSample32";
    sample.setName(sampleName);
    sample.setAlias("alias32LK");
    sample.setProject(dao.get(1L).getProject());
    sample.setId(AbstractSample.UNSAVED_ID);
    User user = new UserImpl();
    user.setUserId(1L);
    sample.setLastModifier(user);

    int sizeBefore = dao.listAll().size();
    long id = dao.save(sample);
    Sample retrieved = dao.get(id);
    assertEquals("did not insert sample", sizeBefore + 1, dao.listAll().size());
    assertEquals("sample name does not match", sampleName, retrieved.getName());
  }

  @Test
  public void testSaveExisting() throws Exception {

    Sample sample = dao.get(8);

    SecurityProfile profile = new SecurityProfile();
    profile.setProfileId(1L);
    sample.setSecurityProfile(profile);

    Project project = new ProjectImpl();
    project.setProjectId(2L);
    sample.setProject(project);

    String sampleName = "updatedSample";
    sample.setName(sampleName);
    sample.setAlias("updatedAlias");
    User user = new UserImpl();
    user.setUserId(1L);
    sample.setLastModifier(user);

    int sizeBefore = dao.listAll().size();
    long id = dao.save(sample);
    Sample retrieved = dao.get(id);
    assertEquals("sample name does not match", sampleName, retrieved.getName());
    assertEquals("did not update sample", sizeBefore, dao.listAll().size());
  }

  @Test
  public void testListAllWithLimit() throws Exception {
    Collection<Sample> samples = dao.listAllWithLimit(3);
    assertEquals(3, samples.size());
  }

  @Test
  public void testListAllByReceivedDate() throws Exception {
    Collection<Sample> samples = dao.listAllByReceivedDate(99);

    Date previous = null;
    for (Sample sample : samples) {
      if (previous != null && sample.getReceivedDate() != null) {
        log.debug("testing receivedDates " + previous + " comes before " + sample.getReceivedDate());
        assertTrue(
            "not ordered by received date descending",
            previous.equals(sample.getReceivedDate()) || previous.after(sample.getReceivedDate()));
      }
      previous = sample.getReceivedDate();
    }

  }

  @Test
  public void testCount() throws Exception {
    int total = dao.count();
    assertEquals(17, total);
  }

  @Test
  public void testRemove() throws Exception {

    Sample sample = dao.get(7);

    assertTrue(dao.remove(sample));
    assertNull(dao.get(7));

  }

  @Test
  public void testGet() throws Exception {
    Sample sample = dao.get(3);
    assertNotNull(sample);
    assertEquals("sample name does not match", "SAM3", sample.getName());
    assertEquals("sample description does not match", "Inherited from TEST_0002", sample.getDescription());
    assertNull("sample accession is not null", sample.getAccession());
    assertEquals("sample identification does not match", "SAM3::TEST_0002_Bn_P_nn_1-1_D_1", sample.getIdentificationBarcode());
    assertEquals("sample location barcode does not match", "Freezer1_3", sample.getLocationBarcode());
    assertEquals("sample location type does not match", "GENOMIC", sample.getSampleType());
    assertTrue(sample.getQcPassed());
    assertEquals("sample alias type does not match", "TEST_0002_Bn_P_nn_1-1_D_1", sample.getAlias());
    assertEquals("sample scientific name does not match", "Homo sapiens", sample.getScientificName());
    assertNull("sample scientific name does not match", sample.getTaxonIdentifier());
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    assertEquals("sample location type does not match", "2014-01-17", df.format(sample.getReceivedDate()));
  }

  @Test
  public void testGetPlainSample() throws Exception {
    Sample sample = dao.get(1L);
    assertTrue(LimsUtils.isPlainSample(sample));
  }

  @Test
  public void testGetDetailedSample() throws Exception {
    Sample sample = dao.get(15L);
    assertTrue(LimsUtils.isDetailedSample(sample));
  }

  @Test
  public void testGetIdentitySample() throws Exception {
    Sample sample = dao.get(15L);
    assertTrue(LimsUtils.isDetailedSample(sample));
    assertTrue(LimsUtils.isIdentitySample(sample));
    Identity identity = (Identity) sample;
    assertEquals("15_EXT15,EXT15", identity.getExternalName());
  }

  @Test
  public void testGetTissueSample() throws Exception {
    Sample sample = dao.get(16L);
    assertTrue(LimsUtils.isDetailedSample(sample));
    assertTrue(LimsUtils.isTissueSample(sample));
  }

  @Test
  public void testGetByBarcode() throws Exception {
    Sample sample = dao.getByBarcode("SAM7::TEST_0004_Bn_P_nn_1-1_D_1");
    assertEquals("Sample id does not match", 7, sample.getId());
  }

  @Test
  public void testGetByBarcodeList() throws Exception {
    Collection<Sample> samples = dao.getByBarcodeList(Arrays.asList("SAM7::TEST_0004_Bn_P_nn_1-1_D_1", "SAM11::TEST_0006_Bn_P_nn_1-1_D_1"));
    assertEquals("Sample size does not match", 2, samples.size());

    for (Sample sample : samples) {
      assertTrue("did not find id " + sample.getId(), Arrays.asList(7L, 11L).contains(sample.getId()));
    }

  }

  @Test
  public void testListByAlias() throws Exception {
    Collection<Sample> samples = dao.listByAlias("TEST_0007_Bn_P_nn_1-1_D_1");
    assertEquals("wrong sample found", 13, ((Sample) samples.toArray()[0]).getId());
  }

  @Test
  public void testListAllSampleTypes() throws Exception {
    Collection<String> sampleTypes = dao.listAllSampleTypes();
    List<String> types = Arrays
        .asList("NON GENOMIC", "GENOMIC", "OTHER", "VIRAL RNA", "SYNTHETIC", "TRANSCRIPTOMIC", "METAGENOMIC", "METATRANSCRIPTOMIC");

    assertTrue("Did not find all sample types", sampleTypes.containsAll(types));
  }

  @Test
  public void getSamplesOffsetZeroWithTwoSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(0, 2, false, "id");
    assertEquals(2, samples.size());
    assertEquals(17L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetThreeWithThreeSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(3, 3, false, "id");
    assertEquals(3, samples.size());
    assertEquals(14L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetThreeWithThreeSamplesPerPageOrderLastModTest() throws Exception {
    List<Sample> samples = dao.list(2, 2, false, "lastModified");
    assertEquals(2, samples.size());
    assertEquals(15L, samples.get(0).getId());
  }

  @Test
  public void getSamplesBySearchOffsetZeroWithTwoSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(0, 2, true, "id", PaginationFilter.query("TEST_0006"));
    assertEquals(2, samples.size());
    assertEquals(11L, samples.get(0).getId());
  }

  @Test
  public void getSamplesBySearchOffsetZeroWithTenSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(0, 10, false, "id", PaginationFilter.query("SaM1"));
    assertEquals(9, samples.size());
    assertEquals(17L, samples.get(0).getId());
  }

  @Test
  public void countSamplesBySearch() throws IOException {
    Long numSamples = dao.count(PaginationFilter.query("SAM1"));
    assertEquals(Long.valueOf(9L), numSamples);
  }

  @Test
  public void countSamplesByBadSearch() throws IOException {
    Long numSamples = dao.count(PaginationFilter.query(";DROP TABLE Sample;"));
    assertEquals(Long.valueOf(0), numSamples);
  }

  @Test
  public void countSamplesByEmptySearch() throws IOException {
    Long numSamples = dao.count(PaginationFilter.query(""));
    assertEquals(Long.valueOf(17L), numSamples);
  }

  @Test
  public void testGetIdentityByExternalName() throws IOException {
    List<Identity> identity = (List<Identity>) dao.getIdentitiesByExternalNameOrAlias("EXT15");
    assertTrue(identity.get(0).getExternalName().contains("EXT15"));
  }

  @Test
  public void getIdentityByAlias() throws IOException {
    Collection<Identity> identities = dao.getIdentitiesByExternalNameOrAlias("TEST_0001_IDENTITY_1");
    assertEquals(1, identities.size());
    assertEquals("TEST_0001_IDENTITY_1", identities.iterator().next().getAlias());
  }

  @Test
  public void getIdentityByNullAlias() throws IOException {
    Collection<Identity> identities = dao.getIdentitiesByExternalNameOrAlias(null);
    assertTrue(identities.isEmpty());
  }

  @Test
  public void getIdentityByNonIdentityAlias() throws IOException {
    Collection<Identity> identities = dao.getIdentitiesByExternalNameOrAlias("TEST_0001_Bn_P_nn_1-1_D_1");
    assertTrue(identities.isEmpty());
  }

  @Test
  public void getSampleWithChildrenTest() throws Exception {
    Sample sample = dao.get(15L);
    assertTrue(LimsUtils.isDetailedSample(sample));
    DetailedSample detailed = (DetailedSample) sample;
    assertNotNull(detailed.getChildren());
    assertEquals(2, detailed.getChildren().size());
    for (@SuppressWarnings("unused")
    Sample child : detailed.getChildren()) {
      // will throw ClassCastException if children are not correctly loaded as Samples
    }
  }

  @Test
  public void getSampleWithParentTest() throws Exception {
    SampleImpl sample = (SampleImpl) dao.get(16L);
    assertNotNull(sample);
    assertTrue(LimsUtils.isDetailedSample(sample));
    DetailedSample detailed = (DetailedSample) sample;
    assertNotNull(detailed.getParent());
    assertEquals(15L, detailed.getParent().getId());
  }

  @Test
  public void getNextSiblingNumberTest() throws Exception {
    String partialAlias = "TEST_0001_TISSUE_";
    DetailedSample s1 = (DetailedSample) dao.get(16L);
    DetailedSample s2 = (DetailedSample) dao.get(17L);
    assertTrue(s1.getAlias().startsWith(partialAlias));
    assertEquals(new Integer(1), s1.getSiblingNumber());
    assertTrue(s2.getAlias().startsWith(partialAlias));
    assertEquals(new Integer(2), s2.getSiblingNumber());
    assertEquals(3, dao.getNextSiblingNumber(partialAlias));
  }

  @Test
  public void concurrentSaveTest() throws Exception {
    final int jobCount = 10;
    testConcurrentTask(new ConcurrentTestWork() {

      private final Sample[] samples = new Sample[jobCount];

      @Override
      public void prepare(Session session, int jobNumber) throws Exception {
        SampleStock sample = new SampleStockImpl();
        String name = "SAMMY" + jobNumber;
        sample.setName(name);
        sample.setAlias(name);
        sample.setSampleType("type");
        Project project = (Project) session.get(ProjectImpl.class, 1L);
        sample.setProject(project);
        sample.setScientificName("scientific");
        User user = (User) session.get(UserImpl.class, 1L);
        sample.setLastModifier(user);
        SampleClass sampleClass = (SampleClass) session.get(SampleClassImpl.class, 3L);
        sample.setSampleClass(sampleClass);
        DetailedSample parent = (DetailedSample) session.get(SampleImpl.class, 17L);
        sample.setParent(parent);
        samples[jobNumber] = sample;
      }

      @Override
      public void doWork(HibernateSampleDao threadDao, int jobNumber) throws Exception {
        samples[jobNumber].setId(threadDao.save(samples[jobNumber]));
      }

      @Override
      public void assertEach(int jobNumber) throws Exception {
        assertNotNull(dao.get(samples[jobNumber].getId()));
      }

    }, jobCount);
  }

  @Test
  public void concurrentGetSiblingNumberTest() throws Exception {
    testConcurrentTask(new ConcurrentTestWork() {

      @Override
      public void doWork(HibernateSampleDao threadDao, int jobNumber) throws Exception {
        int num = threadDao.getNextSiblingNumber("TEST_0001_TISSUE_");
        assertEquals(3, num);
      }

    }, 10);
  }

  /**
   * Attempt to run a similar task on multiple threads to test concurrency. The scheduler could of course prevent some or all of the
   * tasks from actually running concurrently
   * 
   * @param work
   * @param jobCount
   * @throws Exception
   */
  private void testConcurrentTask(ConcurrentTestWork work, int jobCount) throws Exception {
    List<Thread> threads = new ArrayList<>();
    CyclicBarrier barrier = new CyclicBarrier(jobCount + 1);
    AtomicInteger successCount = new AtomicInteger(0);

    for (int i = 0; i < jobCount; i++) {
      final int jobNum = i;
      Thread thread = new Thread(() -> {
        try {
          Session session = sessionFactory.openSession();
          work.prepare(session, jobNum);
          // wait for all threads to be at the same point
          barrier.await(1, TimeUnit.SECONDS);
          SessionFactory mockSessionFactory = Mockito.mock(SessionFactory.class);
          Mockito.when(mockSessionFactory.getCurrentSession()).thenReturn(session);
          HibernateSampleDao threadDao = new HibernateSampleDao();
          threadDao.setJdbcTemplate(template);
          threadDao.setSessionFactory(mockSessionFactory);
          work.doWork(threadDao, jobNum);
          successCount.incrementAndGet();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
      threads.add(thread);
      thread.start();
    }

    // release all threads to do their work
    barrier.await(1, TimeUnit.SECONDS);

    for (Thread thread : threads) {
      thread.join(3000);
    }

    assertEquals(jobCount, successCount.get());
    for (int i = 0; i < jobCount; i++) {
      work.assertEach(i);
    }
  }

  private static interface ConcurrentTestWork {
    /**
     * Any preparation work needing done before the task to be concurrency-tested should be done here. Default implementation does nothing
     * 
     * @param session a Session that is valid for this thread. Should be used for database lookups
     * @param jobNumber
     * @throws Exception on any failure
     */
    public default void prepare(Session session, final int jobNumber) throws Exception {

    };

    /**
     * The work to be concurrency-tested. May contain assertions that don't require waiting until all jbos have completed
     * 
     * @param threadDao the dao to test against. Uses a Session valid for the thread
     * @param jobNumber
     * @throws Exception on any failure
     */
    public void doWork(HibernateSampleDao threadDao, final int jobNumber) throws Exception;

    /**
     * Test any relevant postconditions after all jobs are completed. Called once per job. Default implementation does nothing
     * 
     * @param jobNumber
     * @throws Exception
     */
    public default void assertEach(int jobNumber) throws Exception {

    };
  }

}
