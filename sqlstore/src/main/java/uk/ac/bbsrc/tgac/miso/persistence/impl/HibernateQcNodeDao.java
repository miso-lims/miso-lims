package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Join;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryAliquotQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryAliquotQcNode_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryQcNode_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.PoolQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.PoolQcNode_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionAliquotQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionAliquotQcNode_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNodePartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNodePartition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNode_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunQcNode_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode_;
import uk.ac.bbsrc.tgac.miso.persistence.QcNodeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateQcNodeDao implements QcNodeDao {

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public SampleQcNode getForSample(long id) throws IOException {
    SampleQcNode item = (SampleQcNode) currentSession().get(SampleQcNode.class, id);
    if (item == null) {
      return null;
    }
    SampleQcNode top = populateParents(item);
    populateChildren(item);
    return top;
  }

  @Override
  public SampleQcNode getForLibrary(long id) throws IOException {
    LibraryQcNode item = (LibraryQcNode) currentSession().get(LibraryQcNode.class, id);
    if (item == null) {
      return null;
    }
    SampleQcNode top = populateParents(item);
    populateChildren(item);
    return top;
  }

  @Override
  public SampleQcNode getForLibraryAliquot(long id) throws IOException {
    LibraryAliquotQcNode item = (LibraryAliquotQcNode) currentSession().get(LibraryAliquotQcNode.class, id);
    if (item == null) {
      return null;
    }
    SampleQcNode top = populateParents(item);
    populateChildren(item);
    return top;
  }

  @Override
  public SampleQcNode getForRunLibrary(long runId, long partitionId, long aliquotId) throws IOException {
    RunQcNode run = (RunQcNode) currentSession().get(RunQcNode.class, runId);

    QueryBuilder<Long, Run> idBuilder = new QueryBuilder<>(currentSession(), Run.class, Long.class);
    Join<Run, RunPosition> runPositionJoin = idBuilder.getJoin(idBuilder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> container =
        idBuilder.getJoin(runPositionJoin, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> parJoin =
        idBuilder.getJoin(container, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> poolJoin = idBuilder.getJoin(parJoin, PartitionImpl_.pool);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(idBuilder.getRoot().get(Run_.runId), runId));
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(parJoin.get(PartitionImpl_.id), partitionId));
    idBuilder.setColumns(poolJoin.get(PoolImpl_.poolId));
    Long id = idBuilder.getSingleResultOrNull();

    QueryBuilder<PoolQcNode, PoolQcNode> poolBuilder =
        new QueryBuilder<>(currentSession(), PoolQcNode.class, PoolQcNode.class);
    poolBuilder.addPredicate(poolBuilder.getCriteriaBuilder().equal(poolBuilder.getRoot().get(PoolQcNode_.id), id));
    PoolQcNode pool = poolBuilder.getSingleResultOrNull();

    pool.setRuns(Lists.newArrayList(run));

    LibraryAliquotQcNode aliquot = (LibraryAliquotQcNode) currentSession().get(LibraryAliquotQcNode.class, aliquotId);
    aliquot.setPools(Lists.newArrayList(pool));

    SampleQcNode top = populateParents(aliquot);

    QueryBuilder<RunPartitionQcNode, RunPartitionQcNode> partitionBuilder =
        new QueryBuilder<>(currentSession(), RunPartitionQcNode.class, RunPartitionQcNode.class);
    Join<RunPartitionQcNode, RunQcNode> runJoin =
        partitionBuilder.getJoin(partitionBuilder.getRoot(), RunPartitionQcNode_.run);
    partitionBuilder
        .addPredicate(partitionBuilder.getCriteriaBuilder().equal(runJoin.get(RunQcNode_.id), runId));
    Join<RunPartitionQcNode, RunPartitionQcNodePartition> partitionJoin =
        partitionBuilder.getJoin(partitionBuilder.getRoot(), RunPartitionQcNode_.partition);
    partitionBuilder.addPredicate(
        partitionBuilder.getCriteriaBuilder().equal(partitionJoin.get(RunPartitionQcNodePartition_.id), partitionId));
    RunPartitionQcNode partition = partitionBuilder.getSingleResultOrNull();

    run.setRunPartitions(Lists.newArrayList(partition));

    populateChildren(partition, run, aliquot);

    return top;
  }

  private SampleQcNode populateParents(SampleQcNode item) {
    if (item.getParentId() == null) {
      return item;
    } else {
      SampleQcNode parent = (SampleQcNode) currentSession().get(SampleQcNode.class, item.getParentId());
      parent.setChildSamples(Lists.newArrayList(item));
      return populateParents(parent);
    }
  }

  private SampleQcNode populateParents(LibraryQcNode item) {
    SampleQcNode parent = (SampleQcNode) currentSession().get(SampleQcNode.class, item.getSampleId());
    parent.setLibraries(Lists.newArrayList(item));
    return populateParents(parent);
  }

  private SampleQcNode populateParents(LibraryAliquotQcNode item) {
    if (item.getParentAliquotId() != null) {
      LibraryAliquotQcNode parent =
          (LibraryAliquotQcNode) currentSession().get(LibraryAliquotQcNode.class, item.getParentAliquotId());
      parent.setChildAliquots(Lists.newArrayList(item));
      return populateParents(parent);
    } else {
      LibraryQcNode parent = (LibraryQcNode) currentSession().get(LibraryQcNode.class, item.getLibraryId());
      parent.setAliquots(Lists.newArrayList(item));
      return populateParents(parent);
    }
  }

  private void populateChildren(SampleQcNode item) {
    QueryBuilder<SampleQcNode, SampleQcNode> childSampleBuilder =
        new QueryBuilder<>(currentSession(), SampleQcNode.class, SampleQcNode.class);
    childSampleBuilder.addPredicate(childSampleBuilder.getCriteriaBuilder()
        .equal(childSampleBuilder.getRoot().get(SampleQcNode_.parentId), item.getId()));
    List<SampleQcNode> childSamples = childSampleBuilder.getResultList();

    item.setChildSamples(childSamples);
    for (SampleQcNode child : childSamples) {
      populateChildren(child);
    }

    QueryBuilder<LibraryQcNode, LibraryQcNode> libraryBuilder =
        new QueryBuilder<>(currentSession(), LibraryQcNode.class, LibraryQcNode.class);
    libraryBuilder.addPredicate(
        libraryBuilder.getCriteriaBuilder().equal(libraryBuilder.getRoot().get(LibraryQcNode_.sampleId), item.getId()));
    List<LibraryQcNode> libraries = libraryBuilder.getResultList();

    item.setLibraries(libraries);
    for (LibraryQcNode library : libraries) {
      populateChildren(library);
    }
  }

  private void populateChildren(LibraryQcNode item) {
    QueryBuilder<LibraryAliquotQcNode, LibraryAliquotQcNode> aliquotBuilder =
        new QueryBuilder<>(currentSession(), LibraryAliquotQcNode.class, LibraryAliquotQcNode.class);
    aliquotBuilder.addPredicate(aliquotBuilder.getCriteriaBuilder()
        .isNull(aliquotBuilder.getRoot().get(LibraryAliquotQcNode_.parentAliquotId)));
    aliquotBuilder.addPredicate(aliquotBuilder.getCriteriaBuilder()
        .equal(aliquotBuilder.getRoot().get(LibraryAliquotQcNode_.libraryId), item.getId()));
    List<LibraryAliquotQcNode> aliquots = aliquotBuilder.getResultList();

    item.setAliquots(aliquots);
    for (LibraryAliquotQcNode aliquot : aliquots) {
      populateChildren(aliquot);
    }
  }

  private void populateChildren(LibraryAliquotQcNode item) {
    QueryBuilder<LibraryAliquotQcNode, LibraryAliquotQcNode> aliquotBuilder =
        new QueryBuilder<>(currentSession(), LibraryAliquotQcNode.class, LibraryAliquotQcNode.class);
    aliquotBuilder.addPredicate(aliquotBuilder.getCriteriaBuilder()
        .equal(aliquotBuilder.getRoot().get(LibraryAliquotQcNode_.parentAliquotId), item.getId()));
    List<LibraryAliquotQcNode> aliquots = aliquotBuilder.getResultList();

    item.setChildAliquots(aliquots);
    for (LibraryAliquotQcNode aliquot : aliquots) {
      populateChildren(aliquot);
    }

    QueryBuilder<Long, PoolImpl> idBuilder = new QueryBuilder<>(currentSession(), PoolImpl.class, Long.class);
    Join<PoolImpl, PoolElement> element = idBuilder.getJoin(idBuilder.getRoot(), PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquot = idBuilder.getJoin(element, PoolElement_.aliquot);
    idBuilder.addPredicate(
        idBuilder.getCriteriaBuilder().equal(aliquot.get(ListLibraryAliquotView_.aliquotId), item.getId()));
    idBuilder.setColumns(idBuilder.getRoot().get(PoolImpl_.poolId));
    List<Long> ids = idBuilder.getResultList();

    QueryBuilder<PoolQcNode, PoolQcNode> poolBuilder =
        new QueryBuilder<>(currentSession(), PoolQcNode.class, PoolQcNode.class);
    In<Long> inClause = poolBuilder.getCriteriaBuilder().in(poolBuilder.getRoot().get(PoolQcNode_.id));
    for (Long id : ids) {
      inClause.value(id);
    }
    poolBuilder.addPredicate(inClause);
    List<PoolQcNode> pools = poolBuilder.getResultList();

    item.setPools(pools);
    for (PoolQcNode pool : pools) {
      populateChildren(pool, item);
    }
  }

  private void populateChildren(PoolQcNode item, LibraryAliquotQcNode aliquot) {
    QueryBuilder<Long, Run> idBuilder = new QueryBuilder<>(currentSession(), Run.class, Long.class);
    Join<Run, RunPosition> runPositionJoin = idBuilder.getJoin(idBuilder.getRoot(), Run_.runPositions);
    Join<RunPosition, SequencerPartitionContainerImpl> containerJoin =
        idBuilder.getJoin(runPositionJoin, RunPosition_.container);
    Join<SequencerPartitionContainerImpl, PartitionImpl> partitionJoin =
        idBuilder.getJoin(containerJoin, SequencerPartitionContainerImpl_.partitions);
    Join<PartitionImpl, PoolImpl> poolJoin = idBuilder.getJoin(partitionJoin, PartitionImpl_.pool);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(poolJoin.get(PoolImpl_.poolId), item.getId()));
    idBuilder.setColumns(idBuilder.getRoot().get(Run_.runId));
    List<Long> ids = idBuilder.getResultList();

    QueryBuilder<RunQcNode, RunQcNode> runBuilder =
        new QueryBuilder<>(currentSession(), RunQcNode.class, RunQcNode.class);
    In<Long> inClause = runBuilder.getCriteriaBuilder().in(runBuilder.getRoot().get(RunQcNode_.id));
    for (Long id : ids) {
      inClause.value(id);
    }
    runBuilder.addPredicate(inClause);
    List<RunQcNode> runs = runBuilder.getResultList();

    item.setRuns(runs);
    for (RunQcNode run : runs) {
      populateChildren(run, item, aliquot);
    }
  }

  private void populateChildren(RunQcNode item, PoolQcNode pool, LibraryAliquotQcNode aliquot) {
    QueryBuilder<Long, PartitionImpl> idBuilder = new QueryBuilder<>(currentSession(), PartitionImpl.class, Long.class);
    Join<PartitionImpl, PoolImpl> poolJoin = idBuilder.getJoin(idBuilder.getRoot(), PartitionImpl_.pool);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(poolJoin.get(PoolImpl_.poolId), pool.getId()));
    idBuilder.setColumns(idBuilder.getRoot().get(PartitionImpl_.id));
    List<Long> ids = idBuilder.getResultList();

    QueryBuilder<RunPartitionQcNode, RunPartitionQcNode> partitionBuilder =
        new QueryBuilder<>(currentSession(), RunPartitionQcNode.class, RunPartitionQcNode.class);
    Join<RunPartitionQcNode, RunQcNode> runJoin =
        partitionBuilder.getJoin(partitionBuilder.getRoot(), RunPartitionQcNode_.run);
    partitionBuilder
        .addPredicate(partitionBuilder.getCriteriaBuilder().equal(runJoin.get(RunQcNode_.id), item.getId()));
    Join<RunPartitionQcNode, RunPartitionQcNodePartition> partitionJoin =
        partitionBuilder.getJoin(partitionBuilder.getRoot(), RunPartitionQcNode_.partition);

    In<Long> inClause = partitionBuilder.getCriteriaBuilder().in(partitionJoin.get(RunPartitionQcNodePartition_.id));
    for (Long id : ids) {
      inClause.value(id);
    }
    partitionBuilder.addPredicate(inClause);
    List<RunPartitionQcNode> partitions = partitionBuilder.getResultList();

    item.setRunPartitions(partitions);
    for (RunPartitionQcNode partition : partitions) {
      populateChildren(partition, item, aliquot);
    }
  }

  private void populateChildren(RunPartitionQcNode item, RunQcNode run, LibraryAliquotQcNode aliquot) {
    QueryBuilder<RunPartitionAliquotQcNode, RunPartitionAliquotQcNode> builder =
        new QueryBuilder<>(currentSession(), RunPartitionAliquotQcNode.class, RunPartitionAliquotQcNode.class);
    Join<RunPartitionAliquotQcNode, RunQcNode> runJoin =
        builder.getJoin(builder.getRoot(), RunPartitionAliquotQcNode_.run);
    Join<RunPartitionAliquotQcNode, RunPartitionQcNodePartition> partitionJoin =
        builder.getJoin(builder.getRoot(), RunPartitionAliquotQcNode_.partition);
    Join<RunPartitionAliquotQcNode, LibraryAliquotQcNode> aliquotJoin =
        builder.getJoin(builder.getRoot(), RunPartitionAliquotQcNode_.aliquot);
    builder.addPredicate(builder.getCriteriaBuilder().equal(runJoin.get(RunQcNode_.id), run.getId()));
    builder.addPredicate(builder.getCriteriaBuilder().equal(partitionJoin.get(RunPartitionQcNodePartition_.id),
        item.getPartition().getId()));
    builder.addPredicate(builder.getCriteriaBuilder().equal(aliquotJoin.get(LibraryAliquotQcNode_.id),
        aliquot.getId()));

    RunPartitionAliquotQcNode runLib = builder.getSingleResultOrNull();

    if (runLib == null) {
      runLib = new RunPartitionAliquotQcNode();
      runLib.setRun(run);
      runLib.setPartition(item.getPartition());
      runLib.setAliquot(aliquot);
    }
    item.setRunLibraries(Lists.newArrayList(runLib));
  }

}
