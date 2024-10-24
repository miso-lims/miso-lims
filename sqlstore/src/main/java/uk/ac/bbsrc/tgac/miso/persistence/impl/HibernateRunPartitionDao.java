package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition_;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunPartitionDao extends HibernateProviderDao<RunPartition> implements RunPartitionStore {

  public HibernateRunPartitionDao() {
    super(RunPartition.class);
  }

  @Override
  public RunPartition get(long runId, long partitionId) throws IOException {
    QueryBuilder<RunPartition, RunPartition> builder =
        new QueryBuilder<>(currentSession(), RunPartition.class, RunPartition.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartition_.runId), runId));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartition_.partitionId), partitionId));
    return builder.getSingleResultOrNull();
  }

  @Override
  public void create(RunPartition runPartition) throws IOException {
    currentSession().persist(runPartition);
  }

  @Override
  public void update(RunPartition runPartition) throws IOException {
    currentSession().merge(runPartition);
  }

  @Override
  public void deleteForRun(Run run) throws IOException {
    QueryBuilder<RunPartition, RunPartition> builder =
        new QueryBuilder<>(currentSession(), RunPartition.class, RunPartition.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartition_.runId), run.getId()));
    List<RunPartition> runPartitions = builder.getResultList();

    for (RunPartition runPartition : runPartitions) {
      currentSession().remove(runPartition);
    }
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    Set<Long> partitionIds = container.getPartitions().stream()
        .map(Partition::getId)
        .collect(Collectors.toSet());

    QueryBuilder<RunPartition, RunPartition> builder =
        new QueryBuilder<>(currentSession(), RunPartition.class, RunPartition.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartition_.runId), run.getId()));
    builder.addInPredicate(builder.getRoot().get(RunPartition_.partitionId), partitionIds);
    List<RunPartition> items = builder.getResultList();

    for (RunPartition item : items) {
      currentSession().remove(item);
    }
  }

}
