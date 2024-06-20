package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType_;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartition_;
import uk.ac.bbsrc.tgac.miso.persistence.PartitionQcTypeDao;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernatePartitionQcTypeDao extends HibernateSaveDao<PartitionQCType> implements PartitionQcTypeDao {

  public HibernatePartitionQcTypeDao() {
    super(PartitionQCType.class);
  }

  @Override
  public PartitionQCType getByDescription(String description) throws IOException {
    QueryBuilder<PartitionQCType, PartitionQCType> builder =
        new QueryBuilder<>(currentSession(), PartitionQCType.class, PartitionQCType.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(PartitionQCType_.description), description));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<PartitionQCType> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(PartitionQCType_.PARTITION_QC_TYPE_ID, idList);
  }

  @Override
  public long getUsage(PartitionQCType type) throws IOException {
    LongQueryBuilder<RunPartition> builder = new LongQueryBuilder<>(currentSession(), RunPartition.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(RunPartition_.qcType), type));
    return builder.getCount();
  }

}
