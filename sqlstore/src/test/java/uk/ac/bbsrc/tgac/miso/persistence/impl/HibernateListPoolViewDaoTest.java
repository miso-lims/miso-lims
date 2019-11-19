package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateListPoolViewDaoTest extends AbstractDAOTest {

  private HibernateListPoolViewDao sut;

  @Before
  public void setup() {
    sut = new HibernateListPoolViewDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testSearchByFreezer() throws Exception {
    testSearch(PaginationFilter.freezer("freezer1"));
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
