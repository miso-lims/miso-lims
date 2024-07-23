package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolViewElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolViewElement_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListPoolView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewPool_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.ListPoolViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListPoolViewDao
    implements ListPoolViewDao, JpaCriteriaPaginatedDataSource<ListPoolView, ListPoolView> {

  private final static List<SingularAttribute<? super ListPoolView, String>> SEARCH_PROPERTIES = Arrays
      .asList(ListPoolView_.name, ListPoolView_.alias, ListPoolView_.identificationBarcode, ListPoolView_.description);

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public String getFriendlyName() {
    return "Pool";
  }

  @Override
  public SingularAttribute<ListPoolView, ?> getIdProperty() {
    return ListPoolView_.poolId;
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, ListPoolView> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<ListPoolView, ListPoolViewElement> elementJoin = builder.getJoin(builder.getRoot(), ListPoolView_.elements);
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(elementJoin.get(ListPoolViewElement_.projectId), projectId));
  }

  @Override
  public Class<ListPoolView> getEntityClass() {
    return ListPoolView.class;
  }

  @Override
  public Class<ListPoolView> getResultClass() {
    return ListPoolView.class;
  }

  @Override
  public List<SingularAttribute<? super ListPoolView, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Path<?> propertyForDate(Root<ListPoolView> root, DateType type) {
    switch (type) {
      case CREATE:
        return root.get(ListPoolView_.creationDate);
      case ENTERED:
        return root.get(ListPoolView_.creationTime);
      case UPDATE:
        return root.get(ListPoolView_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, ListPoolView> builder, String original) {
    if ("creationDate".equals(original)) {
      return builder.getRoot().get(ListPoolView_.creationTime);
    } else if ("id".equals(original)) {
      return builder.getRoot().get(ListPoolView_.poolId);
    } else {
      return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<ListPoolView, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? ListPoolView_.creator : ListPoolView_.lastModifier;
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, ListPoolView> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(ListPoolView_.platformType), platformType));
  }

  @Override
  public void restrictPaginationByBox(QueryBuilder<?, ListPoolView> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(
        Arrays.asList(builder.getRoot().get(ListPoolView_.boxName), builder.getRoot().get(ListPoolView_.boxAlias)),
        query);
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, ListPoolView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListPoolView, ListPoolViewElement> elementJoin = builder.getJoin(builder.getRoot(), ListPoolView_.elements);
    Join<ListPoolViewElement, Index> index1 = builder.getJoin(elementJoin, ListPoolViewElement_.index1);
    Join<ListPoolViewElement, Index> index2 = builder.getJoin(elementJoin, ListPoolViewElement_.index2);
    builder.addTextRestriction(Arrays.asList(index1.get(Index_.name), index1.get(Index_.sequence),
        index2.get(Index_.name), index2.get(Index_.sequence)), query);
  }

  @Override
  public void restrictPaginationByFreezer(QueryBuilder<?, ListPoolView> builder, String query,
      Consumer<String> errorHandler) {
    builder.addFreezerPredicate(builder.getRoot(), query);
  }

  @Override
  public void restrictPaginationByDate(QueryBuilder<?, ListPoolView> builder, Date start, Date end, DateType type,
      Consumer<String> errorHandler) {
    if (type == DateType.RECEIVE) {
      builder.addReceiptTransferDatePredicate(start, end);
    } else if (type == DateType.DISTRIBUTED) {
      builder.addDistributionTransferDatePredicate(start, end);
    } else {
      JpaCriteriaPaginatedDataSource.super.restrictPaginationByDate(builder, start, end, type, errorHandler);
    }
  }

  @Override
  public void restrictPaginationByDistributionRecipient(QueryBuilder<?, ListPoolView> builder, String query,
      Consumer<String> errorHandler) {
    builder.addDistributionRecipientPredicate(
        query, ListTransferView_.POOLS, ListTransferViewPool_.POOL_ID, ListPoolView_.POOL_ID);
  }

  @Override
  public void restrictPaginationByBarcode(QueryBuilder<?, ListPoolView> builder, String barcode,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get(ListPoolView_.identificationBarcode), barcode);
  }

}
