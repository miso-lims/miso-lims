/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

/**
 * @author Chris Salt
 *
 */
public class HibernateExperimentDaoIT extends AbstractDAOTest {

  private HibernateExperimentDao dao;

  @Before
  public void setup() throws Exception {
    dao = new HibernateExperimentDao();
    dao.setEntityManager(getEntityManager());
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#save(uk.ac.bbsrc.tgac.miso.core.data.Experiment)}
   * .
   * 
   * @throws IOException
   * @throws MisoNamingException
   */
  @Test
  public void testCreate() throws IOException, MisoNamingException {
    String name = "TEMPORARY_XXX";
    Experiment experiment = new Experiment();
    experiment.setName(name);
    InstrumentModel model = (InstrumentModel) currentSession().get(InstrumentModel.class, 16L);
    experiment.setInstrumentModel(model);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    user.setId(1L);

    experiment.setChangeDetails(user);
    experiment.setTitle("Title");
    long savedId = dao.create(experiment);

    clearSession();

    Experiment saved = (Experiment) currentSession().get(Experiment.class, savedId);
    assertNotNull(saved);
    assertEquals(name, saved.getName());
  }

  @Test
  public void testUpdate() throws Exception {
    long id = 4L;
    String newAlias = "New Alias";
    Experiment original = (Experiment) currentSession().get(Experiment.class, id);
    assertNotEquals(newAlias, original.getAlias());
    original.setAlias(newAlias);
    dao.update(original);

    clearSession();

    Experiment saved = (Experiment) currentSession().get(Experiment.class, id);
    assertEquals(newAlias, saved.getAlias());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao#listAll()}.
   */
  @Test
  public void testList() throws IOException {
    List<Experiment> experiments = dao.list();
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

  @Test
  public void testGetUsage() throws Exception {
    Experiment experiment = (Experiment) currentSession().get(Experiment.class, 1L);
    assertEquals(2L, dao.getUsage(experiment));
  }

}
