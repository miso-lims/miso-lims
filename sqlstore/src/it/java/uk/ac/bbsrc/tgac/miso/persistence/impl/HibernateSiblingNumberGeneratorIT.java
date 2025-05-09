package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

public class HibernateSiblingNumberGeneratorIT extends AbstractDAOTest {

  @PersistenceContext
  private EntityManager entityManager;

  private HibernateSiblingNumberGenerator sut;

  @Before
  public void setup() {
    sut = new HibernateSiblingNumberGenerator();
    sut.setEntityManager(entityManager);
  }

  @Test
  public void getNextSiblingNumberTest() throws Exception {
    String partialAlias = "TEST_0001_TISSUE_";
    DetailedSample s1 = (DetailedSample) entityManager.unwrap(Session.class).get(SampleImpl.class, 16L);
    DetailedSample s2 = (DetailedSample) entityManager.unwrap(Session.class).get(SampleImpl.class, 17L);
    assertTrue(s1.getAlias().startsWith(partialAlias));
    assertEquals(Integer.valueOf(1), s1.getSiblingNumber());
    assertTrue(s2.getAlias().startsWith(partialAlias));
    assertEquals(Integer.valueOf(2), s2.getSiblingNumber());
    assertEquals(5, sut.getNextSiblingNumber(SampleImpl.class, partialAlias));
  }

  @Test
  public void getFirstAvailableSiblingNumberTest() throws Exception {
    String partialAlias = "TEST_0001_TISSUE_";
    DetailedSample s1 = (DetailedSample) entityManager.unwrap(Session.class).get(SampleImpl.class, 16L);
    DetailedSample s2 = (DetailedSample) entityManager.unwrap(Session.class).get(SampleImpl.class, 17L);
    assertTrue(s1.getAlias().startsWith(partialAlias));
    assertEquals(Integer.valueOf(1), s1.getSiblingNumber());
    assertTrue(s2.getAlias().startsWith(partialAlias));
    assertEquals(Integer.valueOf(2), s2.getSiblingNumber());
    assertEquals(3, sut.getFirstAvailableSiblingNumber(SampleImpl.class, partialAlias));
  }

  @Test
  public void testIgnoreNonSiblingPartialMatch() throws Exception {
    String partialAlias = "TEST_0001_TIS";
    DetailedSample s1 = (DetailedSample) entityManager.unwrap(Session.class).get(SampleImpl.class, 16L);
    DetailedSample s2 = (DetailedSample) entityManager.unwrap(Session.class).get(SampleImpl.class, 17L);
    assertTrue(s1.getAlias().startsWith(partialAlias));
    assertEquals(Integer.valueOf(1), s1.getSiblingNumber());
    assertTrue(s2.getAlias().startsWith(partialAlias));
    assertEquals(Integer.valueOf(2), s2.getSiblingNumber());
    assertEquals(1, sut.getFirstAvailableSiblingNumber(SampleImpl.class, partialAlias));
  }

  @Test
  public void getNextForFirstChildTest() throws Exception {
    String partialAlias = "only_child_";
    assertEquals(1, sut.getNextSiblingNumber(SampleImpl.class, partialAlias));
  }

  @Test
  public void getFirstForFirstChildTest() throws Exception {
    String partialAlias = "only_child_";
    assertEquals(1, sut.getFirstAvailableSiblingNumber(SampleImpl.class, partialAlias));
  }

}
