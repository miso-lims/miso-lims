package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement_;
import uk.ac.bbsrc.tgac.miso.persistence.ListLibraryAliquotViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionAliquotDao;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunPartitionAliquotDao implements RunPartitionAliquotDao {

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private RunStore runStore;
  @Autowired
  private ListLibraryAliquotViewDao listLibraryAliquotViewDao;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public void setListLibraryAliquotViewDao(ListLibraryAliquotViewDao listLibraryAliquotViewDao) {
    this.listLibraryAliquotViewDao = listLibraryAliquotViewDao;
  }

  @Override
  public RunPartitionAliquot get(Run run, Partition partition, ListLibraryAliquotView aliquot) throws IOException {
    RunPartitionAliquotId id = new RunPartitionAliquotId();
    id.setRun(run);
    id.setPartition(partition);
    id.setAliquot(aliquot);
    RunPartitionAliquot result = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    if (result == null) {
      // ensure the relationship exists before constructing the entity
      QueryBuilder<Object[], Run> idBuilder = new QueryBuilder<>(currentSession(), Run.class, Object[].class);
      Join<Run, RunPosition> position = idBuilder.getJoin(idBuilder.getRoot(), Run_.runPositions, JoinType.INNER);
      Join<RunPosition, SequencerPartitionContainerImpl> container =
          idBuilder.getJoin(position, RunPosition_.container, JoinType.INNER);
      Join<SequencerPartitionContainerImpl, PartitionImpl> part =
          idBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions, JoinType.INNER);
      Join<PartitionImpl, PoolImpl> pool = idBuilder.getJoin(part, PartitionImpl_.pool, JoinType.INNER);
      Join<PoolImpl, PoolElement> element = idBuilder.getJoin(pool, PoolImpl_.poolElements, JoinType.INNER);
      Join<PoolElement, ListLibraryAliquotView> plAliquot =
          idBuilder.getJoin(element, PoolElement_.aliquot, JoinType.INNER);
      idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(idBuilder.getRoot().get(Run_.runId), run.getId()));
      idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(part.get(PartitionImpl_.id), partition.getId()));
      idBuilder.addPredicate(
          idBuilder.getCriteriaBuilder().equal(plAliquot.get(ListLibraryAliquotView_.aliquotId), aliquot.getId()));
      idBuilder.setColumns(idBuilder.getRoot().get(Run_.runId), part.get(PartitionImpl_.id),
          plAliquot.get(ListLibraryAliquotView_.aliquotId));
      List<Object[]> ids = idBuilder.getResultList();

      if (ids.isEmpty()) {
        return null;
      }
      result = new RunPartitionAliquot(run, partition, aliquot);
    }
    return result;
  }

  @Override
  public List<RunPartitionAliquot> listByRunId(long runId) throws IOException {
    QueryBuilder<Object[], Run> idBuilder = new QueryBuilder<>(currentSession(), Run.class, Object[].class);
    Join<Run, RunPosition> position = idBuilder.getJoin(idBuilder.getRoot(), Run_.runPositions, JoinType.INNER);
    Join<RunPosition, SequencerPartitionContainerImpl> container =
        idBuilder.getJoin(position, RunPosition_.container, JoinType.INNER);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        idBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions, JoinType.INNER);
    Join<PartitionImpl, PoolImpl> pool = idBuilder.getJoin(partition, PartitionImpl_.pool, JoinType.INNER);
    Join<PoolImpl, PoolElement> element = idBuilder.getJoin(pool, PoolImpl_.poolElements, JoinType.INNER);
    Join<PoolElement, ListLibraryAliquotView> plAliquot =
        idBuilder.getJoin(element, PoolElement_.aliquot, JoinType.INNER);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(idBuilder.getRoot().get(Run_.runId), runId));
    idBuilder.setColumns(idBuilder.getRoot().get(Run_.runId), partition.get(PartitionImpl_.id),
        plAliquot.get(ListLibraryAliquotView_.aliquotId));
    List<Object[]> ids = idBuilder.getResultList();

    QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> builder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
    Join<RunPartitionAliquot, Run> run = builder.getJoin(builder.getRoot(), RunPartitionAliquot_.run);
    builder.addPredicate(builder.getCriteriaBuilder().equal(run.get(Run_.runId), runId));
    List<RunPartitionAliquot> results = builder.getResultList();

    constructMissing(ids, results);
    return results;
  }

  @Override
  public List<RunPartitionAliquot> listByAliquotId(long aliquotId) throws IOException {
    QueryBuilder<Object[], Run> idBuilder = new QueryBuilder<>(currentSession(), Run.class, Object[].class);
    Join<Run, RunPosition> position = idBuilder.getJoin(idBuilder.getRoot(), Run_.runPositions, JoinType.INNER);
    Join<RunPosition, SequencerPartitionContainerImpl> container =
        idBuilder.getJoin(position, RunPosition_.container, JoinType.INNER);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        idBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions, JoinType.INNER);
    Join<PartitionImpl, PoolImpl> pool = idBuilder.getJoin(partition, PartitionImpl_.pool, JoinType.INNER);
    Join<PoolImpl, PoolElement> element = idBuilder.getJoin(pool, PoolImpl_.poolElements, JoinType.INNER);
    Join<PoolElement, ListLibraryAliquotView> plAliquot =
        idBuilder.getJoin(element, PoolElement_.aliquot, JoinType.INNER);
    idBuilder.addPredicate(
        idBuilder.getCriteriaBuilder().equal(plAliquot.get(ListLibraryAliquotView_.aliquotId), aliquotId));
    idBuilder.setColumns(idBuilder.getRoot().get(Run_.runId), partition.get(PartitionImpl_.id),
        plAliquot.get(ListLibraryAliquotView_.aliquotId));
    List<Object[]> ids = idBuilder.getResultList();

    QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> builder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
    Join<RunPartitionAliquot, ListLibraryAliquotView> aliquot =
        builder.getJoin(builder.getRoot(), RunPartitionAliquot_.aliquot);
    builder.addPredicate(builder.getCriteriaBuilder().equal(aliquot.get(ListLibraryAliquotView_.aliquotId), aliquotId));
    List<RunPartitionAliquot> results = builder.getResultList();

    constructMissing(ids, results);
    return results;
  }

  @Override
  public List<RunPartitionAliquot> listByLibraryIdList(Collection<Long> libraryIds) throws IOException {
    if (libraryIds == null || libraryIds.isEmpty()) {
      return Collections.emptyList();
    }

    QueryBuilder<Object[], Run> idBuilder = new QueryBuilder<>(currentSession(), Run.class, Object[].class);
    Join<Run, RunPosition> position = idBuilder.getJoin(idBuilder.getRoot(), Run_.runPositions, JoinType.INNER);
    Join<RunPosition, SequencerPartitionContainerImpl> container =
        idBuilder.getJoin(position, RunPosition_.container, JoinType.INNER);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        idBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions, JoinType.INNER);
    Join<PartitionImpl, PoolImpl> pool = idBuilder.getJoin(partition, PartitionImpl_.pool, JoinType.INNER);
    Join<PoolImpl, PoolElement> element = idBuilder.getJoin(pool, PoolImpl_.poolElements, JoinType.INNER);
    Join<PoolElement, ListLibraryAliquotView> plAliquot =
        idBuilder.getJoin(element, PoolElement_.aliquot, JoinType.INNER);
    Join<PoolElement, ListLibraryAliquotView> libraryAliquot =
        idBuilder.getJoin(element, PoolElement_.aliquot, JoinType.INNER);
    Join<ListLibraryAliquotView, ParentLibrary> parentLibrary =
        idBuilder.getJoin(libraryAliquot, ListLibraryAliquotView_.parentLibrary, JoinType.INNER);
    idBuilder.addInPredicate(parentLibrary.get(ParentLibrary_.libraryId), libraryIds);
    idBuilder.setColumns(idBuilder.getRoot().get(Run_.runId), partition.get(PartitionImpl_.id),
        plAliquot.get(ListLibraryAliquotView_.aliquotId));
    List<Object[]> ids = idBuilder.getResultList();

    QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> builder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
    Join<RunPartitionAliquot, ListLibraryAliquotView> aliquot =
        builder.getJoin(builder.getRoot(), RunPartitionAliquot_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> library =
        builder.getJoin(aliquot, ListLibraryAliquotView_.parentLibrary);
    builder.addInPredicate(library.get(ParentLibrary_.libraryId), libraryIds);
    List<RunPartitionAliquot> results = builder.getResultList();

    constructMissing(ids, results);
    return results;
  }

  private void constructMissing(List<Object[]> ids, List<RunPartitionAliquot> results) throws IOException {
    List<Long[]> missingIds = new ArrayList<>();
    for (Object[] id : ids) {
      long rowRunId = parseLong(id[0]);
      long partitionId = parseLong(id[1]);
      long aliquotId = parseLong(id[2]);

      if (results.stream().noneMatch(x -> matches(x, rowRunId, partitionId, aliquotId))) {
        missingIds.add(new Long[] {rowRunId, partitionId, aliquotId});
      }
    }

    if (!missingIds.isEmpty()) {
      List<Run> runs = runStore.listByIdList(getIdComponent(missingIds, 0));
      QueryBuilder<Partition, PartitionImpl> builder =
          new QueryBuilder<>(currentSession(), PartitionImpl.class, Partition.class);
      builder.addInPredicate(builder.getRoot().get(PartitionImpl_.id), getIdComponent(missingIds, 1));
      List<Partition> partitions = builder.getResultList();
      List<ListLibraryAliquotView> aliquots = listLibraryAliquotViewDao.listByIdList(getIdComponent(missingIds, 2));

      for (Long[] id : missingIds) {
        Run run = runs.stream().filter(x -> x.getId() == id[0].longValue()).findFirst().orElseThrow();
        Partition partition = partitions.stream().filter(x -> x.getId() == id[1].longValue()).findFirst().orElseThrow();
        ListLibraryAliquotView aliquot =
            aliquots.stream().filter(x -> x.getId() == id[2].longValue()).findFirst().orElseThrow();
        results.add(new RunPartitionAliquot(run, partition, aliquot));
      }
    }
  }

  private static long parseLong(Object value) {
    return Long.parseLong(value.toString());
  }

  private boolean matches(RunPartitionAliquot rpa, long runId, long partitionId, long aliquotId) {
    return rpa.getRun().getId() == runId
        && rpa.getPartition().getId() == partitionId
        && rpa.getAliquot().getId() == aliquotId;
  }

  private Set<Long> getIdComponent(List<Long[]> ids, int index) {
    return ids.stream().map(id -> id[index]).collect(Collectors.toSet());
  }

  @Override
  public void save(RunPartitionAliquot runPartitionAliquot) throws IOException {
    currentSession().persist(runPartitionAliquot);
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> builder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
    Join<RunPartitionAliquot, Partition> partition = builder.getJoin(builder.getRoot(), RunPartitionAliquot_.partition);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartitionAliquot_.run), run));
    builder.addPredicate(builder.getCriteriaBuilder().equal(partition.get("sequencerPartitionContainer"), container));
    List<RunPartitionAliquot> items = builder.getResultList();

    for (RunPartitionAliquot item : items) {
      currentSession().remove(item);
    }
  }

  @Override
  public void deleteForPartition(Partition partition) throws IOException {
    QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> builder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartitionAliquot_.partition), partition));
    List<RunPartitionAliquot> items = builder.getResultList();

    for (RunPartitionAliquot item : items) {
      currentSession().remove(item);
    }
  }

  @Override
  public void deleteForPoolAliquot(Pool pool, long aliquotId) throws IOException {
    QueryBuilder<Run, Run> runBuilder = new QueryBuilder<>(currentSession(), Run.class, Run.class);
    Join<Run, RunPosition> runPosition = runBuilder.getJoin(runBuilder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container =
        runBuilder.getJoin(runPosition, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partition =
        runBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    runBuilder.addPredicate(runBuilder.getCriteriaBuilder().equal(partition.get(PartitionImpl_.pool), pool));
    List<Run> runs = runBuilder.getResultList();

    if (!runs.isEmpty()) {
      QueryBuilder<RunPartitionAliquot, RunPartitionAliquot> builder =
          new QueryBuilder<>(currentSession(), RunPartitionAliquot.class, RunPartitionAliquot.class);
      Join<RunPartitionAliquot, ListLibraryAliquotView> aliquot =
          builder.getJoin(builder.getRoot(), RunPartitionAliquot_.aliquot);
      builder.addInPredicate(builder.getRoot().get(RunPartitionAliquot_.run), runs);
      builder
          .addPredicate(builder.getCriteriaBuilder().equal(aliquot.get(ListLibraryAliquotView_.aliquotId), aliquotId));
      List<RunPartitionAliquot> items = builder.getResultList();

      for (RunPartitionAliquot item : items) {
        currentSession().remove(item);
      }
    }
  }

}
