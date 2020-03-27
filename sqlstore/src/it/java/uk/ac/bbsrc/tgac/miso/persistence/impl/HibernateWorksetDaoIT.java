package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

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
    assertNotNull(workset.getSamples());
    assertEquals(3, workset.getSamples().size());
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
    Sample addition = (Sample) sessionFactory.getCurrentSession().get(SampleImpl.class, 4L);
    assertEquals(3, workset.getSamples().size());
    workset.getSamples().add(addition);
    sut.save(workset);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Workset saved = sut.get(1L);
    assertEquals(4, saved.getSamples().size());
  }

  @Test
  public void testSaveSampleRemoval() {
    Workset workset = sut.get(1L);
    Sample removal = workset.getSamples().iterator().next();
    workset.getSamples().remove(removal);
    sut.save(workset);

    sessionFactory.getCurrentSession().flush();
    sessionFactory.getCurrentSession().clear();

    Workset saved = sut.get(1L);
    assertEquals(2, saved.getSamples().size());
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
