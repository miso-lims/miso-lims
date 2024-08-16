package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.data.type.ConsentLevel;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateSampleDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateSampleDao dao;

  @Before
  public void setup() {
    dao = new HibernateSampleDao();
    dao.setSessionFactory(sessionFactory);
    dao.setDetailedSample(true);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<Sample> samples = dao.list();
    assertEquals(24, samples.size());
  }

  @Test
  public void testCreate() throws Exception {
    Sample sample = new SampleImpl();
    String sampleName = "latestSample32";
    sample.setName(sampleName);
    sample.setAlias("alias32LK");
    sample.setProject(dao.get(1L).getProject());
    sample.setSampleType("GENOMIC");
    ScientificName scientificName = (ScientificName) currentSession().get(ScientificName.class, 1L);
    sample.setScientificName(scientificName);
    User user = new UserImpl();
    user.setId(1L);
    Date now = new Date();
    sample.setCreator(user);
    sample.setCreationTime(now);
    sample.setLastModifier(user);
    sample.setLastModified(now);

    int sizeBefore = dao.list().size();
    long id = dao.create(sample);

    clearSession();

    Sample retrieved = dao.get(id);
    assertEquals("did not insert sample", sizeBefore + 1, dao.list().size());
    assertEquals("sample name does not match", sampleName, retrieved.getName());
  }

  @Test
  public void testUpdate() throws Exception {
    Sample sample = dao.get(8);

    Project project = new ProjectImpl();
    project.setId(2L);
    sample.setProject(project);

    String sampleName = "updatedSample";
    sample.setName(sampleName);
    sample.setAlias("updatedAlias");
    User user = new UserImpl();
    user.setId(1L);
    sample.setLastModifier(user);

    int sizeBefore = dao.list().size();
    long id = dao.update(sample);

    clearSession();

    Sample retrieved = dao.get(id);
    assertEquals("sample name does not match", sampleName, retrieved.getName());
    assertEquals("did not update sample", sizeBefore, dao.list().size());
  }

  @Test
  public void testGet() throws Exception {
    Sample sample = dao.get(3);
    assertNotNull(sample);
    assertEquals("SAM3", sample.getName());
    assertEquals("Inherited from TEST_0002", sample.getDescription());
    assertNull(sample.getAccession());
    assertEquals("SAM3::TEST_0002_Bn_P_nn_1-1_D_1", sample.getIdentificationBarcode());
    assertEquals("Freezer1_3", sample.getLocationBarcode());
    assertEquals("GENOMIC", sample.getSampleType());
    assertEquals("Passed", sample.getDetailedQcStatus().getDescription());
    assertEquals("TEST_0002_Bn_P_nn_1-1_D_1", sample.getAlias());
    assertEquals("Homo sapiens", sample.getScientificName().getAlias());
    assertNull(sample.getTaxonIdentifier());
  }

  @Test
  public void testGetPlainSample() throws Exception {
    Sample sample = dao.get(1L);
    assertTrue(LimsUtils.isPlainSample(sample));
  }

  @Test
  public void testGetDetailedSample() throws Exception {
    Sample sample = dao.get(18L);
    assertTrue(LimsUtils.isDetailedSample(sample));
    DetailedSample detailed = (DetailedSample) sample;
    assertNotNull(detailed.getTissueAttributes());
    assertNotNull(detailed.getTissueAttributes().getTissueOrigin());
    assertEquals("Test Origin", detailed.getTissueAttributes().getTissueOrigin().getAlias());
    assertNotNull(detailed.getIdentityAttributes());
    assertEquals(ConsentLevel.THIS_PROJECT, detailed.getIdentityAttributes().getConsentLevel());
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
  public void testListByBarcodeList() throws Exception {
    Collection<Sample> samples =
        dao.listByBarcodeList(Arrays.asList("SAM7::TEST_0004_Bn_P_nn_1-1_D_1", "SAM11::TEST_0006_Bn_P_nn_1-1_D_1"));
    assertEquals("Sample size does not match", 2, samples.size());

    for (Sample sample : samples) {
      assertTrue("did not find id " + sample.getId(), Arrays.asList(7L, 11L).contains(sample.getId()));
    }
  }

  @Test
  public void testListByBarcodeListNone() throws Exception {
    Collection<Sample> samples = dao.listByBarcodeList(Collections.emptyList());
    assertNotNull(samples);
    assertTrue(samples.isEmpty());
  }

  @Test
  public void testGetByLibraryAliquotId() throws Exception {
    Sample sample = dao.getByLibraryAliquotId(5L);
    assertNotNull(sample);
    assertEquals(5L, sample.getId());
  }

  @Test
  public void testListByAlias() throws Exception {
    String alias = "TEST_0007_Bn_P_nn_1-1_D_1";
    List<EntityReference> samples = dao.listByAlias(alias);
    assertEquals(1, samples.size());
    assertEquals("wrong sample found", 13, samples.get(0).getId());
    assertEquals(alias, samples.get(0).getLabel());
  }

  @Test
  public void getSamplesOffsetZeroWithTwoSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(0, 2, false, "id");
    assertEquals(2, samples.size());
    assertEquals(24L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetThreeWithThreeSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(3, 3, false, "id");
    assertEquals(3, samples.size());
    assertEquals(21L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetTwoWithTwoSamplesPerPageOrderLastModTest() throws Exception {
    List<Sample> samples = dao.list(2, 2, false, "lastModified");
    assertEquals(2, samples.size());
    assertEquals(24L, samples.get(0).getId());
  }

  @Test
  public void getSamplesBySearchOffsetZeroWithTwoSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(0, 2, true, "id", PaginationFilter.query("TEST_0006*"));
    assertEquals(2, samples.size());
    assertEquals(11L, samples.get(0).getId());
  }

  @Test
  public void getSamplesBySearchOffsetZeroWithTenSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.list(0, 10, false, "id", PaginationFilter.query("SaM1*"));
    assertEquals(10, samples.size());
    assertEquals(19L, samples.get(0).getId());
  }

  @Test
  public void countSamplesBySearch() throws IOException {
    long numSamples = dao.count(PaginationFilter.query("SAM1*"));
    assertEquals(11L, numSamples);
  }

  @Test
  public void countSamplesByBadSearch() throws IOException {
    long numSamples = dao.count(PaginationFilter.query(";DROP TABLE Sample;"));
    assertEquals(0L, numSamples);
  }

  @Test
  public void testGetIdentityByPartialMatchExternalName() throws IOException {
    Collection<IdentityView> identity = dao.getIdentitiesByExternalNameOrAliasAndProject("EXT1", null, false);
    assertEquals(1, identity.size());
  }

  @Test
  public void testGetIdentityByExactMatchExternalNameWithNonExactMatch() throws IOException {
    String query = "EXT1";
    Collection<IdentityView> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(query, null, true);
    assertTrue(exactMatches.isEmpty());
  }

  @Test
  public void testGetIdentityByExactMatchExternalNameWithExactMatch() throws IOException {
    String query = "EXT15";
    Collection<IdentityView> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(query, null, true);
    assertFalse(exactMatches.isEmpty());
  }

  @Test
  public void testGetIdentityByExactMatchExternalNameWithEmptyString() throws IOException {
    String query = "";
    Collection<IdentityView> exactMatches = dao.getIdentitiesByExternalNameOrAliasAndProject(query, null, true);
    assertTrue(exactMatches.isEmpty());
  }

  @Test
  public void testGetIdentityByExternalNameAndProjectExactMatch() throws IOException {
    String externalName = "EXT15";
    long projectId = 1;
    Collection<IdentityView> exactMatches =
        dao.getIdentitiesByExternalNameOrAliasAndProject(externalName, projectId, true);
    assertFalse(exactMatches.isEmpty());
    assertEquals(1, exactMatches.size());
  }

  @Test
  public void testGetIdentityByExternalNameAndProjectNonExactMatch() throws IOException {
    String externalName = "EXT1";
    long projectId = 1;
    Collection<IdentityView> exactMatches =
        dao.getIdentitiesByExternalNameOrAliasAndProject(externalName, projectId, true);
    assertTrue(exactMatches.isEmpty());
  }

  @Test
  public void getIdentityByAlias() throws IOException {
    Collection<IdentityView> identities =
        dao.getIdentitiesByExternalNameOrAliasAndProject("TEST_0001_IDENTITY_1", null, true);
    assertEquals(1, identities.size());
    assertEquals("TEST_0001_IDENTITY_1", identities.iterator().next().getAlias());
  }

  @Test
  public void getIdentityByNullAlias() throws IOException {
    Collection<IdentityView> identities = dao.getIdentitiesByExternalNameOrAliasAndProject(null, null, true);
    assertTrue(identities.isEmpty());
  }

  @Test
  public void getIdentityByNonIdentityAlias() throws IOException {
    Collection<IdentityView> identities =
        dao.getIdentitiesByExternalNameOrAliasAndProject("TEST_0001_Bn_P_nn_1-1_D_1", null, false);
    assertTrue(identities.isEmpty());
  }

  @Test
  public void getSampleWithChildrenTest() throws Exception {
    Sample sample = dao.get(15L);
    assertTrue(LimsUtils.isDetailedSample(sample));
    DetailedSample detailed = (DetailedSample) sample;
    assertNotNull(detailed.getChildren());
    assertEquals(3, detailed.getChildren().size());
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
  public void testGetChildSampleCount() throws Exception {
    Sample parent = (Sample) currentSession().get(SampleImpl.class, 15L);
    assertEquals(3L, dao.getChildSampleCount(parent));
  }

  @Test
  public void testListByIdList() throws Exception {
    List<Long> ids = Arrays.asList(16L, 17L, 21L);
    List<Sample> results = dao.listByIdList(ids);
    assertNotNull(results);
    assertEquals(ids.size(), results.size());
    for (Long id : ids) {
      assertTrue(results.stream().anyMatch(x -> x.getId() == id.longValue()));
    }
  }

  @Test
  public void testListByIdListNone() throws Exception {
    List<Sample> results = dao.listByIdList(Collections.emptyList());
    assertNotNull(results);
    assertTrue(results.isEmpty());
  }

  @Test
  public void testGetPreviousInProject() throws Exception {
    Sample sample = (Sample) currentSession().get(SampleImpl.class, 23L);
    EntityReference reference = dao.getPreviousInProject(sample);
    assertNotNull(reference);
    assertEquals(22L, reference.getId());
  }

  @Test
  public void testGetPreviousInProjectNone() throws Exception {
    Sample sample = (Sample) currentSession().get(SampleImpl.class, 22L);
    assertNull(dao.getPreviousInProject(sample));
  }

  @Test
  public void testGetNextInProject() throws Exception {
    Sample sample = (Sample) currentSession().get(SampleImpl.class, 23L);
    EntityReference reference = dao.getNextInProject(sample);
    assertNotNull(reference);
    assertEquals(24L, reference.getId());
  }

  @Test
  public void testGetNextInProjectNone() throws Exception {
    Sample sample = (Sample) currentSession().get(SampleImpl.class, 24L);
    assertNull(dao.getNextInProject(sample));
  }

  @Test
  public void testPropertyForDateNull() throws Exception {
    // receive and distributed dates are handled by JpaCriteriaPaginatedBoxableSource
    QueryBuilder<?, SampleImpl> builder = Mockito.mock(QueryBuilder.class);
    assertNull(dao.propertyForDate(builder, DateType.RECEIVE));
    assertNull(dao.propertyForDate(builder, DateType.DISTRIBUTED));
  }

  @Test
  public void testGetChildren() throws Exception {
    final long identityId = 15L;
    final long stockId = 18L;
    List<Sample> children = dao.getChildren(Arrays.asList(identityId), SampleStock.CATEGORY_NAME, 2L);
    assertNotNull(children);
    assertEquals(1, children.size());
    assertEquals(stockId, children.get(0).getId());
  }

  @Test
  public void testGetChildIds() throws Exception {
    final long identityId = 15L;
    final long stockId = 18L;
    Set<Long> ids = dao.getChildIds(Arrays.asList(identityId), SampleStock.CATEGORY_NAME, null);
    assertNotNull(ids);
    assertEquals(1, ids.size());
    assertTrue(ids.contains(stockId));
  }

}
