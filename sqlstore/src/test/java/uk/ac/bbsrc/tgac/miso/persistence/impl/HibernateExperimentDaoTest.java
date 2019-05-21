/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;

/**
 * @author Chris Salt
 *
 */
public class HibernateExperimentDaoTest extends AbstractDAOTest {


  @Autowired
  private SessionFactory sessionFactory;

  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private HibernateExperimentDao dao;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
    when(namingScheme.generateNameFor(Matchers.any(Experiment.class))).thenReturn("EDI123");
    when(namingScheme.validateName(Matchers.anyString())).thenReturn(ValidationResult.success());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#save(uk.ac.bbsrc.tgac.miso.core.data.Experiment)}
   * .
   * 
   * @throws IOException
   * @throws MisoNamingException
   */
  @Test
  public void testSave() throws IOException, MisoNamingException {
    Experiment experiment = new Experiment();
    experiment.setName("TEMPORARY_XXX");
    InstrumentModel model = (InstrumentModel) sessionFactory.getCurrentSession().get(InstrumentModel.class, 16L);
    experiment.setInstrumentModel(model);
    experiment.setStudy(new StudyImpl());
    User user = new UserImpl();
    user.setId(1L);

    experiment.setChangeDetails(user);
    experiment.setName(namingScheme.generateNameFor(experiment));
    experiment.setTitle("Title");
    dao.save(experiment);
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
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#listByStudyId(long)} .
   */
  @Test
  public void testListByStudyId() {
    List<Experiment> experiments = dao.listByStudyId(1L);
    assertEquals(25, experiments.size());
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

  @Test
  public void testlistByLibraryExists() throws IOException {
    assertEquals(4, dao.listByLibrary(10).size());
  }

  @Test
  public void testlistByLibraryMissing() throws IOException {
    assertEquals(0, dao.listByLibrary(1000).size());
  }

  @Test
  public void testlistByRunExists() throws IOException {
    assertEquals(2, dao.listByRun(1).size());
  }

  @Test
  public void testlistByRunMissing() throws IOException {
    assertEquals(0, dao.listByRun(2).size());
  }
}
