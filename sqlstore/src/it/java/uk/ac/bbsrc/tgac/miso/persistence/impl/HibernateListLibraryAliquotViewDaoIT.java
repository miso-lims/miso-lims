package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateListLibraryAliquotViewDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateListLibraryAliquotViewDao sut;

  @Before
  public void setup() {
    sut = new HibernateListLibraryAliquotViewDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testSearchByFreezer() throws Exception {
    testSearch(PaginationFilter.freezer("freezer1"));
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

  @Test
  public void testSearchByProject() throws Exception {
    testSearch(PaginationFilter.project(1L));
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
