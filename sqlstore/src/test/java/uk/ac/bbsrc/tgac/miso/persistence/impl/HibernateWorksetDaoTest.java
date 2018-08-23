package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateWorksetDaoTest extends AbstractDAOTest {

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
  public void testSearchByCreator() throws IOException {
    testSearch(PaginationFilter.user("admin", true));
  }

  @Test
  public void testSearchByModifier() throws IOException {
    testSearch(PaginationFilter.user("admin", false));
  }

  /**
   * Verifies Hibernate mappings by ensuring that no exception is thrown by a search
   * 
   * @param filter the search filter
   * @throws IOException
   */
  private void testSearch(PaginationFilter filter) throws IOException {
    // verify Hibernate mappings by ensuring that no exception is thrown
    assertNotNull(sut.list(err -> {
      throw new RuntimeException(err);
    }, 0, 10, true, "id", filter));
  }

}
