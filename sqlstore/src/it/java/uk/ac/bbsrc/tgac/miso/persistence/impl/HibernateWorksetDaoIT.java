package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class HibernateWorksetDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateWorksetDao sut;

  @Before
  public void setup() {
    sut = new HibernateWorksetDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testGet() {
    Workset workset = sut.get(1L);
    assertNotNull(workset);
    assertEquals(1L, workset.getId());
    assertEquals("test", workset.getAlias());
    assertEquals("test workset", workset.getDescription());
    assertNotNull(workset.getWorksetSamples());
    assertEquals(3, workset.getWorksetSamples().size());
  }

  @Test
  public void testCreate() {
    String alias = "New workset";
    long sampleId = 3L;
    Workset workset = new Workset();
    workset.setAlias(alias);
    WorksetSample sample = new WorksetSample();
    sample.setWorkset(workset);
    sample.setItem((Sample) currentSession().get(SampleImpl.class, sampleId));
    workset.getWorksetSamples().add(sample);
    workset.setChangeDetails((User) currentSession().get(UserImpl.class, 1L));
    long savedId = sut.save(workset);

    clearSession();

    Workset saved = (Workset) currentSession().get(Workset.class, savedId);
    assertNotNull(saved);
    assertEquals(alias, saved.getAlias());
    assertEquals(1, saved.getWorksetSamples().size());
    assertEquals(sampleId, saved.getWorksetSamples().iterator().next().getItem().getId());
  }

  @Test
  public void testSaveAttributes() {
    Workset workset = sut.get(1L);
    String alias = "changed";
    String desc = "new desc";
    workset.setAlias(alias);
    workset.setDescription(desc);
    sut.save(workset);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Workset saved = sut.get(1L);
    assertEquals(alias, saved.getAlias());
    assertEquals(desc, saved.getDescription());
  }

  @Test
  public void testSaveSampleAddition() {
    Workset workset = sut.get(1L);
    assertEquals(3, workset.getWorksetSamples().size());

    Sample sample = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 4L);
    WorksetSample addition = new WorksetSample();
    addition.setItem(sample);
    addition.setWorkset(workset);
    addition.setAddedTime(new Date());
    workset.getWorksetSamples().add(addition);
    sut.save(workset);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Workset saved = sut.get(1L);
    assertEquals(4, saved.getWorksetSamples().size());
  }

  @Test
  public void testSaveSampleRemoval() {
    Workset workset = sut.get(1L);
    WorksetSample removal = workset.getWorksetSamples().iterator().next();
    workset.getWorksetSamples().remove(removal);
    sut.save(workset);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Workset saved = sut.get(1L);
    assertEquals(2, saved.getWorksetSamples().size());
  }

  @Test
  public void testListBySample() throws Exception {
    List<Workset> results = sut.listBySample(1L);
    assertEquals(2, results.size());
  }

  @Test
  public void testListByLibrary() throws Exception {
    List<Workset> results = sut.listByLibrary(1L);
    assertEquals(1, results.size());
  }

  @Test
  public void testListByLibraryAliquot() throws Exception {
    List<Workset> results = sut.listByLibraryAliquot(1L);
    assertEquals(1, results.size());
  }

  @Test
  public void testGetByAlias() throws Exception {
    String alias = "test";
    Workset workset = sut.getByAlias(alias);
    assertNotNull(workset);
    assertEquals(alias, workset.getAlias());
  }

  @Test
  public void testGetSampleAddedTimes() throws Exception {
    Map<Long, Date> times = sut.getSampleAddedTimes(1L);
    assertEquals(2, times.size());
    Date expected = LimsUtils.parseDateTime("2021-03-08 13:57:00");
    assertEquals(expected, times.get(2L));
    assertEquals(expected, times.get(3L));
  }

  @Test
  public void testGetLibraryAddedTimes() throws Exception {
    Map<Long, Date> times = sut.getLibraryAddedTimes(2L);
    assertEquals(1, times.size());
    Date expected = LimsUtils.parseDateTime("2021-03-08 13:58:00");
    assertEquals(expected, times.get(1L));
  }

  @Test
  public void testGetLibraryAliquotAddedTimes() throws Exception {
    Map<Long, Date> times = sut.getLibraryAliquotAddedTimes(2L);
    assertEquals(1, times.size());
    Date expected = LimsUtils.parseDateTime("2021-03-08 13:59:00");
    assertEquals(expected, times.get(1L));
  }

}
