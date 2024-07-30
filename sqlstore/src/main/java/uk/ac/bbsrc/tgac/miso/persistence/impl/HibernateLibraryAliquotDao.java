package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.OrderLibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAliquotStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryAliquotDao extends HibernateSaveDao<LibraryAliquot>
    implements LibraryAliquotStore, JpaCriteriaPaginatedBoxableSource<LibraryAliquot, LibraryAliquot> {

  public HibernateLibraryAliquotDao() {
    super(LibraryAliquot.class);
  }

  @Override
  public List<LibraryAliquot> listByLibraryId(long libraryId) throws IOException {
    QueryBuilder<LibraryAliquot, LibraryAliquot> builder = getQueryBuilder();
    Join<LibraryAliquot, LibraryImpl> libJoin = builder.getJoin(builder.getRoot(), LibraryAliquot_.library);
    builder.addPredicate(builder.getCriteriaBuilder().equal(libJoin.get(LibraryImpl_.libraryId), libraryId));
    return builder.getResultList();
  }

  @Override
  public LibraryAliquot getByBarcode(String barcode) throws IOException {
    if (barcode == null)
      throw new IOException("Barcode cannot be null!");

    return getBy(LibraryAliquot_.IDENTIFICATION_BARCODE, barcode);
  }

  @Override
  public SingularAttribute<LibraryAliquot, ?> getIdProperty() {
    return LibraryAliquot_.aliquotId;
  }

  @Override
  public Class<LibraryAliquot> getEntityClass() {
    return LibraryAliquot.class;
  }

  @Override
  public Class<LibraryAliquot> getResultClass() {
    return LibraryAliquot.class;
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, LibraryAliquot> builder, String original) {
    if ("id".equals(original)) {
      return builder.getRoot().get(LibraryAliquot_.aliquotId);
    } else {
      return builder.getRoot().get(original);
    }
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<LibraryAliquot> root) {
    // Make sure these match the HiberateListLibraryAliquotViewDao
    return Arrays.asList(root.get(LibraryAliquot_.name), root.get(LibraryAliquot_.alias),
        root.get(LibraryAliquot_.identificationBarcode));
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, LibraryAliquot> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<LibraryAliquot, LibraryImpl> libraryJoin = builder.getJoin(builder.getRoot(), LibraryAliquot_.library);
    Join<LibraryImpl, SampleImpl> sampleJoin = builder.getJoin(libraryJoin, LibraryImpl_.sample);
    Join<SampleImpl, ProjectImpl> projectJoin = builder.getJoin(sampleJoin, SampleImpl_.project);
    builder.addPredicate(builder.getCriteriaBuilder().equal(projectJoin.get(ProjectImpl_.id), projectId));
  }

  @Override
  public void restrictPaginationByPoolId(QueryBuilder<?, LibraryAliquot> builder, long poolId,
      Consumer<String> errorHandler) {

    QueryBuilder<Long, PoolImpl> poolBuilder = new QueryBuilder<>(currentSession(), PoolImpl.class, Long.class);
    Join<PoolImpl, PoolElement> elementJoin = poolBuilder.getJoin(poolBuilder.getRoot(), PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin = poolBuilder.getJoin(elementJoin, PoolElement_.aliquot);
    poolBuilder
        .addPredicate(poolBuilder.getCriteriaBuilder().equal(poolBuilder.getRoot().get(PoolImpl_.poolId), poolId));
    poolBuilder.setColumns(aliquotJoin.get(ListLibraryAliquotView_.aliquotId));
    List<Long> ids = poolBuilder.getResultList();

    In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(LibraryAliquot_.aliquotId));
    for (Long id : ids) {
      inClause.value(id);
    }
    builder.addPredicate(inClause);
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, LibraryAliquot> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<LibraryAliquot, LibraryImpl> libraryJoin = builder.getJoin(builder.getRoot(), LibraryAliquot_.library);
    builder.addPredicate(builder.getCriteriaBuilder().equal(libraryJoin.get(LibraryImpl_.platformType), platformType));
  }

  @Override
  public Path<?> propertyForDate(Root<LibraryAliquot> root, DateType type) {
    switch (type) {
      case CREATE:
        return root.get(LibraryAliquot_.creationDate);
      case ENTERED:
        return root.get(LibraryAliquot_.creationTime);
      case UPDATE:
        return root.get(LibraryAliquot_.lastUpdated);
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<LibraryAliquot, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? LibraryAliquot_.creator : LibraryAliquot_.lastModifier;
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, LibraryAliquot> builder, String query,
      Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      Join<LibraryAliquot, LibraryImpl> libraryJoin = builder.getJoin(builder.getRoot(), LibraryAliquot_.library);
      builder.addPredicate(builder.getCriteriaBuilder().isNull(libraryJoin.get(LibraryImpl_.index1)));
    } else {
      Join<LibraryAliquot, LibraryImpl> libraryJoin = builder.getJoin(builder.getRoot(), LibraryAliquot_.library);
      Join<LibraryImpl, Index> index1 = builder.getJoin(libraryJoin, LibraryImpl_.index1);
      Join<LibraryImpl, Index> index2 = builder.getJoin(libraryJoin, LibraryImpl_.index2);
      builder.addTextRestriction(
          Arrays.asList(
              index1.get(Index_.name), index1.get(Index_.sequence),
              index2.get(Index_.name), index2.get(Index_.sequence)),
          query);
    }
  }

  @Override
  public void restrictPaginationByGroupId(QueryBuilder<?, LibraryAliquot> builder, String query,
      Consumer<String> errorHandler) {
    Root<DetailedLibraryAliquot> root = builder.getRoot(DetailedLibraryAliquot.class);
    builder.addTextRestriction(root.get(DetailedLibraryAliquot_.groupId), query);
  }

  @Override
  public void restrictPaginationByDesign(QueryBuilder<?, LibraryAliquot> builder, String design,
      Consumer<String> errorHandler) {
    Root<DetailedLibraryAliquot> root = builder.getRoot(DetailedLibraryAliquot.class);
    Join<DetailedLibraryAliquot, LibraryDesignCode> join =
        builder.getJoin(root, DetailedLibraryAliquot_.libraryDesignCode);
    builder.addPredicate(builder.getCriteriaBuilder().equal(join.get(LibraryDesignCode_.code), design));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(QueryBuilder<?, LibraryAliquot> builder, String query,
      Consumer<String> errorHandler) {
    builder.addDistributionRecipientPredicate(query, ListTransferView_.LIBRARY_ALIQUOTS,
        ListTransferViewLibraryAliquot_.ALIQUOT_ID, LibraryAliquot_.ALIQUOT_ID);
  }

  @Override
  public String getFriendlyName() {
    return "Library Aliquot";
  }

  @Override
  public List<LibraryAliquot> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(LibraryAliquot_.ALIQUOT_ID, idList);
  }

  @Override
  public List<LibraryAliquot> listByPoolIds(Collection<Long> poolIds) throws IOException {
    if (poolIds.isEmpty()) {
      return Collections.emptyList();
    }

    QueryBuilder<Long, PoolImpl> poolBuilder = new QueryBuilder<>(currentSession(), PoolImpl.class, Long.class);
    Join<PoolImpl, PoolElement> elementJoin = poolBuilder.getJoin(poolBuilder.getRoot(), PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin = poolBuilder.getJoin(elementJoin, PoolElement_.aliquot);
    In<Long> poolInClause = poolBuilder.getCriteriaBuilder().in(poolBuilder.getRoot().get(PoolImpl_.poolId));
    for (Long poolId : poolIds) {
      poolInClause.value(poolId);
    }
    poolBuilder.addPredicate(poolInClause);
    poolBuilder.setColumns(aliquotJoin.get(ListLibraryAliquotView_.aliquotId));
    List<Long> ids = poolBuilder.getResultList();

    QueryBuilder<LibraryAliquot, LibraryAliquot> builder = getQueryBuilder();
    In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(LibraryAliquot_.aliquotId));
    for (Long id : ids) {
      inClause.value(id);
    }
    builder.addPredicate(inClause);
    return builder.getResultList();
  }

  @Override
  public long getUsageByPoolOrders(LibraryAliquot aliquot) throws IOException {
    LongQueryBuilder<PoolOrder> builder = new LongQueryBuilder<>(currentSession(), PoolOrder.class);
    Join<PoolOrder, OrderLibraryAliquot> orderJoin =
        builder.getJoin(builder.getRoot(), PoolOrder_.orderLibraryAliquots);
    builder.addPredicate(builder.getCriteriaBuilder().equal(orderJoin.get(OrderLibraryAliquot_.aliquot), aliquot));
    return builder.getCount();
  }

  @Override
  public long getUsageByChildAliquots(LibraryAliquot aliquot) throws IOException {
    return getUsageInCollection(LibraryAliquot.class, LibraryAliquot_.PARENT_ALIQUOT, aliquot);
  }

}
