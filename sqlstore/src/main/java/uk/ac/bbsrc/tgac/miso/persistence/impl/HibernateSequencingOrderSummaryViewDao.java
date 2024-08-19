package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencingContainerModel_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolViewElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolViewElement_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderFulfillmentView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderFulfillmentView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderNoContainerModelFulfillmentView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderNoContainerModelFulfillmentView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderPartitionView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderPartitionView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderSummaryView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderSummaryView_;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderSummaryViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSequencingOrderSummaryViewDao
    implements SequencingOrderSummaryViewDao,
    JpaCriteriaPaginatedDataSource<SequencingOrderSummaryView, SequencingOrderSummaryView> {

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public String getFriendlyName() {
    return "Sequencing Order Summary";
  }

  @Override
  public SingularAttribute<SequencingOrderSummaryView, ?> getIdProperty() {
    return SequencingOrderSummaryView_.orderSummaryId;
  }

  @Override
  public Class<SequencingOrderSummaryView> getEntityClass() {
    return SequencingOrderSummaryView.class;
  }

  @Override
  public Class<SequencingOrderSummaryView> getResultClass() {
    return SequencingOrderSummaryView.class;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<SequencingOrderSummaryView> root) {
    return Arrays.asList(root.get(SequencingOrderSummaryView_.pool).get(ListPoolView_.alias),
        root.get(SequencingOrderSummaryView_.pool).get(ListPoolView_.name),
        root.get(SequencingOrderSummaryView_.pool).get(ListPoolView_.identificationBarcode),
        root.get(SequencingOrderSummaryView_.pool).get(ListPoolView_.description));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, SequencingOrderSummaryView> builder, DateType type) {
    switch (type) {
      case UPDATE:
        return builder.getRoot().get(SequencingOrderSummaryView_.lastUpdated);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, SequencingOrderSummaryView> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(SequencingOrderSummaryView_.orderSummaryId);
      case "containerModelAlias":
        Join<SequencingOrderSummaryView, SequencingContainerModel> containerModel =
            builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.containerModel);
        return containerModel.get(SequencingContainerModel_.alias);
      case "pool.id":
        Join<SequencingOrderSummaryView, ListPoolView> poolId =
            builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.pool);
        return poolId.get(ListPoolView_.poolId);
      case "pool.alias":
        Join<SequencingOrderSummaryView, ListPoolView> poolAlias =
            builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.pool);
        return poolAlias.get(ListPoolView_.alias);
      case "parameters.name":
        Join<SequencingOrderSummaryView, SequencingParameters> parameters =
            builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.parameters);
        return parameters.get(SequencingParameters_.name);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<SequencingOrderSummaryView, ? extends UserImpl> propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByFulfilled(QueryBuilder<?, SequencingOrderSummaryView> builder, boolean isFulfilled,
      Consumer<String> errorHandler) {
    Join<SequencingOrderSummaryView, SequencingOrderFulfillmentView> fulfillmentView =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.fulfillmentView);
    Join<SequencingOrderSummaryView, SequencingOrderNoContainerModelFulfillmentView> noContainerModelFulfillmentView =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.noContainerModelFulfillmentView);

    if (isFulfilled) {
      builder.addPredicate(builder.getCriteriaBuilder().or(
          builder.getCriteriaBuilder().and(
              builder.getCriteriaBuilder()
                  .isNotNull(fulfillmentView.get(SequencingOrderFulfillmentView_.fulfilled)),
              builder.getCriteriaBuilder().greaterThanOrEqualTo(
                  fulfillmentView.get(SequencingOrderFulfillmentView_.fulfilled),
                  builder.getRoot().get(SequencingOrderSummaryView_.requested))),
          builder.getCriteriaBuilder().and(
              builder.getCriteriaBuilder()
                  .isNotNull(noContainerModelFulfillmentView
                      .get(SequencingOrderNoContainerModelFulfillmentView_.fulfilled)),
              builder.getCriteriaBuilder().greaterThanOrEqualTo(
                  noContainerModelFulfillmentView.get(SequencingOrderNoContainerModelFulfillmentView_.fulfilled),
                  builder.getRoot().get(SequencingOrderSummaryView_.requested)))));
    } else {
      builder.addPredicate(builder.getCriteriaBuilder().and(
          builder.getCriteriaBuilder().or(
              builder.getCriteriaBuilder()
                  .isNull(fulfillmentView.get(SequencingOrderFulfillmentView_.fulfilled)),
              builder.getCriteriaBuilder().lessThan(
                  fulfillmentView.get(SequencingOrderFulfillmentView_.fulfilled),
                  builder.getRoot().get(SequencingOrderSummaryView_.requested))),
          builder.getCriteriaBuilder().or(
              builder.getCriteriaBuilder()
                  .isNull(noContainerModelFulfillmentView
                      .get(SequencingOrderNoContainerModelFulfillmentView_.fulfilled)),
              builder.getCriteriaBuilder().lessThan(
                  noContainerModelFulfillmentView.get(SequencingOrderNoContainerModelFulfillmentView_.fulfilled),
                  builder.getRoot().get(SequencingOrderSummaryView_.requested)))));
    }
  }

  @Override
  public void restrictPaginationByPending(QueryBuilder<?, SequencingOrderSummaryView> builder,
      Consumer<String> errorHandler) {
    Join<SequencingOrderSummaryView, SequencingOrderPartitionView> partition =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.partitions);
    Join<SequencingOrderSummaryView, SequencingOrderPartitionView> noContainerModelPartitions =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.noContainerModelPartitions);
    builder.addPredicate(
        builder.getCriteriaBuilder().or(
            builder.getCriteriaBuilder().and(
                builder.getCriteriaBuilder()
                    .isNotEmpty(builder.getRoot().get(SequencingOrderSummaryView_.partitions)),
                builder.getCriteriaBuilder().isNull(partition.get(SequencingOrderPartitionView_.health))),
            builder.getCriteriaBuilder().and(
                builder.getCriteriaBuilder()
                    .isNotEmpty(builder.getRoot().get(SequencingOrderSummaryView_.noContainerModelPartitions)),
                builder.getCriteriaBuilder()
                    .isNull(noContainerModelPartitions.get(SequencingOrderPartitionView_.health)))));
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, SequencingOrderSummaryView> builder,
      PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<SequencingOrderSummaryView, ListPoolView> pool =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.pool);
    builder.addPredicate(builder.getCriteriaBuilder().equal(pool.get(ListPoolView_.platformType), platformType));
  }

  @Override
  public void restrictPaginationByPoolId(QueryBuilder<?, SequencingOrderSummaryView> builder, long poolId,
      Consumer<String> errorHandler) {
    Join<SequencingOrderSummaryView, ListPoolView> pool =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.pool);
    builder.addPredicate(builder.getCriteriaBuilder().equal(pool.get(ListPoolView_.poolId), poolId));
  }

  @Override
  public void restrictPaginationById(QueryBuilder<?, SequencingOrderSummaryView> builder, long id,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by ID", getFriendlyName()));
  }

  @Override
  public void restrictPaginationByIds(QueryBuilder<?, SequencingOrderSummaryView> builder, List<Long> ids,
      Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by ID", getFriendlyName()));
  }

  @Override
  public void restrictPaginationByHealth(QueryBuilder<?, SequencingOrderSummaryView> builder,
      EnumSet<HealthType> healths,
      Consumer<String> errorHandler) {
    Join<SequencingOrderSummaryView, SequencingOrderPartitionView> partition =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.partitions);
    Join<SequencingOrderSummaryView, SequencingOrderPartitionView> noContainerModelPartitions =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.noContainerModelPartitions);

    In<HealthType> inPartition = builder.getCriteriaBuilder().in(partition.get(SequencingOrderPartitionView_.health));
    In<HealthType> inNoContainerModelPartition =
        builder.getCriteriaBuilder().in(noContainerModelPartitions.get(SequencingOrderPartitionView_.health));
    for (HealthType health : healths) {
      inPartition.value(health);
      inNoContainerModelPartition.value(health);
    }
    builder.addPredicate(builder.getCriteriaBuilder().or(inPartition, inNoContainerModelPartition));
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, SequencingOrderSummaryView> builder, String query,
      Consumer<String> errorHandler) {
    Join<SequencingOrderSummaryView, ListPoolView> pool =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.pool);
    Join<ListPoolView, ListPoolViewElement> poolElement = builder.getJoin(pool, ListPoolView_.elements);
    Join<ListPoolViewElement, Index> index1 = builder.getJoin(poolElement, ListPoolViewElement_.index1);
    Join<ListPoolViewElement, Index> index2 = builder.getJoin(poolElement, ListPoolViewElement_.index2);
    builder.addTextRestriction(Arrays.asList(index1.get(Index_.name), index1.get(Index_.sequence),
        index2.get(Index_.name), index2.get(Index_.sequence)), query);

  }

  @Override
  public void restrictPaginationBySequencingParametersId(QueryBuilder<?, SequencingOrderSummaryView> builder, long id,
      Consumer<String> errorHandler) {
    Join<SequencingOrderSummaryView, SequencingParameters> parameters =
        builder.getJoin(builder.getRoot(), SequencingOrderSummaryView_.parameters);
    builder.addPredicate(builder.getCriteriaBuilder().equal(parameters.get(SequencingParameters_.parametersId), id));
  }

}
