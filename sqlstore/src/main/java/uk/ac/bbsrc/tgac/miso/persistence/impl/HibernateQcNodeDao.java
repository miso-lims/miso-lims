package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryAliquotQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.LibraryQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.PoolQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionAliquotQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunPartitionQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.RunQcNode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;
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

    DetachedCriteria subquery = DetachedCriteria.forClass(RunPartition.class)
        .createAlias("partition", "partition")
        .createAlias("partition.pool", "pool")
        .add(Restrictions.eq("run.id", runId))
        .add(Restrictions.eq("partition.id", partitionId))
        .setProjection(Projections.property("pool.id"));
    PoolQcNode pool = (PoolQcNode) currentSession().createCriteria(PoolQcNode.class)
        .add(Subqueries.propertyEq("id", subquery))
        .uniqueResult();
    pool.setRuns(Lists.newArrayList(run));

    LibraryAliquotQcNode aliquot = (LibraryAliquotQcNode) currentSession().get(LibraryAliquotQcNode.class, aliquotId);
    aliquot.setPools(Lists.newArrayList(pool));

    SampleQcNode top = populateParents(aliquot);

    RunPartitionQcNode partition = (RunPartitionQcNode) currentSession().createCriteria(RunPartitionQcNode.class)
        .add(Restrictions.eq("run.id", runId))
        .add(Restrictions.eq("partition.id", partitionId))
        .uniqueResult();
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
      LibraryAliquotQcNode parent = (LibraryAliquotQcNode) currentSession().get(LibraryAliquotQcNode.class, item.getParentAliquotId());
      parent.setChildAliquots(Lists.newArrayList(item));
      return populateParents(parent);
    } else {
      LibraryQcNode parent = (LibraryQcNode) currentSession().get(LibraryQcNode.class, item.getLibraryId());
      parent.setAliquots(Lists.newArrayList(item));
      return populateParents(parent);
    }
  }

  private void populateChildren(SampleQcNode item) {
    @SuppressWarnings("unchecked")
    List<SampleQcNode> childSamples = currentSession().createCriteria(SampleQcNode.class)
        .add(Restrictions.eq("parentId", item.getId()))
        .list();
    item.setChildSamples(childSamples);
    for (SampleQcNode child : childSamples) {
      populateChildren(child);
    }

    @SuppressWarnings("unchecked")
    List<LibraryQcNode> libraries = currentSession().createCriteria(LibraryQcNode.class)
        .add(Restrictions.eq("sampleId", item.getId()))
        .list();
    item.setLibraries(libraries);
    for (LibraryQcNode library : libraries) {
      populateChildren(library);
    }
  }

  private void populateChildren(LibraryQcNode item) {
    @SuppressWarnings("unchecked")
    List<LibraryAliquotQcNode> aliquots = currentSession().createCriteria(LibraryAliquotQcNode.class)
        .add(Restrictions.isNull("parentAliquotId"))
        .add(Restrictions.eq("libraryId", item.getId()))
        .list();
    item.setAliquots(aliquots);
    for (LibraryAliquotQcNode aliquot : aliquots) {
      populateChildren(aliquot);
    }
  }

  private void populateChildren(LibraryAliquotQcNode item) {
    @SuppressWarnings("unchecked")
    List<LibraryAliquotQcNode> aliquots = currentSession().createCriteria(LibraryAliquotQcNode.class)
        .add(Restrictions.eq("parentAliquotId", item.getId()))
        .list();
    item.setChildAliquots(aliquots);
    for (LibraryAliquotQcNode aliquot : aliquots) {
      populateChildren(aliquot);
    }

    DetachedCriteria subquery = DetachedCriteria.forClass(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.poolableElementView", "aliquot")
        .add(Restrictions.eq("aliquot.id", item.getId()))
        .setProjection(Projections.id());

    @SuppressWarnings("unchecked")
    List<PoolQcNode> pools = currentSession().createCriteria(PoolQcNode.class)
        .add(Subqueries.propertyIn("id", subquery))
        .list();
    item.setPools(pools);
    for (PoolQcNode pool : pools) {
      populateChildren(pool, item);
    }
  }

  private void populateChildren(PoolQcNode item, LibraryAliquotQcNode aliquot) {
    DetachedCriteria subquery = DetachedCriteria.forClass(Run.class)
        .createAlias("runPositions", "runPosition")
        .createAlias("runPosition.container", "container")
        .createAlias("container.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .add(Restrictions.eq("pool.id", item.getId()))
        .setProjection(Projections.id());

    @SuppressWarnings("unchecked")
    List<RunQcNode> runs = currentSession().createCriteria(RunQcNode.class)
        .add(Subqueries.propertyIn("id", subquery))
        .list();
    item.setRuns(runs);
    for (RunQcNode run : runs) {
      populateChildren(run, item, aliquot);
    }
  }

  private void populateChildren(RunQcNode item, PoolQcNode pool, LibraryAliquotQcNode aliquot) {
    DetachedCriteria subquery = DetachedCriteria.forClass(PartitionImpl.class)
        .add(Restrictions.eq("pool.id", pool.getId()))
        .setProjection(Projections.id());

    @SuppressWarnings("unchecked")
    List<RunPartitionQcNode> partitions = currentSession().createCriteria(RunPartitionQcNode.class)
        .add(Restrictions.eq("run.id", item.getId()))
        .add(Subqueries.propertyIn("partition.id", subquery))
        .list();
    item.setRunPartitions(partitions);
    for (RunPartitionQcNode partition : partitions) {
      populateChildren(partition, item, aliquot);
    }
  }

  private void populateChildren(RunPartitionQcNode item, RunQcNode run, LibraryAliquotQcNode aliquot) {
    RunPartitionAliquotQcNode runLib = (RunPartitionAliquotQcNode) currentSession().createCriteria(RunPartitionAliquotQcNode.class)
        .add(Restrictions.eq("run.id", run.getId()))
        .add(Restrictions.eq("partition.id", item.getPartition().getId()))
        .add(Restrictions.eq("aliquot.id", aliquot.getId()))
        .uniqueResult();
    if (runLib == null) {
      runLib = new RunPartitionAliquotQcNode();
      runLib.setRun(run);
      runLib.setPartition(item.getPartition());
      runLib.setAliquot(aliquot);
    }
    item.setRunLibraries(Lists.newArrayList(runLib));
  }

}
