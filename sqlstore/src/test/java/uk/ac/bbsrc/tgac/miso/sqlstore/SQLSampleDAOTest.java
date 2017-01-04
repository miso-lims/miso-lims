package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleDao;

public class SQLSampleDAOTest extends AbstractDAOTest {

  private static final Logger log = LoggerFactory.getLogger(SQLSampleDAOTest.class);
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private JdbcTemplate template;
  @Mock
  private SecurityStore securityDAO;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  @Mock
  private ChangeLogStore changeLogDAO;
  @Mock
  private ProjectStore projectStore;
  @Mock
  private LibraryStore libraryStore;
  @Mock
  private SampleQcStore sampleQCStore;
  @Mock
  private NoteStore noteStore;
  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private HibernateSampleDao dao;

  @Autowired
  private SessionFactory sessionFactory;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    dao.setJdbcTemplate(template);
    dao.setSecurityProfileDao(securityProfileDAO);
  }

  @Test
  public void testListAll() throws IOException {
    Collection<Sample> samples = dao.listAll();
    samples.iterator().next().getScientificName().equals("Homo sapiens");
    assertEquals(17, samples.size());
  }

  @Test
  public void testSaveNew() throws Exception {

    Sample sample = new SampleImpl();
    String sampleName = "latestSample32";
    sample.setName(sampleName);
    sample.setAlias("alias32LK");
    sample.setProject(dao.get(1L).getProject());
    sample.setId(AbstractSample.UNSAVED_ID);
    User user = new UserImpl();
    user.setUserId(1L);
    sample.setLastModifier(user);

    mockAutoIncrement();
    when(namingScheme.generateNameFor(any(Sample.class))).thenReturn(sampleName);
    when(namingScheme.validateName(anyString())).thenReturn(ValidationResult.success());
    when(namingScheme.validateSampleAlias(anyString())).thenReturn(ValidationResult.success());
    when(securityProfileDAO.save(any(SecurityProfile.class))).thenReturn(3L);

    int sizeBefore = dao.listAll().size();
    long id = dao.save(sample);
    Sample retrieved = dao.get(id);
    assertEquals("did not insert sample", sizeBefore + 1, dao.listAll().size());
    assertEquals("sample name does not match", sampleName, retrieved.getName());
    assertEquals("Security profile does not match", 3L, (long) retrieved.getSecurityProfileId());
  }

  @Test
  public void testSaveExisting() throws Exception {

    Sample sample = dao.get(8);

    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
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

    when(namingScheme.generateNameFor(any(Sample.class))).thenReturn(sampleName);
    when(namingScheme.validateName(anyString())).thenReturn(ValidationResult.success());
    when(namingScheme.validateSampleAlias(anyString())).thenReturn(ValidationResult.success());

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
      if (previous != null) {
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
    dao.setCascadeType(CascadeType.ALL);

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
    List<Sample> samples = dao.listByOffsetAndNumResults(0, 2, "id", "desc");
    assertEquals(2, samples.size());
    assertEquals(17L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetThreeWithThreeSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.listByOffsetAndNumResults(3, 3, "id", "desc");
    assertEquals(3, samples.size());
    assertEquals(14L, samples.get(0).getId());
  }

  @Test
  public void getSamplesOffsetThreeWithThreeSamplesPerPageOrderLastModTest() throws Exception {
    List<Sample> samples = dao.listByOffsetAndNumResults(2, 2, "lastModified", "desc");
    assertEquals(2, samples.size());
    assertEquals(15L, samples.get(0).getId());
  }

  @Test
  public void getSamplesBySearchOffsetZeroWithTwoSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.listBySearchOffsetAndNumResults(0, 2, "TEST_0006", "id", "asc");
    assertEquals(2, samples.size());
    assertEquals(11L, samples.get(0).getId());
  }

  @Test
  public void getSamplesBySearchOffsetZeroWithTenSamplesPerPageTest() throws Exception {
    List<Sample> samples = dao.listBySearchOffsetAndNumResults(0, 10, "SaM1", "id", "desc");
    assertEquals(9, samples.size());
    assertEquals(17L, samples.get(0).getId());
  }

  @Test
  public void countSamplesBySearch() throws IOException {
    Long numSamples = dao.countBySearch("SAM1");
    assertEquals(Long.valueOf(9L), numSamples);
  }

  @Test
  public void countSamplesByBadSearch() throws IOException {
    Long numSamples = dao.countBySearch(";DROP TABLE Sample;");
    assertEquals(Long.valueOf(0), numSamples);
  }

  @Test
  public void countSamplesByEmptySearch() throws IOException {
    Long numSamples = dao.countBySearch("");
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

  private void mockAutoIncrement() throws IOException {
    Collection<Sample> samples = dao.listAll();
    long max = 0;
    for (Sample sample : samples) {
      if (sample.getId() > max) {
        max = sample.getId();
      }
    }
    max++;
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", max);
  }

  @Test
  public void getNextSiblingNumberTest() throws Exception {
    // sibling numbers are missing. getNextSiblingNumber should fix these and return first truly available number
    DetailedSample s1 = (DetailedSample) dao.get(16L);
    DetailedSample s2 = (DetailedSample) dao.get(17L);
    assertNull(s1.getSiblingNumber());
    assertNull(s2.getSiblingNumber());
    assertEquals(3, dao.getNextSiblingNumber("TEST_0001_TISSUE_"));
    sessionFactory.getCurrentSession().refresh(s1);
    sessionFactory.getCurrentSession().refresh(s2);
    assertEquals(new Integer(1), s1.getSiblingNumber());
    assertEquals(new Integer(2), s2.getSiblingNumber());
  }

  @Test
  public void testNotes() throws Exception {
    long runId = 1L;
    String message = "test message";
    Note note = new Note();
    note.setText(message);

    Sample sample = dao.get(runId);
    assertNotNull(sample);
    assertEquals(0, sample.getNotes().size());

    dao.addNote(sample, note);
    Sample sampleWithNote = dao.get(runId);
    assertEquals(1, sampleWithNote.getNotes().size());
    Note savedNote = sampleWithNote.getNotes().iterator().next();
    assertEquals(message, savedNote.getText());
    Long noteId = savedNote.getNoteId();
    assertNotNull(sessionFactory.getCurrentSession().get(Note.class, noteId));

    dao.deleteNote(sampleWithNote, savedNote);
    Sample sampleNoteDeleted = dao.get(runId);
    assertEquals(0, sampleNoteDeleted.getNotes().size());
    assertNull(sessionFactory.getCurrentSession().get(Note.class, noteId));
  }

}
