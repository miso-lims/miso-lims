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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao#listAll()}.
   */
  @Test
  public void testListAll() throws Exception {
    List<Project> projects = projectDAO.listAll();
    assertEquals(3, projects.size());
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

  @Test
  public void testGetUsage() throws Exception {
    Project proj = (Project) currentSession().get(ProjectImpl.class, 1L);
    assertEquals(21L, projectDAO.getUsage(proj));
  }

}
