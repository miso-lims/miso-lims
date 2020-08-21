package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderSummaryView;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderSummaryViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSequencingOrderSummaryViewDao
    implements SequencingOrderSummaryViewDao, HibernatePaginatedDataSource<SequencingOrderSummaryView> {

  private static final String[] SEARCH_PROPERTIES = new String[] { "pool.alias", "pool.name", "pool.identificationBarcode",
      "pool.description" };
  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("pool"),
      new AliasDescriptor("parameters"), new AliasDescriptor("containerModel", JoinType.LEFT_OUTER_JOIN));

  @Autowired
  private SessionFactory sessionFactory;
  
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Sequencing Order Summary";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends SequencingOrderSummaryView> getRealClass() {
    return SequencingOrderSummaryView.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
    case "containerModelAlias":
      return "containerModel.alias";
    default:
      return original;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByFulfilled(Criteria criteria, boolean isFulfilled, Consumer<String> errorHandler) {
    criteria.createAlias("fulfillmentView", "fulfillmentView", JoinType.LEFT_OUTER_JOIN)
        .createAlias("noContainerModelFulfillmentView", "noContainerModelFulfillmentView", JoinType.LEFT_OUTER_JOIN);
    if (isFulfilled) {
      criteria.add(Restrictions.or(
          Restrictions.and(Restrictions.isNotNull("fulfillmentView.fulfilled"),
              Restrictions.geProperty("fulfillmentView.fulfilled", "requested")),
          Restrictions.and(Restrictions.isNotNull("noContainerModelFulfillmentView.fulfilled"),
              Restrictions.geProperty("noContainerModelFulfillmentView.fulfilled", "requested"))));
    } else {
      criteria.add(Restrictions.and(
          Restrictions.or(Restrictions.isNull("fulfillmentView.fulfilled"),
              Restrictions.ltProperty("fulfillmentView.fulfilled", "requested")),
          Restrictions.or(Restrictions.isNull("noContainerModelFulfillmentView.fulfilled"),
              Restrictions.ltProperty("noContainerModelFulfillmentView.fulfilled", "requested"))));
    }
  }

  @Override
  public void restrictPaginationByPending(Criteria criteria, Consumer<String> errorHandler) {
    criteria.createAlias("partitions", "partition", JoinType.LEFT_OUTER_JOIN)
        .createAlias("noContainerModelPartitions", "noContainerModelPartition", JoinType.LEFT_OUTER_JOIN)
        .add(Restrictions.or(
            Restrictions.and(Restrictions.isNotEmpty("partitions"),
                Restrictions.isNull("partition.health")),
            Restrictions.and(Restrictions.isNotEmpty("noContainerModelPartitions"),
                Restrictions.isNull("noContainerModelPartition.health"))));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("pool.platformType", platformType));
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("pool.id", poolId));
  }

  @Override
  public void restrictPaginationById(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by ID", getFriendlyName()));
  }

  @Override
  public void restrictPaginationByIds(Criteria criteria, List<Long> ids, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by ID", getFriendlyName()));
  }

  @Override
  public void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths, Consumer<String> errorHandler) {
    criteria.createAlias("partitions", "partition")
        .createAlias("noContainerModelPartitions", "noContainerModelPartition")
        .add(Restrictions.or(
            Restrictions.in("partition.health", healths),
            Restrictions.in("noContainerModelPartition.health", healths)));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("pool.elements", "poolElement");
    criteria.createAlias("poolElement.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public void restrictPaginationBySequencingParametersId(Criteria criteria, long id, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("parameters.id", id));
  }

}
