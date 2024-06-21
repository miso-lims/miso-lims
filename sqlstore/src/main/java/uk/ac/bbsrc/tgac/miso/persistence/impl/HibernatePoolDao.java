package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentProject_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewPool_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.PoolStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolDao extends HibernateSaveDao<Pool>
    implements PoolStore, JpaCriteriaPaginatedBoxableSource<Pool, PoolImpl> {

  public HibernatePoolDao() {
    super(Pool.class, PoolImpl.class);
  }

  private final static List<SingularAttribute<PoolImpl, String>> IDENTIFIER_PROPERTIES =
      Arrays.asList(PoolImpl_.name, PoolImpl_.alias, PoolImpl_.identificationBarcode);

  private final static List<SingularAttribute<PoolImpl, String>> SEARCH_PROPERTIES =
      Arrays.asList(PoolImpl_.name, PoolImpl_.alias, PoolImpl_.identificationBarcode, PoolImpl_.description);

  @Override
  public Pool getByBarcode(String barcode) throws IOException {
    if (barcode == null)
      throw new NullPointerException("cannot look up null barcode");
    return getBy(PoolImpl_.IDENTIFICATION_BARCODE, barcode);
  }

  @Override
  public Pool getByAlias(String alias) throws IOException {
    return getBy(PoolImpl_.ALIAS, alias);
  }

  @Override
  public List<Pool> listByLibraryId(long libraryId) throws IOException {
    QueryBuilder<?, PoolImpl> builder =
        new QueryBuilder<>(currentSession(), PoolImpl.class, Object[].class, Criteria.DISTINCT_ROOT_ENTITY);
    Join<PoolImpl, PoolElement> elementJoin = builder.getJoin(builder.getRoot(), PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin = builder.getJoin(elementJoin, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(aliquotJoin, ListLibraryAliquotView_.parentLibrary);
    builder.addPredicate(builder.getCriteriaBuilder().equal(libraryJoin.get(ParentLibrary_.libraryId), libraryId));
    builder.setColumns(builder.getRoot());
    @SuppressWarnings("unchecked")
    List<Pool> results = (List<Pool>) builder.getResultList();
    return results;
  }

  @Override
  public List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException {
    QueryBuilder<Pool, PoolImpl> builder = getQueryBuilder();
    Join<PoolImpl, PoolElement> elementJoin = builder.getJoin(builder.getRoot(), PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin = builder.getJoin(elementJoin, PoolElement_.aliquot);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(aliquotJoin.get(ListLibraryAliquotView_.aliquotId), aliquotId));
    return builder.getResultList();
  }

  @Override
  public List<SingularAttribute<PoolImpl, String>> getIdentifierProperties() {
    return IDENTIFIER_PROPERTIES;
  }

  @Override
  public List<SingularAttribute<PoolImpl, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }


  @Override
  public SingularAttribute<PoolImpl, ?> getIdProperty() {
    return PoolImpl_.poolId;
  }

  @Override
  public Path<?> propertyForSortColumn(Root<PoolImpl> root, String original) {
    original = original.replaceAll("[^\\w]", "");
    if ("id".equals(original)) {
      return root.get(PoolImpl_.poolId);
    } else {
      return root.get(original);
    }
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, PoolImpl> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<PoolImpl, PoolElement> elementJoin = builder.getJoin(builder.getRoot(), PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin = builder.getJoin(elementJoin, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(aliquotJoin, ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, ParentSample> sampleJoin = builder.getJoin(libraryJoin, ParentLibrary_.parentSample);
    Join<ParentSample, ParentProject> projectJoin = builder.getJoin(sampleJoin, ParentSample_.parentProject);
    builder.addPredicate(builder.getCriteriaBuilder().equal(projectJoin.get(ParentProject_.projectId), projectId));
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, PoolImpl> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(PoolImpl_.platformType), platformType));
  }

  @Override
  public SingularAttribute<PoolImpl, ?> propertyForDate(DateType type) {
    switch (type) {
      case CREATE:
        return PoolImpl_.creationDate;
      case ENTERED:
        return PoolImpl_.creationTime;
      case UPDATE:
        return PoolImpl_.lastModified;
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<PoolImpl, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? PoolImpl_.creator : PoolImpl_.lastModifier;
  }

  @Override
  public Class<PoolImpl> getEntityClass() {
    return PoolImpl.class;
  }

  @Override
  public Class<Pool> getResultClass() {
    return Pool.class;
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, PoolImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<PoolImpl, PoolElement> elementJoin = builder.getJoin(builder.getRoot(), PoolImpl_.poolElements);
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin = builder.getJoin(elementJoin, PoolElement_.aliquot);
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(aliquotJoin, ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, Index> index1 = builder.getJoin(libraryJoin, ParentLibrary_.index1);
    Join<ParentLibrary, Index> index2 = builder.getJoin(libraryJoin, ParentLibrary_.index2);
    builder.addTextRestriction(Arrays.asList(index1.get(Index_.name), index1.get(Index_.sequence),
        index2.get(Index_.name), index2.get(Index_.sequence)), query);
  }

  @Override
  public void restrictPaginationByDistributionRecipient(QueryBuilder<?, PoolImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addDistributionRecipientPredicate(query, ListTransferView_.POOLS, ListTransferViewPool_.POOL_ID,
        PoolImpl_.POOL_ID);
  }

  @Override
  public String getFriendlyName() {
    return "Pool";
  }

  @Override
  public List<Pool> listByIdList(List<Long> poolIds) {
    return listByIdList(PoolImpl_.POOL_ID, poolIds);
    // if (poolIds.isEmpty())
    // return Collections.emptyList();
    // QueryBuilder<Pool, PoolImpl> builder = getQueryBuilder();
    // In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(PoolImpl_.poolId));
    // for (Long id : poolIds) {
    // inClause.value(id);
    // }
    // builder.addPredicate(inClause);
    // return builder.getResultList();
  }

  @Override
  public long getPartitionCount(Pool pool) {
    return getUsageBy(PartitionImpl.class, PartitionImpl_.POOL, pool);
  }

}
