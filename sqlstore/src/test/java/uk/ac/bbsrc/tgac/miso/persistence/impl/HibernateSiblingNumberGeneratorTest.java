package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

public class HibernateSiblingNumberGeneratorTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernateSiblingNumberGenerator sut;

  @Before
  public void setup() {
    sut = new HibernateSiblingNumberGenerator();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void getNextSiblingNumberTest() throws Exception {
    String partialAlias = "TEST_0001_TISSUE_";
    DetailedSample s1 = (DetailedSample) sessionFactory.getCurrentSession().get(SampleImpl.class, 16L);
    DetailedSample s2 = (DetailedSample) sessionFactory.getCurrentSession().get(SampleImpl.class, 17L);
    assertTrue(s1.getAlias().startsWith(partialAlias));
    assertEquals(new Integer(1), s1.getSiblingNumber());
    assertTrue(s2.getAlias().startsWith(partialAlias));
    assertEquals(new Integer(2), s2.getSiblingNumber());
    assertEquals(3, sut.getNextSiblingNumber(SampleImpl.class, partialAlias));
  }

}
