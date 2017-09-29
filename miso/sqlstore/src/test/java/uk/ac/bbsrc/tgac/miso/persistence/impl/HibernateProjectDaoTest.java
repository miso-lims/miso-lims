/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * @author Chris Salt
 *
 */
public class HibernateProjectDaoTest extends AbstractDAOTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private SecurityStore securityStore;

  @InjectMocks
  private HibernateProjectDao projectDAO;

  // a project to save
  private final Project project = new ProjectImpl();

  private final Group group = new Group();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    projectDAO.setJdbcTemplate(jdbcTemplate);
    projectDAO.setSessionFactory(sessionFactory);
    projectDAO.setSecurityStore(securityStore);

    project.setProgress(ProgressType.ACTIVE);
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setId(1L);
    referenceGenome.setAlias("hg19");
    project.setReferenceGenome(referenceGenome);
    project.setLastUpdated(new Date());

    when(securityStore.getGroupByName("ProjectWatchers")).thenReturn(group);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#save(uk.ac.bbsrc.tgac.miso.core.data.Project)} .
   */
  @Test
  public void testSave() throws Exception {
    final String testAlias = "test alias";
    project.setAlias(testAlias);

    long savedProjectId = projectDAO.save(project);

    Project savedProject = projectDAO.get(savedProjectId);
    assertEquals(testAlias, savedProject.getAlias());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#save(uk.ac.bbsrc.tgac.miso.core.data.Project)} .
   */

  @Test
  public void testSaveWithUnsavedSecurityProfile() throws Exception {
    project.getSecurityProfile().setProfileId(SecurityProfile.UNSAVED_ID);
    final String testAlias = "test alias";
    project.setAlias(testAlias);

    long savedProjectId = projectDAO.save(project);

    Project savedProject = projectDAO.get(savedProjectId);
    assertEquals(testAlias, savedProject.getAlias());
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#saveOverview(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview)}
   * .
   * 
   * @throws IOException
   */
  @Ignore
  @Test
  public void testSaveOverview() throws IOException {
    // TODO: implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#listAll()}.
   */
  @Test
  public void testListAll() throws Exception {
    List<Project> projects = projectDAO.listAll();
    System.out.println(projects);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#listAllWithLimit(long)} .
   * 
   * @throws IOException
   */
  @Test
  public void testListAllWithLimit() throws IOException {
    List<Project> projects = projectDAO.listAllWithLimit(2L);
    assertEquals(2, projects.size());

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#count()}.
   * 
   * @throws IOException
   */
  @Test
  public void testCount() throws IOException {
    int count = projectDAO.count();
    assertEquals(3, count);
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#removeOverview(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview)}
   * .
   */
  @Ignore
  @Test
  public void testRemoveOverview() {
    // TODO: Uses cache so ignoring test for now.
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#get(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGet() throws IOException {
    Project p = projectDAO.get(1);
    assertNotNull(p);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#listBySearch(java.lang.String)} .
   */
  @Ignore
  @Test
  public void testListBySearch() {
    // TODO: Delete this method.
    // It allows you to pass in hard mysql query string to
    // return a project. I have deprecated it.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#getByAlias(java.lang.String)} .
   * 
   * @throws IOException
   */
  @Test
  public void testGetByAlias() throws IOException {
    String alias = projectDAO.listAll().get(1).getAlias();
    assertFalse(LimsUtils.isStringEmptyOrNull(alias));
    Project p = projectDAO.getByAlias(alias);
    assertNotNull(p);
    assertEquals(alias, p.getAlias());
  }

  @Test
  public void testGetByShortName() throws IOException {
    String expected = "TEST1";
    Project p = projectDAO.getByShortName(expected);
    assertNotNull(p);
    assertEquals(expected, p.getShortName());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#getByStudyId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGetByStudyId() throws IOException {
    Project p = projectDAO.getByStudyId(1L);
    assertNotNull(p);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#getProjectOverviewById(long)} .
   * 
   * @throws IOException
   */
  @Test
  public void testGetProjectOverviewById() throws IOException {
    ProjectOverview po = projectDAO.getProjectOverviewById(1L);
    assertNotNull(po);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#lazyGetProjectOverviewById(long)} .
   */
  @Test
  public void testLazyGetProjectOverviewById() {
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#listOverviewsByProjectId(long)} .
   */
  @Test
  public void testListOverviewsByProjectId() {
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#listIssueKeysByProjectId(long)} .
   */
  @Test
  public void testListIssueKeysByProjectId() {
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#getProjectColumnSizes()} .
   */
  @Test
  public void testGetProjectColumnSizes() {
    // TODO : Implement
  }

}
