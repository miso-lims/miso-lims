package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQC;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class HibernatePartitionQcDaoTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @InjectMocks
  private HibernatePartitionQcDao dao;

  @Autowired
  SessionFactory sessionFactory;

  @Before
  public void setup() throws IOException, MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testCreate() throws Exception {
    PartitionQCType type = dao.listTypes().iterator().next();
    Run run = (Run) sessionFactory.getCurrentSession().get(Run.class, 2L);
    Partition partition = (Partition) sessionFactory.getCurrentSession().get(PartitionImpl.class, 2L);
    PartitionQC qc = new PartitionQC();
    qc.setRun(run);
    qc.setPartition(partition);
    qc.setType(type);
    dao.create(qc);

    PartitionQC fetchedQc = dao.get(run, partition);
    assertNotNull(fetchedQc);
    assertEquals(qc.getType().getId(), fetchedQc.getType().getId());
    assertEquals(qc.getNotes(), fetchedQc.getNotes());
  }

  @Test
  public void testGet() throws Exception {
    Run run = (Run) sessionFactory.getCurrentSession().get(Run.class, 1L);
    assertNotNull(run);
    Partition partition = (Partition) sessionFactory.getCurrentSession().get(PartitionImpl.class, 1L);
    assertNotNull(partition);
    PartitionQC qc = dao.get(run, partition);
    assertNotNull(qc);
    assertEquals(1L, qc.getType().getId());
    assertEquals("it is written", qc.getNotes());
  }

  @Test
  public void testGetNonExistent() throws Exception {
    Run run = (Run) sessionFactory.getCurrentSession().get(Run.class, 1L);
    Partition partition = (Partition) sessionFactory.getCurrentSession().get(PartitionImpl.class, 2L);
    PartitionQC qc = dao.get(run, partition);
    assertNull(qc);
  }

  @Test
  public void testListTypes() throws Exception {
    assertTrue(dao.listTypes().size() > 0);
  }

  @Test
  public void testUpdate() throws Exception {
    Run run = (Run) sessionFactory.getCurrentSession().get(Run.class, 1L);
    Partition partition = (Partition) sessionFactory.getCurrentSession().get(PartitionImpl.class, 1L);
    PartitionQC qc = dao.get(run, partition);
    assertNotNull(qc);
    qc.setNotes("change is inevitable");
    dao.update(qc);

    PartitionQC fetchedQc = dao.get(run, partition);
    assertNotNull(fetchedQc);
    assertEquals(qc.getType().getId(), fetchedQc.getType().getId());
    assertEquals(qc.getNotes(), fetchedQc.getNotes());

  }

}