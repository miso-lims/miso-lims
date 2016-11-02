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
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateReferenceGenomeDao;

public class SQLReferenceGenomeDAOTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  @InjectMocks
  private HibernateReferenceGenomeDao dao;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testListAllReferenceGeonomesCountIsAtLeastThree() {
    // Three ReferenceGenomes are present in the test database.
    Collection<ReferenceGenome> referenceGenomes = dao.listAllReferenceGenomeTypes();
    assertThat("count of all references", referenceGenomes.size(), is(greaterThanOrEqualTo(3)));
  }

  @Test
  public void testGetByIdTwoReturnsHumanReference() throws Exception {
    Long idTwo = 2L; // Id 2 contains 'Human hg19' in the test database.
    ReferenceGenome actual = dao.getReferenceGenome(idTwo);
    assertThat("alias for reference with id 2", actual.getAlias(), is("Human hg19"));
  }

  @Test
  public void testGetNonExistentReferenceGenomeReturnsNull() throws Exception {
    Long idTooLargeToExistInTestData = 999999999999999999L;
    ReferenceGenome actual = dao.getReferenceGenome(idTooLargeToExistInTestData);
    assertThat("Non exisitent reference", actual, is(nullValue()));
  }

}
