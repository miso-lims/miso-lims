package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;

public class HibernateChangeLoggableDaoIT extends AbstractDAOTest {

  private HibernateChangeLoggableDao sut;

  @Before
  public void setup() {
    sut = new HibernateChangeLoggableDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testUpdate() {
    long worksetId = 1L;
    String newAlias = "Changed";
    Workset before = (Workset) currentSession().get(Workset.class, worksetId);
    assertNotNull(before);
    assertNotEquals(newAlias, before.getAlias());
    before.setAlias(newAlias);
    sut.update(before);

    clearSession();

    Workset after = (Workset) currentSession().get(Workset.class, worksetId);
    assertEquals(newAlias, after.getAlias());
  }

}
