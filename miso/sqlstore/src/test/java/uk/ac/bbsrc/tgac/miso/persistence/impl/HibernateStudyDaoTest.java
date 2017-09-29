package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStudyDao;

public class HibernateStudyDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateStudyDao dao;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testSaveNull() throws IOException, MisoNamingException {
    exception.expect(NullPointerException.class);
    dao.save(null);
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    Study newStudy = makeStudy();
    long id = dao.save(newStudy);

    Study savedRun = dao.get(id);
    assertEquals(newStudy.getAlias(), savedRun.getAlias());
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
    assertTrue(studies.size() > 0);
  }

  @Test
  public void testListByProjectId() throws IOException {
    List<Study> studies = dao.listByProjectId(1L);
    assertTrue(studies.size() > 0);
  }

  @Test
  public void testListByProjectIdNone() throws IOException {
    List<Study> studies = dao.listByProjectId(9999L);
    assertEquals(0, studies.size());
  }

  @Test
  public void testListAllWithNegativeLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(-1L);
    assertTrue(studies.size() > 0);
  }

  @Test
  public void testGetStudyColumnSizes() throws Exception {
    Map<String, Integer> columnSizes = dao.getStudyColumnSizes();
    assertThat("Column size contains", columnSizes, hasEntry("name", 255));
  }

  @Test
  public void testListAllStudyTypes() throws Exception {
    boolean hasCancerGenomics = false;
    for (StudyType type : dao.listAllStudyTypes()) {
      if (type.getName().equals("Cancer Genomics")) {
        hasCancerGenomics = true;
        break;
      }
    }
    assertTrue(hasCancerGenomics);
  }

  @Test
  public void testRemove() throws Exception {
    int count = dao.count();
    Study study = dao.get(2);

    assertTrue(dao.remove(study));
    assertNull(dao.get(2));
    assertEquals(count - 1, dao.count());
  }

  private Study makeStudy() {
    SecurityProfile profile = Mockito.mock(SecurityProfile.class);
    User user = new UserImpl();
    user.setUserId(1L);
    Project project = new ProjectImpl();
    project.setProjectId(1L);
    StudyType studyType = new StudyType();
    studyType.setId(1L);
    Study s = new StudyImpl(project, user);
    s.setName("STU999");
    s.setStudyType(studyType);
    s.setDescription("foo");
    s.setSecurityProfile(profile);
    s.setProject(project);
    s.setLastModifier(user);
    return s;
  }

}
