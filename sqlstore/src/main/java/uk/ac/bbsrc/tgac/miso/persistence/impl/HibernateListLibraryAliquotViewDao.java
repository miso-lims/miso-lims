package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.BoxImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryAliquotBoxPosition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.boxposition.LibraryAliquotBoxPosition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentProject;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentProject_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.ListLibraryAliquotViewDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateListLibraryAliquotViewDao extends HibernateProviderDao<ListLibraryAliquotView>
    implements ListLibraryAliquotViewDao,
    JpaCriteriaPaginatedDataSource<ListLibraryAliquotView, ListLibraryAliquotView> {

  public HibernateListLibraryAliquotViewDao() {
    super(ListLibraryAliquotView.class);
  }

  // Make sure these match the HiberateLibraryAliquotDao
  private static final List<SingularAttribute<ListLibraryAliquotView, String>> SEARCH_PROPERTIES = Arrays.asList(
      ListLibraryAliquotView_.name, ListLibraryAliquotView_.alias, ListLibraryAliquotView_.identificationBarcode);

  @Override
  public ListLibraryAliquotView get(Long aliquotId) throws IOException {
    return (ListLibraryAliquotView) currentSession().get(ListLibraryAliquotView.class, aliquotId);
  }

  @Override
  public SingularAttribute<ListLibraryAliquotView, ?> getIdProperty() {
    return ListLibraryAliquotView_.aliquotId;
  }

  @Override
  public Class<ListLibraryAliquotView> getEntityClass() {
    return ListLibraryAliquotView.class;
  }

  @Override
  public Class<ListLibraryAliquotView> getResultClass() {
    return ListLibraryAliquotView.class;
  }

  @Override
  public List<ListLibraryAliquotView> listByIdList(Collection<Long> aliquotIds) throws IOException {
    if (aliquotIds.size() == 0) {
      return Collections.emptyList();
    }
    return listByIdList(ListLibraryAliquotView_.ALIQUOT_ID, aliquotIds);
  }

  @Override
  public Path<?> propertyForSortColumn(Root<ListLibraryAliquotView> root, String original) {
    switch (original) {
      case "id":
        return root.get(ListLibraryAliquotView_.aliquotId);
      case "lastModified":
        return root.get(ListLibraryAliquotView_.lastUpdated);
      case "library.parentSampleId":
        return root.get(ListLibraryAliquotView_.parentLibrary).get(ParentLibrary_.parentSample)
            .get(ParentSample_.sampleId);
      case "library.parentSampleAlias":
        return root.get(ListLibraryAliquotView_.parentLibrary).get(ParentLibrary_.parentSample)
            .get(ParentSample_.alias);
      case "libraryPlatformType":
      case "library.platformType":
        return root.get(ListLibraryAliquotView_.parentLibrary).get(ParentLibrary_.platformType);
      case "creatorName":
        return root.get(ListLibraryAliquotView_.creator).get(UserImpl_.fullName);
      case "creationDate":
        return root.get(ListLibraryAliquotView_.created);
      case "effectiveTissueOriginAlias":
        return root.get(ListLibraryAliquotView_.parentLibrary).get(ParentLibrary_.parentSample)
            .get(ParentSample_.parentAttributes).get(ParentAttributes_.tissueAttributes)
            .get(ParentTissueAttributes_.tissueOrigin).get(TissueOriginImpl_.alias);
      case "effectiveTissueTypeAlias":
        return root.get(ListLibraryAliquotView_.parentLibrary).get(ParentLibrary_.parentSample)
            .get(ParentSample_.parentAttributes).get(ParentAttributes_.tissueAttributes)
            .get(ParentTissueAttributes_.tissueType).get(TissueTypeImpl_.alias);
      case "projectCode":
        return root.get(ListLibraryAliquotView_.parentLibrary).get(ParentLibrary_.parentSample)
            .get(ParentSample_.parentProject).get(ParentProject_.projectId);
      default:
        return root.get(original);
    }
  }

  @Override
  public List<SingularAttribute<ListLibraryAliquotView, String>> getIdentifierProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public List<SingularAttribute<ListLibraryAliquotView, String>> getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public SingularAttribute<ListLibraryAliquotView, ?> propertyForDate(DateType type) {
    switch (type) {
      case CREATE:
        return ListLibraryAliquotView_.created;
      case UPDATE:
        return ListLibraryAliquotView_.lastUpdated;
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<ListLibraryAliquotView, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? ListLibraryAliquotView_.creator : ListLibraryAliquotView_.lastModifier;
  }

  @Override
  public String getFriendlyName() {
    return "Library Aliquot";
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, ListLibraryAliquotView> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, ParentSample> sampleJoin = builder.getJoin(libraryJoin, ParentLibrary_.parentSample);
    Join<ParentSample, ParentProject> project = builder.getJoin(sampleJoin, ParentSample_.parentProject);
    builder.addPredicate(builder.getCriteriaBuilder().equal(project.get(ParentProject_.projectId), projectId));
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, ListLibraryAliquotView> builder,
      PlatformType platformType,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.parentLibrary);
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(libraryJoin.get(ParentLibrary_.platformType), platformType));
  }

  @Override
  public void restrictPaginationByPoolId(QueryBuilder<?, ListLibraryAliquotView> builder, long poolId,
      Consumer<String> errorHandler) {
    QueryBuilder<Long, PoolElement> poolBuilder = new QueryBuilder<>(currentSession(), PoolElement.class, Long.class);
    Join<PoolElement, PoolImpl> poolJoin = poolBuilder.getJoin(poolBuilder.getRoot(), PoolElement_.pool);
    poolBuilder.addPredicate(poolBuilder.getCriteriaBuilder().equal(poolJoin.get(PoolImpl_.poolId), poolId));
    Join<PoolElement, ListLibraryAliquotView> aliquotJoin =
        poolBuilder.getJoin(poolBuilder.getRoot(), PoolElement_.aliquot);
    poolBuilder.setColumns(aliquotJoin.get(ListLibraryAliquotView_.aliquotId));
    List<Long> aliquotIds = poolBuilder.getResultList();

    In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(ListLibraryAliquotView_.aliquotId));
    for (Long aliquotId : aliquotIds) {
      inClause.value(aliquotId);
    }
    builder.addPredicate(inClause);
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, ListLibraryAliquotView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.parentLibrary);
    if (LimsUtils.isStringBlankOrNull(query)) {
      builder.addPredicate(builder.getCriteriaBuilder().isNull(libraryJoin.get(ParentLibrary_.index1)));
    } else {
      Join<ParentLibrary, Index> index1 = builder.getJoin(libraryJoin, ParentLibrary_.index1);
      Join<ParentLibrary, Index> index2 = builder.getJoin(libraryJoin, ParentLibrary_.index2);
      builder.addTextRestriction(Arrays.asList(index1.get(Index_.name), index1.get(Index_.sequence),
          index2.get(Index_.name), index2.get(Index_.sequence)), query);
    }
  }

  @Override
  public void restrictPaginationByBox(QueryBuilder<?, ListLibraryAliquotView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, LibraryAliquotBoxPosition> boxPositionJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.boxPosition);
    if (LimsUtils.isStringBlankOrNull(query)) {
      builder.addPredicate(builder.getCriteriaBuilder().isNull(boxPositionJoin.get(LibraryAliquotBoxPosition_.box)));
    } else {
      Join<LibraryAliquotBoxPosition, BoxImpl> boxJoin =
          builder.getJoin(boxPositionJoin, LibraryAliquotBoxPosition_.box);
      List<Path<String>> searchProperties = new ArrayList<>();
      for (String property : HibernateBoxDao.SEARCH_PROPERTIES) {
        searchProperties.add(boxJoin.get(property));
      }
      builder.addTextRestriction(searchProperties, query);
    }
  }

  @Override
  public void restrictPaginationByFreezer(QueryBuilder<?, ListLibraryAliquotView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, LibraryAliquotBoxPosition> boxPositionJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.boxPosition);
    builder.addFreezerPredicate(boxPositionJoin, query);
  }

  @Override
  public void restrictPaginationByDate(QueryBuilder<?, ListLibraryAliquotView> builder, Date start, Date end,
      DateType type,
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
  public void restrictPaginationByDesign(QueryBuilder<?, ListLibraryAliquotView> builder, String design,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, LibraryDesignCode> designCodeJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.designCode);
    builder.addPredicate(builder.getCriteriaBuilder().equal(designCodeJoin.get(LibraryDesignCode_.code), design));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(QueryBuilder<?, ListLibraryAliquotView> builder, String query,
      Consumer<String> errorHandler) {
    builder.addDistributionRecipientPredicate(query, ListTransferView_.LIBRARY_ALIQUOTS,
        ListTransferViewLibraryAliquot_.ALIQUOT_ID, ListLibraryAliquotView_.ALIQUOT_ID);
  }

  @Override
  public void restrictPaginationByTissueOrigin(QueryBuilder<?, ListLibraryAliquotView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, ParentSample> sampleJoin = builder.getJoin(libraryJoin, ParentLibrary_.parentSample);
    Join<ParentSample, ParentAttributes> attrJoin = builder.getJoin(sampleJoin, ParentSample_.parentAttributes);
    Join<ParentAttributes, ParentTissueAttributes> tissueAttrJoin =
        builder.getJoin(attrJoin, ParentAttributes_.tissueAttributes);
    Join<ParentTissueAttributes, TissueOriginImpl> tissueOrigin =
        builder.getJoin(tissueAttrJoin, ParentTissueAttributes_.tissueOrigin);
    builder.addTextRestriction(tissueOrigin.get(TissueOriginImpl_.alias), query);
  }

  @Override
  public void restrictPaginationByTissueType(QueryBuilder<?, ListLibraryAliquotView> builder, String query,
      Consumer<String> errorHandler) {
    Join<ListLibraryAliquotView, ParentLibrary> libraryJoin =
        builder.getJoin(builder.getRoot(), ListLibraryAliquotView_.parentLibrary);
    Join<ParentLibrary, ParentSample> sampleJoin = builder.getJoin(libraryJoin, ParentLibrary_.parentSample);
    Join<ParentSample, ParentAttributes> attrJoin = builder.getJoin(sampleJoin, ParentSample_.parentAttributes);
    Join<ParentAttributes, ParentTissueAttributes> tissueAttrJoin =
        builder.getJoin(attrJoin, ParentAttributes_.tissueAttributes);
    Join<ParentTissueAttributes, TissueTypeImpl> tissueType =
        builder.getJoin(tissueAttrJoin, ParentTissueAttributes_.tissueType);
    builder.addTextRestriction(tissueType.get(TissueTypeImpl_.alias), query);
  }

  @Override
  public void restrictPaginationByWorksetId(QueryBuilder<?, ListLibraryAliquotView> builder, long worksetId,
      Consumer<String> errorHandler) {

    QueryBuilder<Long, Workset> idBuilder =
        new QueryBuilder<>(currentSession(), Workset.class, Long.class);
    Join<Workset, WorksetLibraryAliquot> worksetLibraryAliquot =
        idBuilder.getJoin(idBuilder.getRoot(), Workset_.worksetLibraryAliquots)
            .on(idBuilder.getCriteriaBuilder().equal(idBuilder.getRoot().get(Workset_.id), worksetId));
    Join<WorksetLibraryAliquot, LibraryAliquot> libraryAliquot =
        idBuilder.getJoin(worksetLibraryAliquot, WorksetLibraryAliquot_.item);
    idBuilder.setColumn(libraryAliquot.get(LibraryAliquot_.aliquotId));
    List<Long> ids = idBuilder.getResultList();

    In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(ListLibraryAliquotView_.aliquotId));
    for (Long id : ids) {
      inClause.value(id);
    }
    builder.addPredicate(inClause);
  }

  @Override
  public void restrictPaginationByBarcode(QueryBuilder<?, ListLibraryAliquotView> builder, String barcode,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot().get(ListLibraryAliquotView_.identificationBarcode), barcode);
  }
}
