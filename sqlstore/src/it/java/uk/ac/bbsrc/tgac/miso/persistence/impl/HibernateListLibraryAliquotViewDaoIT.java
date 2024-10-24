package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;

public class HibernateListLibraryAliquotViewDaoIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateListLibraryAliquotViewDao sut;

  @Before
  public void setup() {
    sut = new HibernateListLibraryAliquotViewDao();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void testGet() throws Exception {
    long id = 12L;
    ListLibraryAliquotView aliquot = sut.get(id);
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
