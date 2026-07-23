package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;

public class HibernateReferenceGenomeDaoIT extends AbstractDAOTest {

  private HibernateReferenceGenomeDao dao;

  @BeforeEach
  public void setUp() throws Exception {
    dao = new HibernateReferenceGenomeDao();
    dao.setEntityManager(getEntityManager());
  }

  @Test
  public void testListAllReferenceGeonomesCountIsAtLeastThree() throws Exception {
    // Three ReferenceGenomes are present in the test database.
    Collection<ReferenceGenome> referenceGenomes = dao.list();
    assertTrue(referenceGenomes.size() >= 3, "count of all references");
  }

  @Test
  public void testListByIdList() throws Exception {
    testListByIdList(dao::listByIdList, Arrays.asList(1L, 2L));
  }

  @Test
  public void testListByIdListNone() throws Exception {
    testListByIdListNone(dao::listByIdList);
  }

  @Test
  public void testGetByIdTwoReturnsHumanReference() throws Exception {
    long idTwo = 2L; // Id 2 contains 'Human hg19' in the test database.
    ReferenceGenome actual = dao.get(idTwo);
    assertEquals("Human hg19", actual.getAlias(), "alias for reference with id 2");
  }

  @Test
  public void testGetNonExistentReferenceGenomeReturnsNull() throws Exception {
    long idTooLargeToExistInTestData = 999999999999999999L;
    ReferenceGenome actual = dao.get(idTooLargeToExistInTestData);
    assertNull(actual, "Non existent reference");
  }

  @Test
  public void testGetByAlias() throws IOException {
    String alias = "Human hg19";
    ReferenceGenome ref = dao.getByAlias(alias);
    assertNotNull(ref);
    assertEquals(alias, ref.getAlias());
  }

  @Test
  public void testCreate() throws IOException {
    String alias = "New Reference";
    ReferenceGenome ref = new ReferenceGenomeImpl();
    ref.setAlias(alias);
    long savedId = dao.create(ref);

    clearSession();

    ReferenceGenome saved =
        (ReferenceGenome) currentSession().find(ReferenceGenomeImpl.class, savedId);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testUpdate() throws IOException {
    long id = 1L;
    String alias = "New Alias";
    ReferenceGenome ref = (ReferenceGenome) currentSession().find(ReferenceGenomeImpl.class, id);
    assertNotEquals(alias, ref.getAlias());
    ref.setAlias(alias);
    dao.update(ref);

    clearSession();

    ReferenceGenome saved =
        (ReferenceGenome) currentSession().find(ReferenceGenomeImpl.class, id);
    assertEquals(alias, saved.getAlias());
  }

  @Test
  public void testGetUsage() throws IOException {
    ReferenceGenome ref = (ReferenceGenome) currentSession().find(ReferenceGenomeImpl.class, 1L);
    assertEquals("Human hg19 random", ref.getAlias());
    assertEquals(2L, dao.getUsage(ref));
  }

}
