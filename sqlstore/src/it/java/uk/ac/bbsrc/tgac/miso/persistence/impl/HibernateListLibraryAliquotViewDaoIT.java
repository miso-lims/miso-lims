package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibaryAliquotView;

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
  public void testGet() throws Exception {
    long id = 12L;
    ListLibaryAliquotView aliquot = sut.get(id);
    assertNotNull(aliquot);
    assertEquals(id, aliquot.getId());
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(sut::listByIdList, Arrays.asList(3L, 4L, 5L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(sut::listByIdList);
  }

}
