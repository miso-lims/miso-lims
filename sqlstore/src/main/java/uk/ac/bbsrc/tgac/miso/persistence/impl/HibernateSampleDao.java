package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.Array_;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentAttributes_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentIdentityAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentIdentityAttributes_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ParentTissueAttributes_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferViewSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.transfer.ListTransferView_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetSample_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset_;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleDao extends HibernateSaveDao<Sample>
    implements SampleStore, JpaCriteriaPaginatedBoxableSource<Sample, SampleImpl> {

  public HibernateSampleDao() {
    super(Sample.class, SampleImpl.class);
  }

  private static final List<SingularAttribute<? super SampleImpl, String>> IDENTIFIER_PROPERTIES =
      Arrays.asList(SampleImpl_.alias, SampleImpl_.identificationBarcode, SampleImpl_.name);

  private static final List<String> SAMPLE_CATEGORIES =
      Arrays.asList(SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME,
          SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME);

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public void setDetailedSample(boolean detailedSample) {
    this.detailedSample = detailedSample;
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    return getBy(SampleImpl_.IDENTIFICATION_BARCODE, barcode);
  }

  @Override
  public Collection<Sample> listByBarcodeList(Collection<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    QueryBuilder<Sample, SampleImpl> builder = getQueryBuilder();
    builder.addInPredicate(builder.getRoot().get(SampleImpl_.identificationBarcode), barcodeList);
    return builder.getResultList();
  }

  @Override
  public List<Sample> listByIdList(Collection<Long> idList) throws IOException {
    return listByIdList(SampleImpl_.SAMPLE_ID, idList);
  }

  @Override
  public Sample getByLibraryAliquotId(long aliquotId) {
    QueryBuilder<Long, LibraryAliquot> idBuilder =
        new QueryBuilder<>(currentSession(), LibraryAliquot.class, Long.class);
    Join<LibraryAliquot, LibraryImpl> library =
        idBuilder.getJoin(idBuilder.getRoot(), LibraryAliquot_.library, JoinType.INNER);
    Join<LibraryImpl, SampleImpl> sample = idBuilder.getJoin(library, LibraryImpl_.sample, JoinType.INNER);
    idBuilder.addPredicate(
        idBuilder.getCriteriaBuilder().equal(idBuilder.getRoot().get(LibraryAliquot_.aliquotId), aliquotId));
    idBuilder.setColumn(sample.get(SampleImpl_.sampleId));
    Long sampleId = idBuilder.getSingleResultOrNull();

    QueryBuilder<Sample, SampleImpl> builder = getQueryBuilder();
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleImpl_.sampleId), sampleId));
    return builder.getSingleResultOrNull();
  }

  @Override
  public List<EntityReference> listByAlias(String alias) throws IOException {
    QueryBuilder<EntityReference, SampleImpl> builder =
        new QueryBuilder<>(currentSession(), SampleImpl.class, EntityReference.class);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleImpl_.alias), alias));
    builder.setColumns(builder.getRoot().get(SampleImpl_.sampleId), builder.getRoot().get(SampleImpl_.alias));
    return builder.getResultList();
  }

  @Override
  public List<IdentityView> getIdentitiesByExternalNameOrAliasAndProject(String query, Long projectId,
      boolean exactMatch)
      throws IOException {
    if (isStringEmptyOrNull(query)) {
      return Collections.emptyList();
    }

    List<IdentityView> records = (List<IdentityView>) SampleIdentityImpl.getSetFromString(query)
        .stream().map(extNameOrAlias -> {
          QueryBuilder<IdentityView, IdentityView> builder =
              new QueryBuilder<>(currentSession(), IdentityView.class, IdentityView.class);
          builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(IdentityView_.discriminator),
              SampleIdentity.CATEGORY_NAME));
          if (projectId != null) {
            builder.addPredicate(
                builder.getCriteriaBuilder().equal(builder.getRoot().get(IdentityView_.projectId), projectId));
          }
          builder.addTextRestriction(Arrays.asList(builder.getRoot().get(IdentityView_.externalName),
              builder.getRoot().get(IdentityView_.alias)), extNameOrAlias);
          return builder.getResultList();
        }).flatMap(list -> list.stream())
        .distinct()
        .collect(Collectors.toList());

    // filter out those with a non-exact external name match
    if (exactMatch) {
      return filterOnlyExactExternalNameMatches(records, query);
    } else {
      return records;
    }
  }

  private List<IdentityView> filterOnlyExactExternalNameMatches(Collection<IdentityView> candidates,
      String externalNamesOrAlias) {
    return candidates.stream().filter(sam -> {
      Set<String> targets = SampleIdentityImpl.getSetFromString(externalNamesOrAlias).stream().map(String::toLowerCase)
          .collect(Collectors.toSet());
      Set<String> externalNamesOfCandidate = SampleIdentityImpl.getSetFromString(sam.getExternalName()).stream()
          .map(String::toLowerCase).collect(Collectors.toSet());
      targets.retainAll(externalNamesOfCandidate);
      return !targets.isEmpty() || externalNamesOrAlias.equals(sam.getAlias());
    }).collect(Collectors.toList());
  }

  @Override
  public List<IdentityView> getIdentities(Collection<String> externalNames, boolean exactMatch, Project project)
      throws IOException {
    if (externalNames == null || externalNames.isEmpty()) {
      return Collections.emptyList();
    }
    QueryBuilder<IdentityView, IdentityView> builder =
        new QueryBuilder<>(currentSession(), IdentityView.class, IdentityView.class);
    if (project != null) {
      builder.addPredicate(
          builder.getCriteriaBuilder().equal(builder.getRoot().get(IdentityView_.projectId), project.getId()));
    }

    Predicate disjunction = builder.getCriteriaBuilder().disjunction();
    for (String externalName : externalNames) {
      disjunction = builder.getCriteriaBuilder().or(disjunction, builder.getCriteriaBuilder()
          .like(builder.getRoot().get(IdentityView_.externalName), '%' + externalName + '%'));
    }
    builder.addPredicate(disjunction);

    List<IdentityView> results = builder.getResultList();
    if (exactMatch) {
      return filterOnlyExactExternalNameMatches(results, externalNames);
    } else {
      return results;
    }
  }

  private List<IdentityView> filterOnlyExactExternalNameMatches(Collection<IdentityView> candidates,
      Collection<String> externalNames) {
    return candidates.stream()
        .filter(identity -> {
          Set<String> names = SampleIdentityImpl.getSetFromString(identity.getExternalName());
          return names.stream().anyMatch(name -> externalNames.contains(name));
        })
        .collect(Collectors.toList());
  }

  @Override
  public SampleTissue getMatchingGhostTissue(SampleTissue tissue) {
    validateGhostTissueLookup(tissue);

    QueryBuilder<SampleTissue, SampleTissueImpl> builder =
        new QueryBuilder<>(currentSession(), SampleTissueImpl.class, SampleTissue.class);
    Join<SampleTissueImpl, DetailedSampleImpl> parent =
        builder.getJoin(builder.getRoot(), SampleTissueImpl_.parent);
    Join<SampleTissueImpl, ParentAttributes> parentAttributes =
        builder.getJoin(builder.getRoot(), SampleTissueImpl_.parentAttributes);
    Join<ParentAttributes, ParentTissueAttributes> tissueAttributes =
        builder.getJoin(parentAttributes, ParentAttributes_.tissueAttributes);
    Join<ParentTissueAttributes, TissueOriginImpl> tissueOrigin =
        builder.getJoin(tissueAttributes, ParentTissueAttributes_.tissueOrigin);
    Join<ParentTissueAttributes, TissueTypeImpl> tissueType =
        builder.getJoin(tissueAttributes, ParentTissueAttributes_.tissueType);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleTissueImpl_.isSynthetic), true));
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(parent.get(DetailedSampleImpl_.sampleId), tissue.getParent().getId()));
    builder.addPredicate(builder.getCriteriaBuilder().equal(tissueOrigin.get(TissueOriginImpl_.tissueOriginId),
        tissue.getTissueOrigin().getId()));
    builder.addPredicate(builder.getCriteriaBuilder().equal(tissueType.get(TissueTypeImpl_.tissueTypeId),
        tissue.getTissueType().getId()));
    builder.addPredicate(
        eqNullable(builder, builder.getRoot().get(SampleTissueImpl_.timesReceived), tissue.getTimesReceived()));
    builder.addPredicate(
        eqNullable(builder, builder.getRoot().get(SampleTissueImpl_.tubeNumber), tissue.getTubeNumber()));
    builder.addPredicate(
        eqNullable(builder, builder.getRoot().get(SampleTissueImpl_.passageNumber), tissue.getPassageNumber()));
    builder.addPredicate(
        eqNullable(builder, builder.getRoot().get(SampleTissueImpl_.timepoint), tissue.getTimepoint()));
    return builder.getSingleResultOrNull();
  }

  private Predicate eqNullable(QueryBuilder<SampleTissue, SampleTissueImpl> builder, Path<?> property,
      Object value) {
    return value == null ? builder.getCriteriaBuilder().isNull(property)
        : builder.getCriteriaBuilder().equal(property, value);
  }

  private void validateGhostTissueLookup(SampleTissue tissue) {
    if (tissue.getParent() == null
        || !tissue.getParent().isSaved()
        || tissue.getTissueOrigin() == null
        || !tissue.getTissueOrigin().isSaved()
        || tissue.getTissueType() == null
        || !tissue.getTissueType().isSaved()) {
      throw new IllegalArgumentException("Missing tissue attributes required for lookup");
    }
  }

  @Override
  public Path<?> propertyForSortColumn(QueryBuilder<?, SampleImpl> builder, String original) {
    switch (original) {
      case "id":
        return builder.getRoot().get(SampleImpl_.sampleId);
      case "effectiveExternalNames":
        Join<DetailedSampleImpl, ParentAttributes> parentAttributes =
            builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parentAttributes);
        Join<ParentAttributes, ParentIdentityAttributes> identityAttributes =
            builder.getJoin(parentAttributes, ParentAttributes_.identityAttributes);
        return identityAttributes.get(ParentIdentityAttributes_.externalName);
      case "effectiveTissueOriginAlias":
        Join<DetailedSampleImpl, ParentAttributes> tissueOriginParentAttributes =
            builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parentAttributes);
        Join<ParentAttributes, ParentTissueAttributes> tissueOriginTissueAttributes =
            builder.getJoin(tissueOriginParentAttributes, ParentAttributes_.tissueAttributes);
        Join<ParentTissueAttributes, TissueOriginImpl> tissueOrigin =
            builder.getJoin(tissueOriginTissueAttributes, ParentTissueAttributes_.tissueOrigin);
        return tissueOrigin.get(TissueOriginImpl_.alias);
      case "effectiveTissueTypeAlias":
        Join<DetailedSampleImpl, ParentAttributes> tissueTypeParentAttributes =
            builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parentAttributes);
        Join<ParentAttributes, ParentTissueAttributes> tissueTypeTissueAttributes =
            builder.getJoin(tissueTypeParentAttributes, ParentAttributes_.tissueAttributes);
        Join<ParentTissueAttributes, TissueTypeImpl> tissueType =
            builder.getJoin(tissueTypeTissueAttributes, ParentTissueAttributes_.tissueType);
        return tissueType.get(TissueTypeImpl_.alias);
      case "sampleClassId":
        return builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.sampleClass)
            .get(SampleClassImpl_.alias);
      case "projectCode":
        return builder.getJoin(builder.getRoot(), SampleImpl_.project).get(ProjectImpl_.code);
      case "creationDate":
        // Using an unchecked cast here since CriteriaBuilder.treat() does not work here
        @SuppressWarnings("unchecked")
        Root<DetailedSampleImpl> detailedSampleRoot = ((Root<DetailedSampleImpl>) (Object) builder.getRoot());
        return detailedSampleRoot.get(DetailedSampleImpl_.creationDate);
      default:
        return builder.getRoot().get(original);
    }
  }

  @Override
  public SingularAttribute<SampleImpl, ?> getIdProperty() {
    return SampleImpl_.sampleId;
  }

  @Override
  public Class<SampleImpl> getEntityClass() {
    return SampleImpl.class;
  }

  @Override
  public Class<Sample> getResultClass() {
    return Sample.class;
  }

  @Override
  public Path<?> propertyForDate(QueryBuilder<?, SampleImpl> builder, DateType type) {
    switch (type) {
      case CREATE:
        // Using an unchecked cast here since CriteriaBuilder.treat() does not work here
        @SuppressWarnings("unchecked")
        Root<DetailedSampleImpl> detailedSampleRoot = ((Root<DetailedSampleImpl>) (Object) builder.getRoot());
        return detailedSample
            ? detailedSampleRoot.get(DetailedSampleImpl_.creationDate)
            : null;
      case ENTERED:
        return builder.getRoot().get(SampleImpl_.creationTime);
      case UPDATE:
        return builder.getRoot().get(SampleImpl_.lastModified);
      default:
        return null;
    }
  }

  @Override
  public SingularAttribute<SampleImpl, ? extends UserImpl> propertyForUser(boolean creator) {
    return creator ? SampleImpl_.creator : SampleImpl_.lastModifier;
  }

  @Override
  public String getFriendlyName() {
    return "Sample";
  }

  @Override
  public List<SingularAttribute<? super SampleImpl, String>> getIdentifierProperties() {
    return IDENTIFIER_PROPERTIES;
  }

  @Override
  public List<Path<String>> getSearchProperties(Root<SampleImpl> root) {
    return Arrays.asList(root.get(SampleImpl_.alias), root.get(SampleImpl_.identificationBarcode),
        root.get(SampleImpl_.name));
  }

  @Override
  public long getChildSampleCount(Sample sample) {
    LongQueryBuilder<DetailedSampleImpl> builder = new LongQueryBuilder<>(currentSession(), DetailedSampleImpl.class);
    builder.addPredicate(
        sample == null ? builder.getCriteriaBuilder().isNull(builder.getRoot().get(DetailedSampleImpl_.parent))
            : builder.getCriteriaBuilder().equal(builder.getRoot().get(DetailedSampleImpl_.parent), sample));
    return builder.getCount();
  }

  @Override
  public EntityReference getNextInProject(Sample sample) {
    QueryBuilder<EntityReference, SampleImpl> builder =
        new QueryBuilder<>(currentSession(), SampleImpl.class, EntityReference.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleImpl_.project), sample.getProject()));
    builder.addPredicate(
        builder.getCriteriaBuilder().greaterThan(builder.getRoot().get(SampleImpl_.sampleId), sample.getId()));
    builder.addSort(builder.getRoot().get(SampleImpl_.sampleId), true);
    builder.setColumns(builder.getRoot().get(SampleImpl_.sampleId), builder.getRoot().get(SampleImpl_.name));
    List<EntityReference> result = builder.getResultList(1, 0);
    return result.isEmpty() ? null : result.get(0);
  }

  @Override
  public EntityReference getPreviousInProject(Sample sample) {
    QueryBuilder<EntityReference, SampleImpl> builder =
        new QueryBuilder<>(currentSession(), SampleImpl.class, EntityReference.class);
    builder.addPredicate(
        builder.getCriteriaBuilder().equal(builder.getRoot().get(SampleImpl_.project), sample.getProject()));
    builder.addPredicate(
        builder.getCriteriaBuilder().lessThan(builder.getRoot().get(SampleImpl_.sampleId), sample.getId()));
    builder.addSort(builder.getRoot().get(SampleImpl_.sampleId), false);
    builder.setColumns(builder.getRoot().get(SampleImpl_.sampleId), builder.getRoot().get(SampleImpl_.name));
    List<EntityReference> result = builder.getResultList(1, 0);
    return result.isEmpty() ? null : result.get(0);
  }

  @Override
  public List<Sample> getChildren(Collection<Long> parentIds, String targetSampleCategory, long effectiveRequisitionId)
      throws IOException {
    Set<Long> childIds = getChildIds(parentIds, targetSampleCategory, effectiveRequisitionId);
    return listByIdList(new ArrayList<>(childIds));
  }

  @Override
  public Set<Long> getChildIds(Collection<Long> parentIds, String targetSampleCategory, Long effectiveRequisitionId)
      throws IOException {
    if (parentIds == null || parentIds.isEmpty()) {
      return Collections.emptySet();
    }
    Set<Long> output = new HashSet<>();

    QueryBuilder<Object[], SampleImpl> builder = new QueryBuilder<>(currentSession(), SampleImpl.class, Object[].class);
    Join<DetailedSampleImpl, DetailedSampleImpl> parent =
        builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parent, JoinType.INNER);
    Join<DetailedSampleImpl, SampleClassImpl> sampleClass =
        builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.sampleClass, JoinType.INNER);
    Join<SampleImpl, Requisition> requisition =
        builder.getJoin(builder.getRoot(), SampleImpl_.requisition);
    builder.addInPredicate(parent.get(DetailedSampleImpl_.sampleId), parentIds);
    builder.setColumns(builder.getRoot().get(SampleImpl_.sampleId), sampleClass.get(SampleClassImpl_.sampleCategory));

    if (effectiveRequisitionId != null) {
      builder.addPredicate(builder.getCriteriaBuilder().or(
          builder.getCriteriaBuilder().equal(requisition.get(Requisition_.requisitionId), effectiveRequisitionId),
          builder.getCriteriaBuilder().isNull(builder.getRoot().get(SampleImpl_.requisition))));
    }
    List<Object[]> results = builder.getResultList();

    int targetIndex = SAMPLE_CATEGORIES.indexOf(targetSampleCategory);
    Set<Long> nextParents = new HashSet<>();
    for (Object[] result : results) {
      Long childId = (Long) result[0];
      String childCategory = (String) result[1];
      if (childCategory.equals(targetSampleCategory)) {
        output.add(childId);
      }
      if (SAMPLE_CATEGORIES.indexOf(childCategory) <= targetIndex) {
        nextParents.add(childId);
      }
    }
    if (!nextParents.isEmpty()) {
      output.addAll(getChildIds(nextParents, targetSampleCategory, effectiveRequisitionId));
    }
    return output;
  }

  @Override
  public void restrictPaginationByProjectId(QueryBuilder<?, SampleImpl> builder, long projectId,
      Consumer<String> errorHandler) {
    Join<SampleImpl, ProjectImpl> project = builder.getJoin(builder.getRoot(), SampleImpl_.project);
    builder.addPredicate(builder.getCriteriaBuilder().equal(project.get(ProjectImpl_.id), projectId));
  }

  @Override
  public void restrictPaginationByExternalName(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<DetailedSampleImpl, ParentAttributes> parentAttributes =
        builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parentAttributes);
    Join<ParentAttributes, ParentIdentityAttributes> identityAttributes =
        builder.getJoin(parentAttributes, ParentAttributes_.identityAttributes);
    Predicate externalNamePredicate =
        builder.makeTextRestriction(identityAttributes.get(ParentIdentityAttributes_.externalName), query);

    SubqueryBuilder<Long, SampleTissueImpl> subqueryBuilder = builder.makeSubquery(SampleTissueImpl.class, Long.class);
    Root<SampleTissueImpl> subqueryRoot = subqueryBuilder.getRoot();
    subqueryBuilder.setColumn(subqueryRoot.get(SampleTissueImpl_.sampleId));
    subqueryBuilder
        .addPredicate(builder.makeTextRestriction(subqueryRoot.get(SampleTissueImpl_.secondaryIdentifier), query));
    Predicate secondaryIdentifierPredicate =
        builder.makeInPredicate(builder.getRoot().get(SampleImpl_.sampleId), subqueryBuilder);

    builder.addPredicate(builder.getCriteriaBuilder().or(externalNamePredicate, secondaryIdentifierPredicate));
  }

  @Override
  public void restrictPaginationByLab(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<SampleTissueImpl, LabImpl> lab =
        builder.getJoin(builder.getRoot(SampleTissueImpl.class), SampleTissueImpl_.lab);
    builder.addTextRestriction(lab.get(LabImpl_.alias), query);
  }

  @Override
  public void restrictPaginationByGroupId(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot(DetailedSampleImpl.class).get(DetailedSampleImpl_.groupId), query);
  }

  @Override
  public void restrictPaginationByGhost(QueryBuilder<?, SampleImpl> builder, boolean isGhost,
      Consumer<String> errorHandler) {
    builder.addPredicate(builder.getCriteriaBuilder()
        .equal(builder.getRoot(DetailedSampleImpl.class).get(DetailedSampleImpl_.isSynthetic), isGhost));
  }

  @Override
  public void restrictPaginationByClass(QueryBuilder<?, SampleImpl> builder, String name,
      Consumer<String> errorHandler) {
    Join<DetailedSampleImpl, SampleClassImpl> sampleClass =
        builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.sampleClass, JoinType.INNER);
    builder.addTextRestriction(sampleClass.get(SampleClassImpl_.alias), name);
  }

  @Override
  public void restrictPaginationByArrayed(QueryBuilder<?, SampleImpl> builder, boolean isArrayed,
      Consumer<String> errorHandler) {
    QueryBuilder<Long, Array> idBuilder = new QueryBuilder<>(currentSession(), Array.class, Long.class);
    Join<Array, SampleImpl> sample = idBuilder.getJoin(idBuilder.getRoot(), Array_.samples, JoinType.INNER);
    idBuilder.setColumn(sample.get(SampleImpl_.sampleId));
    List<Long> ids = idBuilder.getResultList();
    if (isArrayed) {
      builder.addInPredicate(builder.getRoot().get(SampleImpl_.sampleId), ids);
    } else {
      builder.addNotInPredicate(builder.getRoot().get(SampleImpl_.sampleId), ids);
    }
  }

  @Override
  public void restrictPaginationByRequisitionId(QueryBuilder<?, SampleImpl> builder, long requisitionId,
      Consumer<String> errorHandler) {
    Join<SampleImpl, Requisition> requisition =
        builder.getJoin(builder.getRoot(), SampleImpl_.requisition, JoinType.INNER);
    builder
        .addPredicate(builder.getCriteriaBuilder().equal(requisition.get(Requisition_.requisitionId), requisitionId));
  }

  @Override
  public void restrictPaginationByRequisition(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<SampleImpl, Requisition> requisition =
        builder.getJoin(builder.getRoot(), SampleImpl_.requisition, JoinType.INNER);
    builder.addPredicate(builder.getCriteriaBuilder().equal(requisition.get(Requisition_.alias), query));
  }

  @Override
  public void restrictPaginationBySupplementalToRequisitionId(QueryBuilder<?, SampleImpl> builder, long requisitionId,
      Consumer<String> errorHandler) {
    QueryBuilder<Long, RequisitionSupplementalSample> idBuilder =
        new QueryBuilder<>(currentSession(), RequisitionSupplementalSample.class, Long.class);
    Join<RequisitionSupplementalSample, SampleImpl> sample =
        idBuilder.getJoin(idBuilder.getRoot(), RequisitionSupplementalSample_.sample, JoinType.INNER);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder()
        .equal(idBuilder.getRoot().get(RequisitionSupplementalSample_.requisitionId), requisitionId));
    idBuilder.setColumn(sample.get(SampleImpl_.sampleId));
    List<Long> ids = idBuilder.getResultList();

    builder.addInPredicate(builder.getRoot().get(SampleImpl_.sampleId), ids);
  }

  @Override
  public void restrictPaginationBySubproject(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      builder.addPredicate(builder.getCriteriaBuilder()
          .isNull(builder.getRoot(DetailedSampleImpl.class).get(DetailedSampleImpl_.subproject)));
    } else {
      Join<DetailedSampleImpl, SubprojectImpl> subproject =
          builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.subproject, JoinType.INNER);
      builder.addTextRestriction(subproject.get(SubprojectImpl_.alias), query);
    }
  }

  @Override
  public void restrictPaginationByTissueOrigin(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<DetailedSampleImpl, ParentAttributes> parentAttributes =
        builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parentAttributes);
    Join<ParentAttributes, ParentTissueAttributes> tissueAttributes =
        builder.getJoin(parentAttributes, ParentAttributes_.tissueAttributes);
    Join<ParentTissueAttributes, TissueOriginImpl> tissueOrigin =
        builder.getJoin(tissueAttributes, ParentTissueAttributes_.tissueOrigin);
    builder.addTextRestriction(tissueOrigin.get(TissueOriginImpl_.alias), query);
  }

  @Override
  public void restrictPaginationByTissueType(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    Join<DetailedSampleImpl, ParentAttributes> parentAttributes =
        builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parentAttributes);
    Join<ParentAttributes, ParentTissueAttributes> tissueAttributes =
        builder.getJoin(parentAttributes, ParentAttributes_.tissueAttributes);
    Join<ParentTissueAttributes, TissueTypeImpl> tissueType =
        builder.getJoin(tissueAttributes, ParentTissueAttributes_.tissueType);
    builder.addTextRestriction(tissueType.get(TissueTypeImpl_.alias), query);
  }

  @Override
  public void restrictPaginationByWorksetId(QueryBuilder<?, SampleImpl> builder, long worksetId,
      Consumer<String> errorHandler) {
    QueryBuilder<Long, Workset> idBuilder = new QueryBuilder<>(currentSession(), Workset.class, Long.class);
    Join<Workset, WorksetSample> worksetSample =
        idBuilder.getJoin(idBuilder.getRoot(), Workset_.worksetSamples, JoinType.INNER);
    Join<WorksetSample, SampleImpl> sample = idBuilder.getJoin(worksetSample, WorksetSample_.item, JoinType.INNER);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(idBuilder.getRoot().get(Workset_.id), worksetId));
    idBuilder.setColumn(sample.get(SampleImpl_.sampleId));
    List<Long> ids = idBuilder.getResultList();

    builder.addInPredicate(builder.getRoot().get(SampleImpl_.sampleId), ids);
  }

  @Override
  public void restrictPaginationByTimepoint(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addTextRestriction(builder.getRoot(SampleTissueImpl.class).get(SampleTissueImpl_.timepoint), query);
  }

  @Override
  public void restrictPaginationByDistributionRecipient(QueryBuilder<?, SampleImpl> builder, String query,
      Consumer<String> errorHandler) {
    builder.addDistributionRecipientPredicate(query, ListTransferView_.SAMPLES, ListTransferViewSample_.SAMPLE_ID,
        SampleImpl_.SAMPLE_ID);
  }

  @Override
  public void restrictPaginationByIdentityIds(QueryBuilder<?, SampleImpl> builder, List<Long> identityIds,
      Consumer<String> errorHandler) {
    Join<DetailedSampleImpl, ParentAttributes> parentAttributes =
        builder.getJoin(builder.getRoot(DetailedSampleImpl.class), DetailedSampleImpl_.parentAttributes);
    Join<ParentAttributes, ParentIdentityAttributes> identityAttributes =
        builder.getJoin(parentAttributes, ParentAttributes_.identityAttributes);
    builder.addInPredicate(identityAttributes.get(ParentIdentityAttributes_.sampleId), identityIds);
  }

}
