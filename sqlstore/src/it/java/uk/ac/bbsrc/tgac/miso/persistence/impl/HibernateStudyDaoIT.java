package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateStudyDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateStudyDao dao;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testCreateNull() throws IOException, MisoNamingException {
    exception.expect(IllegalArgumentException.class);
    dao.create(null);
  }

  @Test
  public void testUpdateNull() throws IOException, MisoNamingException {
    exception.expect(IllegalArgumentException.class);
    dao.update(null);
  }

  @Test
  public void testSaveNew() throws IOException, MisoNamingException {
    Study newStudy = makeStudy();
    long id = dao.create(newStudy);

    Study savedRun = dao.get(id);
    assertEquals(newStudy.getAlias(), savedRun.getAlias());
  }

  @Test
  public void testList() throws IOException {
    List<Study> studies = dao.list();
    assertEquals(6, studies.size());
  }

  @Test
  public void testListAllWithLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(2L);
    assertEquals(2, studies.size());
  }

  @Test
  public void testListAllWithBiggerLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(50L);
    assertEquals(6, studies.size());
  }

  @Test
  public void testListAllWithZeroLimit() throws IOException {
    List<Study> studies = dao.listAllWithLimit(0L);
    assertEquals(0, studies.size());
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

  private Study makeStudy() {
    User user = new UserImpl();
    user.setId(1L);
    Project project = new ProjectImpl();
    project.setId(1L);
    StudyType studyType = new StudyType();
    studyType.setId(1L);
    Study s = new StudyImpl();
    s.setName("STU999");
    s.setStudyType(studyType);
    s.setDescription("foo");
    s.setProject(project);
    s.setChangeDetails(user);
    return s;
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("STU1"));
  }

  @Test
  public void testSearchByModifier() throws IOException {
    testSearch(PaginationFilter.user("admin", false));
  }

  @Test
  public void testGetUsage() throws Exception {
    Study study = (Study) currentSession().get(StudyImpl.class, 1L);
    assertEquals(25L, dao.getUsage(study));
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
