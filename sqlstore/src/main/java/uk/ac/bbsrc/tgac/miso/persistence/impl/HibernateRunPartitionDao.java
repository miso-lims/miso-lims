package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunPartitionDao implements RunPartitionStore {

  @Autowired
  private SessionFactory sessionFactory;

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public RunPartition get(long runId, long partitionId) throws IOException {
    return (RunPartition) currentSession().createCriteria(RunPartition.class)
        .add(Restrictions.eq("runId", runId))
        .add(Restrictions.eq("partitionId", partitionId))
        .uniqueResult();
  }

  @Override
  public void create(RunPartition runPartition) throws IOException {
    currentSession().save(runPartition);
  }

  @Override
  public void update(RunPartition runPartition) throws IOException {
    currentSession().update(runPartition);
  }

  @Override
  public void deleteForRun(Run run) throws IOException {
    @SuppressWarnings("unchecked")
    List<RunPartition> runPartitions = currentSession().createCriteria(RunPartition.class)
        .add(Restrictions.eq("runId", run.getId()))
        .list();
    for (RunPartition runPartition : runPartitions) {
      currentSession().delete(runPartition);
    }
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    Set<Long> partitionIds = container.getPartitions().stream()
        .map(Partition::getId)
        .collect(Collectors.toSet());

    @SuppressWarnings("unchecked")
    List<RunPartition> items = currentSession().createCriteria(RunPartition.class)
        .add(Restrictions.eq("runId", run.getId()))
        .add(Restrictions.in("partitionId", partitionIds))
        .list();

    for (RunPartition item : items) {
      currentSession().delete(item);
    }
  }

}
