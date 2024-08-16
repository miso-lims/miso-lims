package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.User;
import com.google.protobuf.BytesValue.Builder;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView_;
import uk.ac.bbsrc.tgac.miso.persistence.ListLibraryAliquotViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;

public class HibernateRunPartitionAliquotDaoIT extends AbstractDAOTest {

  @Mock
  private RunStore runStore;
  @Mock
  private ListLibraryAliquotViewDao listLibraryAliquotViewDao;

  @InjectMocks
  private HibernateRunPartitionAliquotDao sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    sut.setSessionFactory(getSessionFactory());
  }

  @Test
  public void testGetExisting() throws Exception {
    RunPartitionAliquot rpa = get(1L, 1L, 2L, true);
    assertNotNull(rpa);
    assertEquals(1L, rpa.getRun().getId());
    assertEquals(1L, rpa.getPartition().getId());
    assertEquals(2L, rpa.getAliquot().getId());
  }

  @Test
  public void testGetConstructed() throws Exception {
    RunPartitionAliquot rpa = get(1L, 2L, 3L, false);
    assertNotNull(rpa);
    assertEquals(1L, rpa.getRun().getId());
    assertEquals(2L, rpa.getPartition().getId());
    assertEquals(3L, rpa.getAliquot().getId());
  }

  @Test
  public void testGetInvalid() throws Exception {
    RunPartitionAliquot rpa = get(1L, 1L, 15L, false);
    assertNull(rpa);
  }

  private RunPartitionAliquot get(long runId, long partitionId, long aliquotId, boolean expectExisting)
      throws IOException {
    Run run = (Run) currentSession().get(Run.class, runId);
    Partition partition = (Partition) currentSession().get(PartitionImpl.class, partitionId);
    ListLibraryAliquotView aliquot =
        (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, aliquotId);

    RunPartitionAliquotId id = new RunPartitionAliquotId(run, partition, aliquot);
    RunPartitionAliquot existing = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    if (expectExisting) {
      assertNotNull(existing);
    } else {
      assertNull(existing);
    }

    return sut.get(run, partition, aliquot);
  }

  @Test
  public void testListByRunId() throws Exception {
    long runId = 1L;
    Run run = (Run) currentSession().get(Run.class, runId);
    Mockito.when(runStore.listByIdList(Collections.singleton(runId))).thenReturn(Collections.singletonList(run));
    // There are RunPartitionAliquots created for aliquots 1L and 2L. The dao must construct RPAs for
    // the following
    List<ListLibraryAliquotView> aliquots = Arrays.asList(
        (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, 3L),
        (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, 4L),
        (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, 5L),
        (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, 6L));
    Mockito.when(listLibraryAliquotViewDao.listByIdList(ArgumentMatchers.any())).thenReturn(aliquots);

    List<RunPartitionAliquot> results = sut.listByRunId(runId);
    assertNotNull(results);
    assertEquals(6, results.size());
    for (RunPartitionAliquot result : results) {
      assertEquals(runId, result.getRun().getId());
    }
    for (long i = 1L; i <= 6L; i++) {
      long finalI = i;
      assertTrue(results.stream().anyMatch(x -> x.getAliquot().getId() == finalI));
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
  public void testListByLibraryIdList() throws Exception {
    List<RunPartitionAliquot> results = sut.listByLibraryIdList(Arrays.asList(2L));
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals(2L, results.get(0).getAliquot().getParentLibrary().getId());
  }

  @Test
  public void testSaveCreate() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    Partition partition = (Partition) currentSession().get(PartitionImpl.class, 2L);
    ListLibraryAliquotView aliquot = (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, 3L);
    RunPartitionAliquotId id = new RunPartitionAliquotId(run, partition, aliquot);
    assertNull(currentSession().get(RunPartitionAliquot.class, id));

    RunPartitionAliquot rpa = new RunPartitionAliquot(run, partition, aliquot);
    User user = (User) currentSession().get(UserImpl.class, 1L);
    rpa.setLastModifier(user);
    RunLibraryQcStatus qc = (RunLibraryQcStatus) currentSession().get(RunLibraryQcStatus.class, 1L);
    rpa.setQcStatus(qc);
    rpa.setQcUser(user);
    rpa.setQcDate(LocalDate.now(ZoneId.systemDefault()));
    sut.save(rpa);

    clearSession();

    RunPartitionAliquot saved = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    assertNotNull(saved);
    assertEquals(1L, saved.getRun().getId());
    assertEquals(2L, saved.getPartition().getId());
    assertEquals(3L, saved.getAliquot().getId());
  }

  @Test
  public void testSaveUpdate() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    Partition partition = (Partition) currentSession().get(PartitionImpl.class, 1L);
    ListLibraryAliquotView aliquot = (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, 1L);
    RunPartitionAliquotId id = new RunPartitionAliquotId(run, partition, aliquot);

    RunPartitionAliquot existing = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    assertNotNull(existing);
    assertNull(existing.getPurpose());

    RunPurpose purpose = (RunPurpose) currentSession().get(RunPurpose.class, 1L);
    assertNull(existing.getPurpose());
    existing.setPurpose(purpose);
    sut.save(existing);

    clearSession();

    RunPartitionAliquot saved = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    assertNotNull(saved.getPurpose());
    assertEquals(purpose.getId(), saved.getPurpose().getId());
  }

  @Test
  public void testDeleteForRunContainer() throws Exception {
    Run run = (Run) currentSession().get(Run.class, 1L);
    SequencerPartitionContainer container =
        (SequencerPartitionContainer) currentSession().get(SequencerPartitionContainerImpl.class, 1L);
    assertEquals(2, countForRunAndContainer(run, container));

    sut.deleteForRunContainer(run, container);

    clearSession();

    assertEquals(0, countForRunAndContainer(run, container));
  }

  private long countForRunAndContainer(Run run, SequencerPartitionContainer container) {
    LongQueryBuilder<RunPartitionAliquot> builder = new LongQueryBuilder<>(currentSession(), RunPartitionAliquot.class);
    Join<RunPartitionAliquot, Partition> join = builder.getJoin(builder.getRoot(), RunPartitionAliquot_.partition);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartitionAliquot_.run), run));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(join.get(PartitionImpl_.SEQUENCER_PARTITION_CONTAINER), container));
    return builder.getCount();
  }

  @Test
  public void testDeleteForPartition() throws Exception {
    Partition partition = (Partition) currentSession().get(PartitionImpl.class, 1L);

    QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> beforeBuilder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
    beforeBuilder.addPredicate(beforeBuilder.getCriteriaBuilder()
        .equal(beforeBuilder.getRoot().get(RunPartitionAliquot_.partition), partition));
    List<RunPartitionAliquot> before = beforeBuilder.getResultList();

    assertEquals(2, before.size());

    sut.deleteForPartition(partition);
    clearSession();

    QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> afterBuilder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
    afterBuilder.addPredicate(
        afterBuilder.getCriteriaBuilder().equal(afterBuilder.getRoot().get(RunPartitionAliquot_.partition), partition));
    List<RunPartitionAliquot> after = afterBuilder.getResultList();

    assertTrue(after.isEmpty());
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
    LongQueryBuilder<RunPartitionAliquot> builder = new LongQueryBuilder<>(currentSession(), RunPartitionAliquot.class);
    Join<RunPartitionAliquot, Partition> partitionJoin =
        builder.getJoin(builder.getRoot(), RunPartitionAliquot_.partition);
    Join<RunPartitionAliquot, ListLibraryAliquotView> aliquotJoin =
        builder.getJoin(builder.getRoot(), RunPartitionAliquot_.aliquot);
    builder.addPredicate(builder.getCriteriaBuilder().equal(partitionJoin.get(PartitionImpl_.POOL), pool));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(aliquotJoin.get(ListLibraryAliquotView_.aliquotId), aliquot.getId()));
    return builder.getCount();
  }

}
