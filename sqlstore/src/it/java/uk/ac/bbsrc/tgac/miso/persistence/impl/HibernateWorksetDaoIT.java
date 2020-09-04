package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample;

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

}
