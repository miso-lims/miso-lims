package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition_;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class HibernateRunPartitionDaoIT extends AbstractDAOTest {

  private HibernateRunPartitionDao dao;

  @PersistenceContext
  EntityManager entityManager;

  @BeforeEach
  public void setup() throws IOException, MisoNamingException {
    dao = new HibernateRunPartitionDao();
    dao.setEntityManager(entityManager);
  }

  @Test
  public void testCreate() throws Exception {
    long runId = 2L;
    long partitionId = 2L;
    PartitionQCType type = (PartitionQCType) currentSession().find(PartitionQCType.class, 1L);
    RunPurpose purpose = (RunPurpose) currentSession().find(RunPurpose.class, 1L);
    RunPartition qc = new RunPartition();
    User user = (User) currentSession().find(UserImpl.class, 1L);
    qc.setRunId(runId);
    qc.setPartitionId(partitionId);
    qc.setQcType(type);
    qc.setPurpose(purpose);
    qc.setLastModifier(user);
    dao.create(qc);

    RunPartition fetchedQc = dao.get(runId, partitionId);
    assertNotNull(fetchedQc);
    assertEquals(qc.getQcType().getId(), fetchedQc.getQcType().getId());
    assertEquals(qc.getNotes(), fetchedQc.getNotes());
  }

  @Test
  public void testGet() throws Exception {
    Run run = (Run) currentSession().find(Run.class, 1L);
    assertNotNull(run);
    Partition partition = (Partition) currentSession().find(PartitionImpl.class, 1L);
    assertNotNull(partition);
    RunPartition qc = dao.get(run.getId(), partition.getId());
    assertNotNull(qc);
    assertEquals(1L, qc.getQcType().getId());
    assertEquals("it is written", qc.getNotes());
  }

  @Test
  public void testUpdate() throws Exception {
    Run run = (Run) currentSession().find(Run.class, 1L);
    Partition partition = (Partition) currentSession().find(PartitionImpl.class, 1L);
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
    Run run = (Run) currentSession().find(Run.class, 1L);
    List<RunPartition> before = getByRunId(run.getId());
    assertEquals(8, before.size());
    dao.deleteForRun(run);

    clearSession();

    List<RunPartition> after = getByRunId(run.getId());
    assertEquals(0, after.size());
  }

  private List<RunPartition> getByRunId(long runId) {
    QueryBuilder<RunPartition, RunPartition> builder =
        new QueryBuilder<>(currentSession(), RunPartition.class, RunPartition.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartition_.runId), runId));
    return builder.getResultList();
  }

  @Test
  public void testDeleteForRunContainer() throws Exception {
    Run run = (Run) currentSession().find(Run.class, 2L);
    SequencerPartitionContainer container =
        (SequencerPartitionContainer) currentSession().find(SequencerPartitionContainerImpl.class, 2L);
    List<RunPartition> before = getByRunAndContainer(run, container);
    assertEquals(8, before.size());
    dao.deleteForRunContainer(run, container);

    clearSession();

    List<RunPartition> after = getByRunAndContainer(run, container);
    assertEquals(0, after.size());
  }

  private List<RunPartition> getByRunAndContainer(Run run, SequencerPartitionContainer container) {
    Set<Long> partitionIds = container.getPartitions().stream().map(Partition::getId).collect(Collectors.toSet());
    QueryBuilder<RunPartition, RunPartition> builder =
        new QueryBuilder<>(currentSession(), RunPartition.class, RunPartition.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartition_.runId), run.getId()));
    builder.addInPredicate(builder.getRoot().get(RunPartition_.partitionId), partitionIds);
    return builder.getResultList();
  }

}
