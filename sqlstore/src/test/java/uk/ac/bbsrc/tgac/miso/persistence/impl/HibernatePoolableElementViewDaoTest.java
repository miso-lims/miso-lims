package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isDetailedSample;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;

public class HibernatePoolableElementViewDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;

  private HibernatePoolableElementViewDao sut;

  @Before
  public void setup() {
    sut = new HibernatePoolableElementViewDao();
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void testLoadSampleRelative() throws Exception {
    // There was a problem with Hibernate lazy-loading Sample's inheritance structure (DetailedSample, etc.).
    // This is to prevent regression
    PoolableElementView ldi = sut.get(15L);
    assertNotNull(ldi);
    Sample sam = ldi.getSample();
    assertNotNull(sam);
    assertEquals(19L, sam.getId());
    assertTrue(isDetailedSample(sam));
  }

}
