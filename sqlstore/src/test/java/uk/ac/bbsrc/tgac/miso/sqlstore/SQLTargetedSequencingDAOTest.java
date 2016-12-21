package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTargetedSequencingDao;

public class SQLTargetedSequencingDAOTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateTargetedSequencingDao dao;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAllCountIsAtLeastThree() throws Exception {
    Collection<TargetedSequencing> targetedSequencingList = dao.listAll();
    assertThat("count of all targeted sequencing items", targetedSequencingList.size(), is(greaterThanOrEqualTo(3)));
  }

  @Test
  public void testGetByIdOneAliasIsHalo() throws Exception {
    Long idOne = 1L;
    TargetedSequencing actual = dao.get(idOne);
    assertThat("alias for targeted sequencing with id 1", actual.getAlias(), is("HALO_IBP"));
  }

  @Test
  public void testCountIsAtLeastThree() throws Exception {
    int actual = dao.count();
    assertThat("count of targeted sequencing entries", actual, is(greaterThanOrEqualTo(3)));
  }
}
