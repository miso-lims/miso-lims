package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.instrumentstatus.InstrumentStatusPosition;

public class HibernateInstrumentStatusDaoIT extends AbstractDAOTest {

  private HibernateInstrumentStatusDao sut;

  @Before
  public void setup() {
    sut = new HibernateInstrumentStatusDao();
    sut.setEntityManager(getEntityManager());
  }

  @Test
  public void testList() throws Exception {
    List<InstrumentStatus> list = sut.list();
    assertNotNull(list);
    assertEquals(3, list.size());

    InstrumentStatus status1 = find(list, InstrumentStatus::getId, 1L);
    assertNotNull(status1);
    assertEquals("SN7001179", status1.getName());
    assertNotNull(status1.getPositions());
    assertEquals(3, status1.getPositions().size());
    InstrumentStatusPosition pos1a = find(status1.getPositions(), InstrumentStatusPosition::getAlias, "A");
    assertNotNull(pos1a.getRun());
    assertEquals(2L, pos1a.getRun().getRunId());
    assertNull(pos1a.getOutOfServiceTime());
    InstrumentStatusPosition pos1b = find(status1.getPositions(), InstrumentStatusPosition::getAlias, "B");
    assertNotNull(pos1b.getRun());
    assertEquals(3L, pos1b.getRun().getRunId());
    assertNull(pos1b.getOutOfServiceTime());

    InstrumentStatus status2 = find(list, InstrumentStatus::getId, 2L);
    assertNotNull(status2);
    assertEquals("h1180", status2.getName());
    assertNotNull(status2.getPositions());
    assertEquals(3, status2.getPositions().size());
    InstrumentStatusPosition pos2a = find(status2.getPositions(), InstrumentStatusPosition::getAlias, "A");
    assertNotNull(pos2a);
    assertNotNull(pos2a.getRun());
    assertEquals(4L, pos2a.getRun().getRunId());
    assertNull(pos2a.getOutOfServiceTime());
    InstrumentStatusPosition pos2b = find(status2.getPositions(), InstrumentStatusPosition::getAlias, "B");
    assertNotNull(pos2b);
    assertNull(pos2b.getRun());
    assertNotNull(pos2b.getOutOfServiceTime());

    InstrumentStatus status4 = find(list, InstrumentStatus::getId, 4L);
    assertNotNull(status4);
    assertEquals("miseq1", status4.getName());
    assertNotNull(status4.getPositions());
    assertEquals(1, status4.getPositions().size());
    InstrumentStatusPosition pos4 = status4.getPositions().get(0);
    assertNull(pos4.getAlias());
    assertNull(pos4.getRun());
    assertNull(pos4.getOutOfServiceTime());
  }

  private <T, R> T find(Collection<T> collection, Function<T, R> getter, R value) {
    return collection.stream().filter(x -> value.equals(getter.apply(x))).findAny().orElse(null);
  }

}
