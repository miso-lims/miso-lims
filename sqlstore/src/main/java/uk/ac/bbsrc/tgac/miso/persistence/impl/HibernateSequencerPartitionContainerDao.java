package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.List;

import javax.persistence.criteria.Join;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoreVersion;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerPartitionContainerImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.SequencerPartitionContainerStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencerPartitionContainerDao extends HibernateSaveDao<SequencerPartitionContainer>
    implements SequencerPartitionContainerStore {

  public HibernateSequencerPartitionContainerDao() {
    super(SequencerPartitionContainer.class, SequencerPartitionContainerImpl.class);
  }

  @Override
  public List<SequencerPartitionContainer> listAllSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships,
    // unexpected associations may show up
    currentSession().flush();

    QueryBuilder<SequencerPartitionContainer, SequencerPartitionContainerImpl> builder =
        new QueryBuilder<>(currentSession(), SequencerPartitionContainerImpl.class, SequencerPartitionContainer.class);
    Join<SequencerPartitionContainerImpl, RunPosition> runPos =
        builder.getJoin(builder.getRoot(), SequencerPartitionContainerImpl_.runPositions);
    Join<RunPosition, Run> run = builder.getJoin(runPos, RunPosition_.run);
    builder.addPredicate(builder.getCriteriaBuilder().equal(run.get(Run_.runId), runId));
    return builder.getResultList();
  }

  @Override
  public List<Partition> listAllPartitionsByPoolId(long poolId)
      throws IOException {
    QueryBuilder<Partition, PartitionImpl> builder =
        new QueryBuilder<>(currentSession(), PartitionImpl.class, Partition.class);
    Join<PartitionImpl, PoolImpl> pool = builder.getJoin(builder.getRoot(), PartitionImpl_.pool);
    builder.addPredicate(builder.getCriteriaBuilder().equal(pool.get(PoolImpl_.poolId), poolId));
    return builder.getResultList();
  }

  @Override
  public List<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    QueryBuilder<SequencerPartitionContainer, SequencerPartitionContainerImpl> builder =
        new QueryBuilder<>(currentSession(), SequencerPartitionContainerImpl.class, SequencerPartitionContainer.class);
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot().get(SequencerPartitionContainerImpl_.identificationBarcode), barcode));
    return builder.getResultList();
  }

  @Override
  public Partition getPartitionById(long partitionId) {
    return (Partition) currentSession().get(PartitionImpl.class, partitionId);
  }

  @Override
  public PoreVersion getPoreVersion(long id) {
    return (PoreVersion) currentSession().get(PoreVersion.class, id);
  }

  @Override
  public List<PoreVersion> listPoreVersions() {
    QueryBuilder<PoreVersion, PoreVersion> builder =
        new QueryBuilder<>(currentSession(), PoreVersion.class, PoreVersion.class);
    return builder.getResultList();
  }

  @Override
  public Long getPartitionIdByRunIdAndPartitionNumber(long runId, int partitionNumber) throws IOException {
    QueryBuilder<Long, PartitionImpl> builder = new QueryBuilder<>(currentSession(), PartitionImpl.class, Long.class);
    Join<PartitionImpl, SequencerPartitionContainerImpl> container =
        builder.getJoin(builder.getRoot(), PartitionImpl_.sequencerPartitionContainer);
    Join<SequencerPartitionContainerImpl, RunPosition> runPosition =
        builder.getJoin(container, SequencerPartitionContainerImpl_.runPositions);
    Join<RunPosition, Run> run = builder.getJoin(runPosition, RunPosition_.run);
    builder.addPredicate(builder.getCriteriaBuilder().equal(run.get(Run_.runId), runId));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(PartitionImpl_.partitionNumber), partitionNumber));
    builder.setColumn(builder.getRoot().get(PartitionImpl_.id));
    return builder.getSingleResultOrNull();
  }

}
