package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListWorksetView;

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

}
