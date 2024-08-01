package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Index_;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode_;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedLibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryBatch;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset_;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDao extends HibernateSaveDao<Library>
    implements LibraryStore, JpaCriteriaPaginatedBoxableSource<Library, LibraryImpl> {

  public HibernateLibraryDao() {
    super(Library.class, LibraryImpl.class);
  }

  private interface AdjacencySelector {
    Predicate generateCriterion(QueryBuilder<EntityReference, LibraryImpl> builder,
        SingularAttribute<LibraryImpl, Long> associationPath, Long libraryId);

    /**
     * @return boolean true for ascending and false for descending
     */
    boolean getOrder();
  }

  private static final AdjacencySelector BEFORE = new AdjacencySelector() {

    @Override
    public Predicate generateCriterion(QueryBuilder<EntityReference, LibraryImpl> builder,
        SingularAttribute<LibraryImpl, Long> associationPath, Long libraryId) {
      return builder.getCriteriaBuilder().lessThan(builder.getRoot().get(associationPath), libraryId);
    }

    @Override
    public boolean getOrder() {
      return false;
    }

  };

  private static final AdjacencySelector AFTER = new AdjacencySelector() {

    @Override
    public Predicate generateCriterion(QueryBuilder<EntityReference, LibraryImpl> builder,
        SingularAttribute<LibraryImpl, Long> associationPath, Long libraryId) {
      return builder.getCriteriaBuilder().greaterThan(builder.getRoot().get(associationPath), libraryId);
    }

    @Override
    public boolean getOrder() {
      return true;
    }

  };

  @Autowired
  private SampleStore sampleStore;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public void setDetailedSample(boolean detailedSample) {
    this.detailedSample = detailedSample;
  }

  private final static List<SingularAttribute<? super LibraryImpl, String>> IDENTIFIER_FIELDS =
      Arrays.asList(LibraryImpl_.name, LibraryImpl_.alias, LibraryImpl_.identificationBarcode);

  @Override
  public List<Long> listByAlias(String alias) throws IOException {
    QueryBuilder<Long, LibraryImpl> builder = new QueryBuilder<>(currentSession(), LibraryImpl.class, Long.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryImpl_.alias), alias));
    builder.setColumn(builder.getRoot().get(LibraryImpl_.libraryId));
    return builder.getResultList();
  }

  @Override
  public List<Library> listBySampleId(long sampleId) throws IOException {
    QueryBuilder<Library, LibraryImpl> builder = getQueryBuilder();
    Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
    builder.addPredicate(builder.getCriteriaBuilder().equal(sample.get(SampleImpl_.sampleId), sampleId));
    return builder.getResultList();
  }

  @Override
  public List<Library> listByProjectId(long projectId) throws IOException {
    QueryBuilder<Library, LibraryImpl> builder = getQueryBuilder();
    Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
    Join<SampleImpl, ProjectImpl> project = builder.getJoin(sample, SampleImpl_.project);
    builder.addPredicate(builder.getCriteriaBuilder().equal(project.get(ProjectImpl_.id), projectId));
    return builder.getResultList();
  }

  @Override
  public List<Library> listByIdList(List<Long> idList) throws IOException {
    return listByIdList(LibraryImpl_.LIBRARY_ID, idList);
  }

  @Override
  public EntityReference getAdjacentLibrary(Library lib, boolean before) throws IOException {
    AdjacencySelector selector = before ? BEFORE : AFTER;

    // get library siblings
    QueryBuilder<EntityReference, LibraryImpl> builder =
        new QueryBuilder<>(currentSession(), LibraryImpl.class, EntityReference.class);
    Join<LibraryImpl, SampleImpl> sampleJoin = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(sampleJoin.get(SampleImpl_.sampleId), lib.getSample().getId()));
    builder.addPredicate(selector.generateCriterion(builder, LibraryImpl_.libraryId, lib.getId()));
    builder.addSort(builder.getRoot().get(LibraryImpl_.libraryId), selector.getOrder());
    builder.setColumns(builder.getRoot().get(LibraryImpl_.libraryId), builder.getRoot().get(LibraryImpl_.alias));

    List<EntityReference> library = builder.getResultList(1, 0);
    if (!library.isEmpty())
      return library.get(0);

    // get library cousins
    builder = new QueryBuilder<>(currentSession(), LibraryImpl.class, EntityReference.class);
    Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample, JoinType.INNER);
    Join<SampleImpl, ProjectImpl> project = builder.getJoin(sample, SampleImpl_.project, JoinType.INNER);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(project.get(ProjectImpl_.id), lib.getSample().getProject().getId()));
    builder.addPredicate(selector.generateCriterion(builder, LibraryImpl_.libraryId, lib.getId()));
    builder.addSort(sample.get(SampleImpl_.sampleId), selector.getOrder());
    builder.addSort(builder.getRoot().get(LibraryImpl_.libraryId), selector.getOrder());
    // Group by libraryId and alias, remove DISTINCT from query to sort by sampleId (not in select list)
    builder.addGroup(
        Arrays.asList(builder.getRoot().get(LibraryImpl_.libraryId), builder.getRoot().get(LibraryImpl_.alias)));
    builder.setColumns(builder.getRoot().get(LibraryImpl_.libraryId), builder.getRoot().get(LibraryImpl_.alias));

    List<EntityReference> libraryCousin = builder.getResultList(1, 0);
    return libraryCousin.isEmpty() ? null : libraryCousin.get(0);
  }

  @Override
  public List<Long> listIdsBySampleRequisitionId(long requisitionId) throws IOException {
    QueryBuilder<Long, SampleImpl> sampleBuilder = new QueryBuilder<>(currentSession(), SampleImpl.class, Long.class);
    Join<SampleImpl, Requisition> requisition = sampleBuilder.getJoin(sampleBuilder.getRoot(), SampleImpl_.requisition);
    sampleBuilder.addPredicate(
        sampleBuilder.getCriteriaBuilder().equal(requisition.get(Requisition_.requisitionId), requisitionId));
    sampleBuilder.setColumn(sampleBuilder.getRoot().get(SampleImpl_.sampleId));
    List<Long> requisitionSampleIds = sampleBuilder.getResultList();

    QueryBuilder<Long, RequisitionSupplementalSample> reqSampleBuilder =
        new QueryBuilder<>(currentSession(), RequisitionSupplementalSample.class, Long.class);
    Join<RequisitionSupplementalSample, SampleImpl> sample =
        reqSampleBuilder.getJoin(reqSampleBuilder.getRoot(), RequisitionSupplementalSample_.sample);
    reqSampleBuilder.addPredicate(reqSampleBuilder.getCriteriaBuilder()
        .equal(reqSampleBuilder.getRoot().get(RequisitionSupplementalSample_.requisitionId), requisitionId));
    reqSampleBuilder.setColumn(sample.get(SampleImpl_.sampleId));

    requisitionSampleIds.addAll(reqSampleBuilder.getResultList());

    if (requisitionSampleIds.isEmpty()) {
      return Collections.emptyList();
    }

    Set<Long> aliquotSampleIds =
        sampleStore.getChildIds(requisitionSampleIds, SampleAliquot.CATEGORY_NAME, requisitionId);

    Set<Long> parentIds = new HashSet<>(aliquotSampleIds);
    parentIds.addAll(requisitionSampleIds);

    QueryBuilder<Long, LibraryImpl> libraryBuilder =
        new QueryBuilder<>(currentSession(), LibraryImpl.class, Long.class);
    Join<LibraryImpl, SampleImpl> sampleJoin = libraryBuilder.getJoin(libraryBuilder.getRoot(), LibraryImpl_.sample);
    Join<LibraryImpl, Requisition> requisitionJoin =
        libraryBuilder.getJoin(libraryBuilder.getRoot(), LibraryImpl_.requisition);
    libraryBuilder.addInPredicate(sampleJoin.get(SampleImpl_.sampleId), parentIds);
    libraryBuilder.addPredicate(libraryBuilder.getCriteriaBuilder().or(
        libraryBuilder.getCriteriaBuilder().equal(requisitionJoin.get(Requisition_.requisitionId), requisitionId),
        libraryBuilder.getCriteriaBuilder().isNull(libraryBuilder.getRoot().get(LibraryImpl_.requisition))));
    libraryBuilder.setColumn(libraryBuilder.getRoot().get(LibraryImpl_.libraryId));
    return libraryBuilder.getResultList();
  }


  public List<Long> listIdsByAncestorSampleIdList(Collection<Long> sampleIds, Long effectiveRequisitionId)
      throws IOException {
    Set<Long> aliquotSampleIds =
        sampleStore.getChildIds(sampleIds, SampleAliquot.CATEGORY_NAME, effectiveRequisitionId);

    Set<Long> parentIds = new HashSet<>(aliquotSampleIds);
    parentIds.addAll(sampleIds);

    QueryBuilder<Long, LibraryImpl> builder = new QueryBuilder<>(currentSession(), LibraryImpl.class, Long.class);
    Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
    builder.addInPredicate(sample.get(SampleImpl_.sampleId), parentIds);
    builder.setColumn(builder.getRoot().get(LibraryImpl_.libraryId));

    if (effectiveRequisitionId != null) {
      Join<LibraryImpl, Requisition> requisition = builder.getJoin(builder.getRoot(), LibraryImpl_.requisition);
      builder.addPredicate(builder.getCriteriaBuilder().or(
          builder.getCriteriaBuilder().equal(requisition.get(Requisition_.requisitionId), effectiveRequisitionId),
          builder.getCriteriaBuilder().isNull(builder.getRoot().get(LibraryImpl_.requisition))));
    }
    return builder.getResultList();
  }

  @Override
  public List<SingularAttribute<? super LibraryImpl, String>> getIdentifierProperties() {
    return IDENTIFIER_FIELDS;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<LibraryImpl> root) {
    return Arrays.asList(root.get(LibraryImpl_.name), root.get(LibraryImpl_.alias),
        root.get(LibraryImpl_.description), root.get(LibraryImpl_.identificationBarcode));
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, LibraryImpl> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(LibraryImpl_.libraryId);
      case "parentSampleId":
        return builder.getJoin(builder.getRoot(), LibraryImpl_.sample).get(SampleImpl_.sampleId);
      case "parentSampleAlias":
        return builder.getJoin(builder.getRoot(), LibraryImpl_.sample).get(SampleImpl_.alias);
      case "effectiveTissueOriginAlias":
        Join<LibraryImpl, SampleImpl> tissueOriginSample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
        Join<DetailedSampleImpl, ParentAttributes> tissueOriginParentAttributes = builder.getJoin(
            builder.getCriteriaBuilder().treat(tissueOriginSample, DetailedSampleImpl.class),
            DetailedSampleImpl_.parentAttributes);
        Join<ParentAttributes, ParentTissueAttributes> tissueOriginTissueAttributes =
            builder.getJoin(tissueOriginParentAttributes, ParentAttributes_.tissueAttributes);
        Join<ParentTissueAttributes, TissueOriginImpl> tissueOrigin =
            builder.getJoin(tissueOriginTissueAttributes, ParentTissueAttributes_.tissueOrigin);
        return tissueOrigin.get(TissueOriginImpl_.alias);
      case "effectiveTissueTypeAlias":
        Join<LibraryImpl, SampleImpl> tissueTypeSample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
        Join<DetailedSampleImpl, ParentAttributes> tissueTypeParentAttributes = builder.getJoin(
            builder.getCriteriaBuilder().treat(tissueTypeSample, DetailedSampleImpl.class),
            DetailedSampleImpl_.parentAttributes);
        Join<ParentAttributes, ParentTissueAttributes> tissueTypeTissueAttributes =
            builder.getJoin(tissueTypeParentAttributes, ParentAttributes_.tissueAttributes);
        Join<ParentTissueAttributes, TissueTypeImpl> tissueType =
            builder.getJoin(tissueTypeTissueAttributes, ParentTissueAttributes_.tissueType);
        return tissueType.get(TissueTypeImpl_.alias);
      case "projectCode":
        Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
        Join<SampleImpl, ProjectImpl> project = builder.getJoin(sample, SampleImpl_.project);
        return project.get(ProjectImpl_.code);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public Path<?> propertyForDate(Root<LibraryImpl> root, DateType type) {
    switch (type) {
      case CREATE:
        return root.get(LibraryImpl_.creationDate);
      case ENTERED:
        return root.get(LibraryImpl_.creationTime);
      case UPDATE:
        return root.get(LibraryImpl_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<LibraryImpl, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? LibraryImpl_.creator : LibraryImpl_.lastModifier;
  }

  @Override
  public SingularAttribute<LibraryImpl, ?> getIdProperty() {
    return LibraryImpl_.libraryId;
  }

  @Override
  public Class<LibraryImpl> getEntityClass() {
    return LibraryImpl.class;
  }

  @Override
  public Class<Library> getResultClass() {
    return Library.class;
  }

  @Override
  public void restrictPaginationByDesign(QueryBuilder<?, LibraryImpl> builder, String design,
      Consumer<String> errorHandler) {
    Join<DetailedLibraryImpl, LibraryDesignCode> libraryDesignCode =
        builder.getJoin(builder.getRoot(DetailedLibraryImpl.class), DetailedLibraryImpl_.libraryDesignCode);
    builder.addPredicate(builder.getCriteriaBuilder().equal(libraryDesignCode.get(LibraryDesignCode_.code), design));
  }

  @Override
  public void restrictPaginationByPlatformType(QueryBuilder<?, LibraryImpl> builder, PlatformType platformType,
      Consumer<String> errorHandler) {
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryImpl_.platformType), platformType));
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, LibraryImpl> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
    Join<SampleImpl, ProjectImpl> project = builder.getJoin(sample, SampleImpl_.project);
    builder.addPredicate(builder.getCriteriaBuilder().equal(project.get(ProjectImpl_.id), projectId));
  }

  @Override
  public void restrictPaginationByIndex(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      builder.addPredicate(builder.getCriteriaBuilder().isNull(builder.getRoot().get(LibraryImpl_.index1)));
    } else {
      Join<LibraryImpl, Index> index1 = builder.getJoin(builder.getRoot(), LibraryImpl_.index1);
      Join<LibraryImpl, Index> index2 = builder.getJoin(builder.getRoot(), LibraryImpl_.index2);
      builder.addTextRestriction(Arrays.asList(index1.get(Index_.name), index1.get(Index_.sequence),
          index2.get(Index_.name), index2.get(Index_.sequence)), query);
    }
  }

  @Override
  public void restrictPaginationByWorkstationId(QueryBuilder<?, LibraryImpl> builder, long id,
      Consumer<String> errorHandler) {
    Join<LibraryImpl, Workstation> workstation = builder.getJoin(builder.getRoot(), LibraryImpl_.workstation);
    builder.addPredicate(builder.getCriteriaBuilder().equal(workstation.get(Workstation_.workstationId), id));
  }

  @Override
  public void restrictPaginationByKitName(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      builder.addPredicate(builder.getCriteriaBuilder().isNull(builder.getRoot().get(LibraryImpl_.kitDescriptor)));
    } else {
      Join<LibraryImpl, KitDescriptor> kitDescriptor = builder.getJoin(builder.getRoot(), LibraryImpl_.kitDescriptor);
      builder.addTextRestriction(kitDescriptor.get(KitDescriptor_.name), query);
    }
  }

  @Override
  public void restrictPaginationByGroupId(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot(DetailedLibraryImpl.class).get(DetailedLibraryImpl_.groupId), query);
  }

  @Override
  public void restrictPaginationByTissueOrigin(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
    Join<LibraryImpl, DetailedSampleImpl> detailedSample = builder.treatJoin(sample, DetailedSampleImpl.class);
    Join<DetailedSampleImpl, ParentAttributes> parentAttributes =
        builder.getJoin(detailedSample, DetailedSampleImpl_.parentAttributes);
    Join<ParentAttributes, ParentTissueAttributes> tissueAttributes =
        builder.getJoin(parentAttributes, ParentAttributes_.tissueAttributes);
    Join<ParentTissueAttributes, TissueOriginImpl> tissueOrigin =
        builder.getJoin(tissueAttributes, ParentTissueAttributes_.tissueOrigin);
    builder.addTextRestriction(tissueOrigin.get(TissueOriginImpl_.alias), query);
  }

  @Override
  public void restrictPaginationByTissueType(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<LibraryImpl, SampleImpl> sample = builder.getJoin(builder.getRoot(), LibraryImpl_.sample);
    Join<LibraryImpl, DetailedSampleImpl> detailedSample = builder.treatJoin(sample, DetailedSampleImpl.class);
    Join<DetailedSampleImpl, ParentAttributes> parentAttributes =
        builder.getJoin(detailedSample, DetailedSampleImpl_.parentAttributes);
    Join<ParentAttributes, ParentTissueAttributes> tissueAttributes =
        builder.getJoin(parentAttributes, ParentAttributes_.tissueAttributes);
    Join<ParentTissueAttributes, TissueTypeImpl> tissueType =
        builder.getJoin(tissueAttributes, ParentTissueAttributes_.tissueType);
    builder.addTextRestriction(tissueType.get(TissueTypeImpl_.alias), query);
  }

  @Override
  public String getFriendlyName() {
    return "Library";
  }

  @Override
  public void restrictPaginationByWorksetId(QueryBuilder<?, LibraryImpl> builder, long worksetId,
      Consumer<String> errorHandler) {
    QueryBuilder<Long, Workset> idBuilder = new QueryBuilder<>(currentSession(), Workset.class, Long.class);
    Join<Workset, WorksetLibrary> worksetLibrary = idBuilder.getJoin(idBuilder.getRoot(), Workset_.worksetLibraries);
    Join<WorksetLibrary, LibraryImpl> library = idBuilder.getJoin(worksetLibrary, WorksetLibrary_.item);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(idBuilder.getRoot().get(Workset_.id), worksetId));
    idBuilder.setColumn(library.get(LibraryImpl_.libraryId));
    List<Long> ids = idBuilder.getResultList();

    builder.addInPredicate(builder.getRoot().get(LibraryImpl_.libraryId), ids);
  }

  @Override
  public void restrictPaginationByBatchId(QueryBuilder<?, LibraryImpl> builder, String batchId,
      Consumer<String> errorHandler) {
    LibraryBatch batch = null;
    try {
      batch = new LibraryBatch(batchId);
    } catch (IllegalArgumentException e) {
      errorHandler.accept("Invalid batch ID");
      return;
    }
    Join<LibraryImpl, UserImpl> creator = builder.getJoin(builder.getRoot(), LibraryImpl_.creator);
    Join<LibraryImpl, Sop> sop = builder.getJoin(builder.getRoot(), LibraryImpl_.sop);
    Join<LibraryImpl, KitDescriptor> kitDescriptor = builder.getJoin(builder.getRoot(), LibraryImpl_.kitDescriptor);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryImpl_.creationDate), batch.getDate()));
    builder.addPredicate(builder.getCriteriaBuilder().equal(creator.get(UserImpl_.userId), batch.getUserId()));
    builder.addPredicate(builder.getCriteriaBuilder().equal(sop.get(Sop_.sopId), batch.getSopId()));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(kitDescriptor.get(KitDescriptor_.kitDescriptorId), batch.getKitId()));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(LibraryImpl_.kitLot), batch.getKitLot()));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addDistributionRecipientPredicate(query, ListTransferView_.LIBRARIES, ListTransferViewLibrary_.LIBRARY_ID,
        LibraryImpl_.LIBRARY_ID);
  }

  @Override
  public void restrictPaginationByWorkstation(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<LibraryImpl, Workstation> workstation = builder.getJoin(builder.getRoot(), LibraryImpl_.workstation);
    builder.addTextRestriction(
        Arrays.asList(workstation.get(Workstation_.alias), workstation.get(Workstation_.identificationBarcode)), query);
  }

  @Override
  public void restrictPaginationByRequisitionId(QueryBuilder<?, LibraryImpl> builder, long requisitionId,
      Consumer<String> errorHandler) {
    Join<LibraryImpl, Requisition> requisition = builder.getJoin(builder.getRoot(), LibraryImpl_.requisition);
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(requisition.get(Requisition_.requisitionId), requisitionId));
  }

  @Override
  public void restrictPaginationByRequisition(QueryBuilder<?, LibraryImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<LibraryImpl, Requisition> requisition = builder.getJoin(builder.getRoot(), LibraryImpl_.requisition);
    builder.addTextRestriction(requisition.get(Requisition_.alias), query);
  }

  @Override
  public void restrictPaginationBySupplementalToRequisitionId(QueryBuilder<?, LibraryImpl> builder, long requisitionId,
      Consumer<String> errorHandler) {
    QueryBuilder<Long, RequisitionSupplementalLibrary> idBuilder =
        new QueryBuilder<>(currentSession(), RequisitionSupplementalLibrary.class, Long.class);
    Join<RequisitionSupplementalLibrary, LibraryImpl> library =
        idBuilder.getJoin(idBuilder.getRoot(), RequisitionSupplementalLibrary_.library);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder()
        .equal(idBuilder.getRoot().get(RequisitionSupplementalLibrary_.requisitionId), requisitionId));
    idBuilder.setColumn(library.get(LibraryImpl_.libraryId));
    List<Long> ids = idBuilder.getResultList();

    builder.addInPredicate(builder.getRoot().get(LibraryImpl_.libraryId), ids);
  }

}
