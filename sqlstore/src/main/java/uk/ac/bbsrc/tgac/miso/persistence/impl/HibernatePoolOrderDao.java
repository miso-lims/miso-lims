package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolOrderDao extends HibernateSaveDao<PoolOrder>
    implements PoolOrderDao, JpaCriteriaPaginatedDataSource<PoolOrder, PoolOrder> {

  public HibernatePoolOrderDao() {
    super(PoolOrder.class);
  }

  @Override
  public String getFriendlyName() {
    return "Pool Order";
  }

  @Override
  public Class<PoolOrder> getEntityClass() {
    return PoolOrder.class;
  }

  @Override
  public Class<PoolOrder> getResultClass() {
    return PoolOrder.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<PoolOrder> root) {
    return Arrays.asList(root.get(PoolOrder_.alias), root.get(PoolOrder_.description));
  }

  @Override
  public SingularAttribute<PoolOrder, ?> getIdProperty() {
    return PoolOrder_.poolOrderId;
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, PoolOrder> builder, DateType type) {
    switch (type) {
      case ENTERED:
        return builder.getRoot().get(PoolOrder_.creationDate);
      case UPDATE:
        return builder.getRoot().get(PoolOrder_.lastUpdated);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, PoolOrder> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(PoolOrder_.poolOrderId);
      case "purposeAlias":
        Join<PoolOrder, RunPurpose> purpose = builder.getJoin(builder.getRoot(), PoolOrder_.purpose);
        return purpose.get(RunPurpose_.alias);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<PoolOrder, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? PoolOrder_.createdBy : PoolOrder_.updatedBy;
  }

  @Override
  public void restrictPaginationByFulfilled(QueryBuilder<?, PoolOrder> builder, boolean isFulfilled,
      Consumer<String> errorHandler) {
    if (isFulfilled) {
      builder.addPredicate(builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(PoolOrder_.pool)));
      builder.addPredicate(builder.getCriteriaBuilder().or(
          builder.getCriteriaBuilder().isNull(builder.getRoot().get(PoolOrder_.partitions)),
          builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(PoolOrder_.sequencingOrder))));
    } else {
      builder.addPredicate(
          builder.getCriteriaBuilder().or(builder.getCriteriaBuilder().isNull(builder.getRoot().get(PoolOrder_.pool)),
              builder.getCriteriaBuilder().and(
                  builder.getCriteriaBuilder().isNotNull(builder.getRoot().get(PoolOrder_.partitions)),
                  builder.getCriteriaBuilder().isNull(builder.getRoot().get(PoolOrder_.sequencingOrder)))));
    }
  }

  @Override
  public void restrictPaginationByDraft(QueryBuilder<?, PoolOrder> builder, boolean isDraft,
      Consumer<String> errorHandler) {
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(PoolOrder_.draft), isDraft));
  }

  @Override
  public List<PoolOrder> getAllByPoolId(long poolId) {
    QueryBuilder<PoolOrder, PoolOrder> builder = getQueryBuilder();
    Join<PoolOrder, PoolImpl> poolJoin = builder.getJoin(builder.getRoot(), PoolOrder_.pool);
    builder.addPredicate(builder.getCriteriaBuilder().equal(poolJoin.get(PoolImpl_.poolId), poolId));
    return builder.getResultList();
  }

}
