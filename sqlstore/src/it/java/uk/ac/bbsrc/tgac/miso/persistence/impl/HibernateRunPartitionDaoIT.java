package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class HibernateRunPartitionDaoIT extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private HibernateRunPartitionDao dao;

  @Autowired
  SessionFactory sessionFactory;

  @Before
  public void setup() throws IOException, MisoNamingException {
    dao = new HibernateRunPartitionDao();
    dao.setSessionFactory(sessionFactory);
  }

  @Test
  public void testCreate() throws Exception {
    PartitionQCType type = (PartitionQCType) sessionFactory.getCurrentSession().get(PartitionQCType.class, 1L);
    Run run = (Run) sessionFactory.getCurrentSession().get(Run.class, 2L);
    Partition partition = (Partition) sessionFactory.getCurrentSession().get(PartitionImpl.class, 2L);
    RunPartition qc = new RunPartition();
    qc.setRun(run);
    qc.setPartition(partition);
    qc.setQcType(type);
    dao.create(qc);

    RunPartition fetchedQc = dao.get(run.getId(), partition.getId());
    assertNotNull(fetchedQc);
    assertEquals(qc.getQcType().getId(), fetchedQc.getQcType().getId());
    assertEquals(qc.getNotes(), fetchedQc.getNotes());
  }

  @Test
  public void testGet() throws Exception {
    Run run = (Run) sessionFactory.getCurrentSession().get(Run.class, 1L);
    assertNotNull(run);
    Partition partition = (Partition) sessionFactory.getCurrentSession().get(PartitionImpl.class, 1L);
    assertNotNull(partition);
    RunPartition qc = dao.get(run.getId(), partition.getId());
    assertNotNull(qc);
    assertEquals(1L, qc.getQcType().getId());
    assertEquals("it is written", qc.getNotes());
  }

  @Test
  public void testUpdate() throws Exception {
    Run run = (Run) sessionFactory.getCurrentSession().get(Run.class, 1L);
    Partition partition = (Partition) sessionFactory.getCurrentSession().get(PartitionImpl.class, 1L);
    RunPartition qc = dao.get(run.getId(), partition.getId());
    assertNotNull(qc);
    qc.setNotes("change is inevitable");
    dao.update(qc);

    RunPartition fetchedQc = dao.get(run.getId(), partition.getId());
    assertNotNull(fetchedQc);
    assertEquals(qc.getQcType().getId(), fetchedQc.getQcType().getId());
    assertEquals(qc.getNotes(), fetchedQc.getNotes());
  }

  @Test
  public void testDeleteForRun() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    List<RunPartition> before = getByRun(run);
    assertEquals(8, before.size());
    dao.deleteForRun(run);

    clearSession();

    List<RunPartition> after = getByRun(run);
    assertEquals(0, after.size());
  }

  private List<RunPartition> getByRun(Run run) {
    @SuppressWarnings("unchecked")
    List<RunPartition> results = currentSession().createCriteria(RunPartition.class)
        .add(Restrictions.eq("run", run))
        .list();
    return results;
  }

  @Test
  public void testDeleteForRunContainer() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 2L);
    SequencerPartitionContainer container = (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, 2L);
    List<RunPartition> before = getByRunAndContainer(run, container);
    assertEquals(8, before.size());
    dao.deleteForRunContainer(run, container);

    clearSession();

    List<RunPartition> after = getByRunAndContainer(run, container);
    assertEquals(0, after.size());
  }

  private List<RunPartition> getByRunAndContainer(Run run, SequencerPartitionContainer container) {
    @SuppressWarnings("unchecked")
    List<RunPartition> results = currentSession().createCriteria(RunPartition.class)
        .createAlias("partition", "partition")
        .add(Restrictions.eq("run", run))
        .add(Restrictions.eq("partition.sequencerPartitionContainer", container))
        .list();
    return results;
  }

}