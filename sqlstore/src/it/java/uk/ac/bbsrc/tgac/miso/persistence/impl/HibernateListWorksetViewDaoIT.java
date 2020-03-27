package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;

public class HibernateListWorksetViewDaoIT extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateListWorksetViewDao sut;

  @Before
  public void setup() {
    sut = new HibernateListWorksetViewDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListBySearch() throws Exception {
    List<ListWorksetView> results = sut.listBySearch("two");
    assertEquals(1, results.size());
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
