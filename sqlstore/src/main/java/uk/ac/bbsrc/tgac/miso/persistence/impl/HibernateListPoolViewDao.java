package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex_;
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

  @PersistenceContext
  private EntityManager entityManager;

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
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
  public List<Path<String>> getSearchProperties(Root<ListPoolView> root) {
    return Arrays.asList(root.get(ListPoolView_.name), root.get(ListPoolView_.alias),
        root.get(ListPoolView_.identificationBarcode), root.get(ListPoolView_.description));
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, ListPoolView> builder, DateType type) {
    switch (type) {
      case CREATE:
        return builder.getRoot().get(ListPoolView_.creationDate);
      case ENTERED:
        return builder.getRoot().get(ListPoolView_.creationTime);
      case UPDATE:
        return builder.getRoot().get(ListPoolView_.lastModified);
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
    Join<ListPoolViewElement, LibraryIndex> index1 = builder.getJoin(elementJoin, ListPoolViewElement_.index1);
    Join<ListPoolViewElement, LibraryIndex> index2 = builder.getJoin(elementJoin, ListPoolViewElement_.index2);
    builder.addTextRestriction(Arrays.asList(index1.get(LibraryIndex_.name), index1.get(LibraryIndex_.sequence),
        index2.get(LibraryIndex_.name), index2.get(LibraryIndex_.sequence)), query);
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
