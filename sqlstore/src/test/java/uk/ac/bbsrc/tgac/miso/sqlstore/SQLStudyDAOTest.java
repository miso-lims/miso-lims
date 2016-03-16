package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;

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

import net.sf.ehcache.CacheManager;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

public class SQLStudyDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Mock
  private Store<SecurityProfile> securityProfileDAO;

  @Mock
  private ProjectStore projectStore;

  @Mock
  private SecurityStore securityDAO;

  @Mock
  private ChangeLogStore changeLogDAO;

  @Mock
  private ExperimentStore experimentDAO;

  @Mock
  private MisoNamingScheme<Study> namingScheme;

  @InjectMocks
  private SQLStudyDAO dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 5L;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
  }

  @Test
  public void testSaveNull() throws IOException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    long autoIncrementId = nextAutoIncrementId;
    Study newStudy = makeStudy();
    mockAutoIncrement(autoIncrementId);
    Mockito.when(namingScheme.validateField(Matchers.anyString(), Matchers.anyString())).thenReturn(true);

    assertEquals(autoIncrementId, dao.save(newStudy));

    Study savedRun = dao.get(autoIncrementId);
    assertEquals(newStudy.getAlias(), savedRun.getAlias());
    nextAutoIncrementId += 1;
  }

  @Test
  public void testListAll() {
    List<Study> studies = dao.listAll();
    assertEquals(4, studies.size());
  }

  @Test
  public void testListAllWithLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(2L);
    assertEquals(2, studies.size());
  }

  @Test
  public void testListAllWithBiggerLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(50L);
    assertEquals(4, studies.size());
  }

  @Test
  public void testListAllWithZeroLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(0L);
    assertEquals(0, studies.size());
  }

  @Test
  public void testStudyCount() throws IOException {
    assertEquals(4, dao.count());
  }

  @Test
  public void testListBySearchOICR() {
    List<Study> runs = dao.listBySearch("OICR");
    assertEquals(2, runs.size());
  }

  @Test
  public void testListBySearchStu() {
    List<Study> runs = dao.listBySearch("STU");
    assertEquals(4, runs.size());
  }

  @Test
  public void testListBySearchNone() {
    List<Study> runs = dao.listBySearch("pizza");
    assertEquals(0, runs.size());
  }

  @Test
  public void testListBySearchEmpty() {
    List<Study> studies = dao.listBySearch("");
    assertEquals(4, studies.size());
  }

  @Test
  public void testListBySearchNull() {
    exception.expect(NullPointerException.class);
    dao.listBySearch(null);
  }

  @Test
  public void testListByProjectId() throws IOException {
    List<Study> studies = dao.listByProjectId(1L);
    assertEquals(1, studies.size());
  }

  @Test
  public void testListByProjectIdNone() throws IOException {
    List<Study> studies = dao.listByProjectId(9999L);
    assertEquals(0, studies.size());
  }

  @Test
  public void testListAllWithNegativeLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(-1L);
    assertEquals(4, studies.size());
  }

  @Test
  public void testListByLibraryIdNone() throws IOException {
    List<Study> studies = dao.listByLibraryId(9999L);
    assertEquals(0, studies.size());
  }

  @Test
  public void testListBySubmissionId() throws IOException {
    List<Study> studies = dao.listBySubmissionId(22L);
    assertEquals(0, studies.size());
  }

  @Test
  public void testGetStudyColumnSizes() throws Exception {
    Map<String, Integer> columnSizes = dao.getStudyColumnSizes();
    assertThat("Column size contains", columnSizes, hasEntry("name", 255));
  }

  @Test
  public void testListAllStudyTypes() throws Exception {
    assertThat(dao.listAllStudyTypes(), hasItem("Cancer Genomics"));
  }

  @Test
  public void testGetByExperimentId() throws Exception {
    assertThat(dao.getByExperimentId(9).getName(), is("STU1"));
  }

  @Test
  public void testGetByExperimentIdNull() throws Exception {
    assertNull(dao.getByExperimentId(9999));
  }

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }

  @Test
  public void testRemove() throws Exception {
    Study study = dao.get(1);
    assertNotNull(study);

    dao.setCascadeType(CascadeType.ALL);

    CacheManager cacheManager = Mockito.mock(CacheManager.class);
    Mockito.when(cacheManager.getCache(Matchers.anyString())).thenReturn(null);
    dao.setCacheManager(cacheManager);

    assertTrue(dao.remove(study));
    assertNull(dao.get(1L));
    assertThat(dao.count(), is(3));
  }

  @Test
  public void testGetByStudyType() throws Exception {
    List<Study> studies = dao.getByStudyType(1L);
    assertThat("Number of studies of type 'Other'.", studies.size(), is(4));
  }

  private Study makeStudy() {
    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    User user = Mockito.mock(User.class);
    Mockito.when(user.getUserId()).thenReturn(1L);
    Project project = Mockito.mock(Project.class);
    Mockito.when(project.getProjectId()).thenReturn(1L);
    Study s = new StudyImpl(project, user);
    s.setSecurityProfile(profile);
    s.setProject(project);
    s.setLastModifier(user);
    return s;
  }

}
