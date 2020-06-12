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
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition.RunPartitionId;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunPartitionDao implements RunPartitionStore {

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public void create(RunPartition runPartition) throws IOException {
    currentSession().save(runPartition);
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public RunPartition get(Run run, Partition partition) throws IOException {
    RunPartitionId id = new RunPartitionId();
    id.setRun(run);
    id.setPartition(partition);
    return (RunPartition) currentSession().get(RunPartition.class, id);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void update(RunPartition runPartition) throws IOException {
    currentSession().update(runPartition);
  }

  @Override
  public void deleteForRun(Run run) throws IOException {
    for (RunPosition position : run.getRunPositions()) {
      for (Partition partition : position.getContainer().getPartitions()) {
        RunPartition runPartition = get(run, partition);
        currentSession().delete(runPartition);
      }
    }
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    @SuppressWarnings("unchecked")
    List<RunPartition> items = currentSession().createCriteria(RunPartition.class)
        .createAlias("partition", "partition")
        .add(Restrictions.eq("run", run))
        .add(Restrictions.eq("partition.sequencerPartitionContainer", container))
        .list();

    for (RunPartition item : items) {
      currentSession().delete(item);
    }
  }

}
