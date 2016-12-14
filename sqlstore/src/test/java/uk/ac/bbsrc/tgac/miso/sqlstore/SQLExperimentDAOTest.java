/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ExperimentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao;

/**
 * @author Chris Salt
 *
 */
public class SQLExperimentDAOTest extends AbstractDAOTest {

  private static long nextAutoIncrementId = 33L;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private HibernateExperimentDao dao;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    when(namingScheme.generateNameFor(Matchers.any(Experiment.class))).thenReturn("EDI123");
    when(namingScheme.validateName(Matchers.anyString())).thenReturn(ValidationResult.success());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#save(uk.ac.bbsrc.tgac.miso.core.data.Experiment)} .
   * 
   * @throws IOException
   */
  @Test
  public void testSave() throws IOException {
    Experiment experiment = new ExperimentImpl();
    experiment.setPlatform(new PlatformImpl());
    experiment.setStudy(new StudyImpl());
    User mockUser = Mockito.mock(User.class);
    when(mockUser.getUserId()).thenReturn(1L);

    experiment.setLastModifier(mockUser);
    long autoIncrementId = nextAutoIncrementId;
    mockAutoIncrement(autoIncrementId);

    dao.save(experiment);
    nextAutoIncrementId += 1;
  }

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#listAll()}.
   */
  @Test
  public void testListAll() {
    List<Experiment> experiments = dao.listAll();
    assertEquals(32, experiments.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#listAllWithLimit(long)} .
   * 
   * @throws IOException
   */
  @Test
  public void testListAllWithLimit() throws IOException {
    List<Experiment> experiments = dao.listAllWithLimit(3L);
    assertEquals(3L, experiments.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#count()}.
   * 
   * @throws IOException
   */
  @Test
  public void testCount() throws IOException {
    int count = dao.count();
    assertEquals(32, count);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#listBySearch(java.lang.String)} .
   */
  @Ignore
  @Test
  public void testListBySearch() {
    // This method isn't tested as I have deprecated it.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#listByStudyId(long)} .
   */
  @Test
  public void testListByStudyId() {
    List<Experiment> experiments = dao.listByStudyId(1L);
    assertEquals(32, experiments.size());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#get(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGet() throws IOException {
    Experiment experiment = dao.get(1L);
    assertNotNull(experiment);
    assertEquals("EXP1", experiment.getName());
    assertEquals("TEST", experiment.getDescription());
    assertEquals("PRO1 Illumina Other experiment (Auto-gen)", experiment.getTitle());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#lazyGet(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testLazyGet() throws IOException {
    Experiment experiment = dao.lazyGet(1L);
    assertNotNull(experiment);
    assertEquals("EXP1", experiment.getName());
    assertEquals("TEST", experiment.getDescription());
    assertEquals("PRO1 Illumina Other experiment (Auto-gen)", experiment.getTitle());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#remove(uk.ac.bbsrc.tgac.miso.core.data.Experiment)} .
   * 
   * @throws IOException
   */
  @Test
  public void testRemove() throws IOException {
    Experiment experiment = dao.lazyGet(1L);
    dao.remove(experiment);
    assertEquals(31, dao.listAll().size());
  }
}
