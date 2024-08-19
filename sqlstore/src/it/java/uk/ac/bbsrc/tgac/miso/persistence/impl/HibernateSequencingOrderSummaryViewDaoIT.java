package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateSequencingOrderSummaryViewDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateSequencingOrderSummaryViewDao sut;

  @Before
  public void setup() {
    sut = new HibernateSequencingOrderSummaryViewDao();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void testSearch() throws IOException {
    testSearch(PaginationFilter.query("IPO1"));
  }

  @Test
  public void testSearchByFulfilled() throws IOException {
    testSearch(PaginationFilter.fulfilled(true));
  }

  @Test
  public void testSearchByPending() throws IOException {
    testSearch(PaginationFilter.pending());
  }

  @Test
  public void testSearchByPlatformType() throws IOException {
    testSearch(PaginationFilter.platformType(PlatformType.ILLUMINA));
  }

  @Test
  public void testSearchByPoolId() throws IOException {
    testSearch(PaginationFilter.pool(1L));
  }

  @Test
  public void testSearchByHealth() throws IOException {
    testSearch(PaginationFilter.health(EnumSet.of(HealthType.Running)));
  }

  @Test
  public void testSearchByIndex() throws IOException {
    testSearch(PaginationFilter.index("ACGTACGT"));
  }

  @Test
  public void testSearchByParametersId() throws IOException {
    testSearch(PaginationFilter.sequencingParameters(1L));
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
    }, 0, 10, true, "lastUpdated", filter));
  }

}
