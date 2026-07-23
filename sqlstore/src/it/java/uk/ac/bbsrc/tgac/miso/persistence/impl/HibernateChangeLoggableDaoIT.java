package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;

public class HibernateChangeLoggableDaoIT extends AbstractDAOTest {

  private HibernateChangeLoggableDao sut;

  @BeforeEach
  public void setup() {
    sut = new HibernateChangeLoggableDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testUpdate() {
    long worksetId = 1L;
    String newAlias = "Changed";
    Workset before = (Workset) currentSession().find(Workset.class, worksetId);
    assertNotNull(before);
    assertNotEquals(newAlias, before.getAlias());
    before.setAlias(newAlias);
    sut.update(before);

    clearSession();

    Workset after = (Workset) currentSession().find(Workset.class, worksetId);
    assertEquals(newAlias, after.getAlias());
  }

}
