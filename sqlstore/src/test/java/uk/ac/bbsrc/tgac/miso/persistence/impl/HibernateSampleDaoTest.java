package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
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
    assertEquals(20, samples.size());
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
    Date now = new Date();
    sample.setCreator(user);
    sample.setCreationTime(now);
    sample.setLastModifier(user);
    sample.setLastModified(now);

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
    project.setId(2L);
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
  public void testCount() throws Exception {
    int total = dao.count();
    assertEquals(20, total);
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
    assertEquals("sample location type does not match", "2014-01-17", LimsUtils.formatDate(sample.getReceivedDate()));
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
    SampleIdentity identity = (SampleIdentity) sample;
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
    Collection<String> sampleTypes = dao.listSampleTypes();
    List<String> types = Arrays
        .asList("NON GENOMIC", "GENOMIC", "OTHER", "VIRAL RNA", "SYNTHETIC", "TRANSCRIPTOMIC", "METAGENOMIC", "METATRANSCRIPTOMIC");

    assertTrue("Did not find all sample types", sampleTypes.containsAll(types));
  }

  @Test
  public void getSamplesOffsetZeroWithTwoSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(0, 2, false, "id");
    assertEquals(2, samples.size());
    assertEquals(20L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetThreeWithThreeSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(3, 3, false, "id");
    assertEquals(3, samples.size());
    assertEquals(17L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetTwoWithTwoSamplesPerPageOrderLastModTest() throws Exception {
    List<Sample> samples = dao.list(2, 2, false, "lastModified");
    assertEquals(2, samples.size());
    assertEquals(18L, samples.get(0).getId());
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
    assertEquals(10, samples.size());
    assertEquals(19L, samples.get(0).getId());
  }

  @Test
  public void countSamplesBySearch() throws IOException {
    Long numSamples = dao.count(PaginationFilter.query("SAM1"));
    assertEquals(Long.valueOf(11L), numSamples);
  }

  @Test
  public void countSamplesByBadSearch() throws IOException {
    Long numSamples = dao.count(PaginationFilter.query(";DROP TABLE Sample;"));
    assertEquals(Long.valueOf(0), numSamples);
  }

  @Test
  public void countSamplesByEmptySearch() throws IOException {
    Long numSamples = dao.count(PaginationFilter.query(""));
    assertEquals(Long.valueOf(20L), numSamples);
  }

  @Test
  public void testGetIdentityByPartialMatchExternalName() throws IOException {
    Collection<SampleIdentity> identity = dao.getIdentitiesByExternalNameOrAliasAndProject("EXT1", null, false);
    assertEquals(1, identity.size());
  }

  @Test
  public void testGetIdentityByExactMatchExternalNameWithNonExactMatch() throws IOException {
    String query = "EXT1";
    Collection<SampleIdentity> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(query, null, true);
    assertTrue(exactMatches.isEmpty());
  }

  @Test
  public void testGetIdentityByExactMatchExternalNameWithExactMatch() throws IOException {
    String query = "EXT15";
    Collection<SampleIdentity> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(query, null, true);
    assertFalse(exactMatches.isEmpty());
  }

  @Test
  public void testGetIdentityByExactMatchExternalNameWithEmptyString() throws IOException {
    String query = "";
    Collection<SampleIdentity> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(query, null, true);
    assertTrue(exactMatches.isEmpty());
  }

  @Test
  public void testGetIdentityByExternalNameAndProjectExactMatch() throws IOException {
    String externalName = "EXT15";
    long projectId = 1;
    Collection<SampleIdentity> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(externalName, projectId, true);
    assertFalse(exactMatches.isEmpty());
    assertEquals(1, exactMatches.size());
  }

  @Test
  public void testGetIdentityByExternalNameAndProjectNonExactMatch() throws IOException {
    String externalName = "EXT1";
    long projectId = 1;
    Collection<SampleIdentity> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(externalName, projectId, true);
    assertTrue(exactMatches.isEmpty());
  }

  @Test
  public void getIdentityByAlias() throws IOException {
    Collection<SampleIdentity> identities = dao.getIdentitiesByExternalNameOrAliasAndProject("TEST_0001_IDENTITY_1", null, true);
    assertEquals(1, identities.size());
    assertEquals("TEST_0001_IDENTITY_1", identities.iterator().next().getAlias());
  }

  @Test
  public void getIdentityByNullAlias() throws IOException {
    Collection<SampleIdentity> identities = dao.getIdentitiesByExternalNameOrAliasAndProject(null, null, true);
    assertTrue(identities.isEmpty());
  }

  @Test
  public void getIdentityByNonIdentityAlias() throws IOException {
    Collection<SampleIdentity> identities = dao.getIdentitiesByExternalNameOrAliasAndProject("TEST_0001_Bn_P_nn_1-1_D_1", null, false);
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
  public void getMatchingGhostTissueTest() throws Exception {
    SampleTissue tissue = new SampleTissueImpl();
    tissue.setParent(new SampleIdentityImpl());
    tissue.getParent().setId(15L);
    tissue.setTissueOrigin(new TissueOriginImpl());
    tissue.getTissueOrigin().setId(1L);
    tissue.setTissueType(new TissueTypeImpl());
    tissue.getTissueType().setId(1L);
    tissue.setTimesReceived(1);
    tissue.setTubeNumber(1);
    SampleTissue match = dao.getMatchingGhostTissue(tissue);
    assertNotNull(match);
    assertEquals(16L, match.getId());
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("SAM1"));
  }

  @Test
  public void testSearchByClass() throws IOException {
    testSearch(PaginationFilter.sampleClass("gDNA"));
  }

  @Test
  public void testSearchByInstitute() throws IOException {
    testSearch(PaginationFilter.institute("OICR"));
  }

  @Test
  public void testSearchByExternal() throws IOException {
    testSearch(PaginationFilter.external("EXT"));
  }

  @Test
  public void testSearchBySubproject() throws IOException {
    testSearch(PaginationFilter.subproject("Mini"));
  }

  @Test
  public void testSearchByCreated() throws IOException {
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2017-01-01"), LimsUtils.parseDate("2018-01-01"), DateType.CREATE));
  }

  @Test
  public void testSearchByReceived() throws IOException {
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2017-01-01"), LimsUtils.parseDate("2018-01-01"), DateType.RECEIVE));
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
  public void testSearchByBox() throws IOException {
    testSearch(PaginationFilter.box("BOX1"));
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
