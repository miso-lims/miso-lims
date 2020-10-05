package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionAliquotDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunPartitionAliquotDao implements RunPartitionAliquotDao {

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
  public RunPartitionAliquot get(Run run, Partition partition, LibraryAliquot aliquot) throws IOException {
    RunPartitionAliquotId id = new RunPartitionAliquotId();
    id.setRun(run);
    id.setPartition(partition);
    id.setAliquot(aliquot);
    return (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
  }

  @Override
  public List<RunPartitionAliquot> listByRunId(long runId) throws IOException {
    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> results = currentSession().createCriteria(RunPartitionAliquot.class)
        .add(Restrictions.eq("run.id", runId))
        .list();
    return results;
  }

  @Override
  public List<RunPartitionAliquot> listByAliquotId(long aliquotId) throws IOException {
    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> results = currentSession().createCriteria(RunPartitionAliquot.class)
        .add(Restrictions.eq("aliquot.aliquotId", aliquotId))
        .list();
    return results;
  }

  @Override
  public void create(RunPartitionAliquot runPartitionAliquot) throws IOException {
    currentSession().save(runPartitionAliquot);
  }

  @Override
  public void update(RunPartitionAliquot runPartitionAliquot) throws IOException {
    currentSession().update(runPartitionAliquot);
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> items = currentSession().createCriteria(RunPartitionAliquot.class)
        .createAlias("partition", "partition")
        .add(Restrictions.eq("run", run))
        .add(Restrictions.eq("partition.sequencerPartitionContainer", container))
        .list();

    for (RunPartitionAliquot item : items) {
      currentSession().delete(item);
    }
  }

  @Override
  public void deleteForPoolAliquot(Pool pool, long aliquotId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Run> runs = currentSession().createCriteria(Run.class)
        .createAlias("runPositions", "runPosition")
        .createAlias("runPosition.container", "container")
        .createAlias("container.partitions", "partition")
        .add(Restrictions.eq("partition.pool", pool))
        .list();

    if (!runs.isEmpty()) {
      @SuppressWarnings("unchecked")
      List<RunPartitionAliquot> items = currentSession().createCriteria(RunPartitionAliquot.class)
          .createAlias("partition", "partition")
          .add(Restrictions.in("run", runs))
          .add(Restrictions.eq("aliquot.aliquotId", aliquotId))
          .list();

      for (RunPartitionAliquot item : items) {
        currentSession().delete(item);
      }
    }
  }

}
