package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.hasItem;

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
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStudyDao;

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
  private NamingScheme namingScheme;

  @InjectMocks
  private HibernateStudyDao dao;

  // Auto-increment sequence doesn't roll back with transactions, so must be tracked
  private static long nextAutoIncrementId = 5L;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
  }

  @Test
  public void testSaveNull() throws IOException, MisoNamingException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    long autoIncrementId = nextAutoIncrementId;
    Study newStudy = makeStudy();
    mockAutoIncrement(autoIncrementId);
    Mockito.when(namingScheme.validateName(Matchers.anyString())).thenReturn(ValidationResult.success());

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
  public void testGetStudyColumnSizes() throws Exception {
    Map<String, Integer> columnSizes = dao.getStudyColumnSizes();
    assertThat("Column size contains", columnSizes, hasEntry("name", 255));
  }

  @Test
  public void testListAllStudyTypes() throws Exception {
    List<String> names = new ArrayList<>();
    for (StudyType type : dao.listAllStudyTypes()) {
      names.add(type.getName());
    }
    assertThat(names, hasItem("Cancer Genomics"));
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

    assertTrue(dao.remove(study));
    assertNull(dao.get(1L));
    assertThat(dao.count(), is(3));
  }

  private Study makeStudy() {
    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    User user = Mockito.mock(UserImpl.class);
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
