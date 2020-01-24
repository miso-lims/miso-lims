package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isDetailedSample;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernatePoolableElementViewDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernatePoolableElementViewDao sut;

  @Before
  public void setup() {
    sut = new HibernatePoolableElementViewDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testLoadSampleRelative() throws Exception {
    // There was a problem with Hibernate lazy-loading Sample's inheritance structure (DetailedSample, etc.).
    // This is to prevent regression
    PoolableElementView ldi = sut.get(15L);
    assertNotNull(ldi);
    Sample sam = ldi.getSample();
    assertNotNull(sam);
    assertEquals(19L, sam.getId());
    assertTrue(isDetailedSample(sam));
  }

  @Test
  public void testSearchByFreezer() throws Exception {
    testSearch(PaginationFilter.freezer("freezer1"));
  }

  @Test
  public void testSearchByDistributed() throws Exception {
    testSearch(PaginationFilter.distributed());
  }

  @Test
  public void testSearchByDistributionDate() throws Exception {
    testSearch(PaginationFilter.date(LimsUtils.parseDate("2019-01-01"), LimsUtils.parseDate("2020-01-01"), DateType.DISTRIBUTED));
  }

  @Test
  public void testSearchByDistributionRecipient() throws Exception {
    testSearch(PaginationFilter.distributedTo("far away"));
  }

  @Test
  public void testSearchByTissueOrigin() throws Exception {
    testSearch(PaginationFilter.tissueOrigin("Ly"));
  }

  @Test
  public void testSearchByTissueType() throws Exception {
    testSearch(PaginationFilter.tissueType("P"));
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
    }, 0, 10, true, "name", filter));
  }

}
