/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;

/**
 * @author Chris Salt
 *
 */
public class HibernateProjectDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private SecurityStore securityStore;

  @InjectMocks
  private HibernateProjectDao projectDAO;

  // a project to save
  private final Project project = new ProjectImpl();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    projectDAO.setSessionFactory(sessionFactory);
    projectDAO.setSecurityStore(securityStore);

    project.setStatus(StatusType.ACTIVE);
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setId(1L);
    referenceGenome.setAlias("hg19");
    project.setReferenceGenome(referenceGenome);
    User user = new UserImpl();
    user.setId(1L);
    project.setCreator(user);
    project.setCreationTime(new Date());
    project.setLastModifier(user);
    project.setLastModified(new Date());
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#save(uk.ac.bbsrc.tgac.miso.core.data.Project)}
   * .
   */
  @Test
  public void testSave() throws Exception {
    Pipeline pipeline = (Pipeline) currentSession().get(Pipeline.class, 1L);
    final String testTitle = "test title";
    project.setTitle(testTitle);
    project.setPipeline(pipeline);

    long savedProjectId = projectDAO.save(project);

    Project savedProject = projectDAO.get(savedProjectId);
    assertEquals(testTitle, savedProject.getTitle());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#listAll()}.
   */
  @Test
  public void testListAll() throws Exception {
    List<Project> projects = projectDAO.listAll();
    assertEquals(3, projects.size());
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
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#getByTitle(java.lang.String)} .
   * 
   * @throws IOException
   */
  @Test
  public void testGetByTitle() throws IOException {
    String title = projectDAO.listAll().get(1).getTitle();
    assertFalse(LimsUtils.isStringEmptyOrNull(title));
    Project p = projectDAO.getByTitle(title);
    assertNotNull(p);
    assertEquals(title, p.getTitle());
  }

  @Test
  public void testGetByCode() throws IOException {
    String expected = "TEST1";
    Project p = projectDAO.getByCode(expected);
    assertNotNull(p);
    assertEquals(expected, p.getCode());
  }

  @Test
  public void testGetUsage() throws Exception {
    Project proj = (Project) currentSession().get(ProjectImpl.class, 1L);
    assertEquals(21L, projectDAO.getUsage(proj));
  }

}
