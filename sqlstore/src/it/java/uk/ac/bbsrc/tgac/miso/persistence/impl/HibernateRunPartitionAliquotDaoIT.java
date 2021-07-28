package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

public class HibernateRunPartitionAliquotDaoIT extends AbstractDAOTest {

  private HibernateRunPartitionAliquotDao sut;

  @Before
  public void setup() {
    sut = new HibernateRunPartitionAliquotDao();
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGet() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    Partition partition = (Partition) currentSession().get(PartitionImpl.class, 1L);
    LibraryAliquot aliquot = (LibraryAliquot) currentSession().get(LibraryAliquot.class, 2L);
    RunPartitionAliquot rpa = sut.get(run, partition, aliquot);
    assertNotNull(rpa);
    assertEquals(1L, rpa.getRun().getId());
    assertEquals(1L, rpa.getPartition().getId());
    assertEquals(2L, rpa.getAliquot().getId());
  }

  @Test
  public void testListByRunId() throws Exception {
    List<RunPartitionAliquot> results = sut.listByRunId(1L);
    assertNotNull(results);
    assertEquals(2, results.size());
    for (RunPartitionAliquot result : results) {
      assertEquals(1L, result.getRun().getId());
    }
  }

  @Test
  public void testListByAliquotId() throws Exception {
    List<RunPartitionAliquot> results = sut.listByAliquotId(2L);
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(2L, results.get(0).getAliquot().getId());
  }

  @Test
  public void testListByLibraryId() throws Exception {
    List<RunPartitionAliquot> results = sut.listByLibraryId(2L);
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(2L, results.get(0).getAliquot().getLibrary().getId());
  }

  @Test
  public void testCreate() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    Partition partition = (Partition) currentSession().get(PartitionImpl.class, 2L);
    LibraryAliquot aliquot = (LibraryAliquot) currentSession().get(LibraryAliquot.class, 3L);
    RunPartitionAliquotId id = new RunPartitionAliquotId(run, partition, aliquot);
    assertNull(currentSession().get(RunPartitionAliquot.class, id));

    RunPartitionAliquot rpa = new RunPartitionAliquot(run, partition, aliquot);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    rpa.setLastModifier(user);
    RunLibraryQcStatus qc = (RunLibraryQcStatus) currentSession().get(RunLibraryQcStatus.class, 1L);
    rpa.setQcStatus(qc);
    rpa.setQcUser(user);
    rpa.setQcDate(new Date());
    sut.create(rpa);

    clearSession();

    RunPartitionAliquot saved = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    assertNotNull(saved);
    assertEquals(1L, saved.getRun().getId());
    assertEquals(2L, saved.getPartition().getId());
    assertEquals(3L, saved.getAliquot().getId());
  }

  @Test
  public void testUpdate() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    Partition partition = (Partition) currentSession().get(PartitionImpl.class, 1L);
    LibraryAliquot aliquot = (LibraryAliquot) currentSession().get(LibraryAliquot.class, 1L);
    RunPartitionAliquotId id = new RunPartitionAliquotId(run, partition, aliquot);
    RunPartitionAliquot rpa = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    RunPurpose purpose = (RunPurpose) currentSession().get(RunPurpose.class, 1L);
    assertNull(rpa.getPurpose());
    rpa.setPurpose(purpose);
    sut.update(rpa);

    clearSession();

    RunPartitionAliquot saved = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    assertNotNull(saved.getPurpose());
    assertEquals(purpose.getId(), saved.getPurpose().getId());
  }

  @Test
  public void testDeleteForRunContainer() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    SequencerPartitionContainer container = (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, 1L);
    assertEquals(2, countForRunAndContainer(run, container));

    sut.deleteForRunContainer(run, container);

    clearSession();

    assertEquals(0, countForRunAndContainer(run, container));
  }

  private long countForRunAndContainer(Run run, SequencerPartitionContainer container) {
    return (long) currentSession().createCriteria(RunPartitionAliquot.class)
        .createAlias("partition", "partition")
        .add(Restrictions.eq("run", run))
        .add(Restrictions.eq("partition.sequencerPartitionContainer", container))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  @Test
  public void testDeleteForPoolAliquot() throws Exception {
    Pool pool = (Pool) currentSession().get(PoolImpl.class, 1L);
    LibraryAliquot aliquot = (LibraryAliquot) currentSession().get(LibraryAliquot.class, 2L);
    assertEquals(1, countForPoolAliquot(pool, aliquot));

    sut.deleteForPoolAliquot(pool, aliquot.getId());

    clearSession();

    assertEquals(0, countForPoolAliquot(pool, aliquot));
  }

  private long countForPoolAliquot(Pool pool, LibraryAliquot aliquot) {
    return (long) currentSession().createCriteria(RunPartitionAliquot.class)
        .createAlias("partition", "partition")
        .add(Restrictions.eq("partition.pool", pool))
        .add(Restrictions.eq("aliquot", aliquot))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

}
