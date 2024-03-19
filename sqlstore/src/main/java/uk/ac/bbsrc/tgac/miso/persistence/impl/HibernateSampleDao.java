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

import javax.persistence.TemporalType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissueProcessing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedSampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSampleDao implements SampleStore, HibernatePaginatedBoxableSource<Sample> {

  private static final String[] SEARCH_PROPERTIES = new String[] {"alias", "identificationBarcode", "name"};
  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(
      new AliasDescriptor("parentAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("parentAttributes.identityAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("parentAttributes.tissueAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueOrigin", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueType", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("sampleClass", JoinType.LEFT_OUTER_JOIN));

  private static final List<String> SAMPLE_CATEGORIES =
      Arrays.asList(SampleIdentity.CATEGORY_NAME, SampleTissue.CATEGORY_NAME,
          SampleTissueProcessing.CATEGORY_NAME, SampleStock.CATEGORY_NAME, SampleAliquot.CATEGORY_NAME);

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  @Autowired
  private SessionFactory sessionFactory;

  public void setDetailedSample(boolean detailedSample) {
    this.detailedSample = detailedSample;
  }

  @Override
  public Long create(final Sample sample) throws IOException {
    return (Long) currentSession().save(sample);
  }

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public Sample get(long id) throws IOException {
    return getSample(id);
  }

  @Override
  public Sample getByBarcode(String barcode) throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (Sample) criteria.uniqueResult();
  }

  @Override
  public Collection<Sample> listByBarcodeList(Collection<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public List<Sample> listByIdList(Collection<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    criteria.add(Restrictions.in("sampleId", idList));
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public Sample getByLibraryAliquotId(long aliquotId) {
    DetachedCriteria subquery = DetachedCriteria.forClass(LibraryAliquot.class)
        .createAlias("library", "library")
        .createAlias("library.sample", "sample")
        .add(Restrictions.eq("id", aliquotId))
        .setProjection(Projections.property("sample.sampleId"));

    return (Sample) currentSession().createCriteria(SampleImpl.class)
        .add(Property.forName("id").eq(subquery))
        .uniqueResult();
  }

  @Override
  public List<Sample> list() throws IOException {
    Criteria criteria = currentSession().createCriteria(SampleImpl.class);
    @SuppressWarnings("unchecked")
    List<Sample> records = criteria.list();
    return records;
  }

  @Override
  public Sample getSample(long id) throws IOException {
    return (Sample) currentSession().get(SampleImpl.class, id);
  }

  @Override
  public List<EntityReference> listByAlias(String alias) throws IOException {
    @SuppressWarnings("unchecked")
    List<EntityReference> results = currentSession().createCriteria(SampleImpl.class)
        .add(Restrictions.eq("alias", alias))
        .setProjection(EntityReference.makeProjectionList("id", "alias"))
        .setResultTransformer(EntityReference.RESULT_TRANSFORMER)
        .list();
    return results;
  }

  @Override
  public long update(Sample sample) throws IOException {
    currentSession().update(sample);
    return sample.getId();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public List<IdentityView> getIdentitiesByExternalNameOrAliasAndProject(String query, Long projectId,
      boolean exactMatch)
      throws IOException {
    if (isStringEmptyOrNull(query)) {
      return Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<IdentityView> records = (List<IdentityView>) SampleIdentityImpl.getSetFromString(query)
        .stream().map(extNameOrAlias -> {
          Criteria criteria = currentSession().createCriteria(IdentityView.class)
              .add(Restrictions.eq("discriminator", SampleIdentity.CATEGORY_NAME));
          if (projectId != null) {
            criteria.add(Restrictions.eq("projectId", projectId));
          }
          criteria.add(DbUtils.textRestriction(extNameOrAlias, "externalName", "alias"));
          return criteria.list();
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
    Criteria criteria = currentSession().createCriteria(IdentityView.class);
    if (project != null) {
      criteria.add(Restrictions.eq("projectId", project.getId()));
    }
    Disjunction disjunction = Restrictions.disjunction();
    for (String externalName : externalNames) {
      disjunction.add(Restrictions.ilike("externalName", externalName, MatchMode.ANYWHERE));
    }
    criteria.add(disjunction);
    @SuppressWarnings("unchecked")
    List<IdentityView> results = criteria.list();
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
    Criteria criteria = currentSession().createCriteria(SampleTissueImpl.class);
    criteria.add(Restrictions.eq("isSynthetic", true));
    criteria.add(Restrictions.eq("parent.id", tissue.getParent().getId()));
    criteria.add(Restrictions.eq("tissueOrigin.id", tissue.getTissueOrigin().getId()));
    criteria.add(Restrictions.eq("tissueType.id", tissue.getTissueType().getId()));
    criteria.add(eqNullable("timesReceived", tissue.getTimesReceived()));
    criteria.add(eqNullable("tubeNumber", tissue.getTubeNumber()));
    criteria.add(eqNullable("passageNumber", tissue.getPassageNumber()));
    criteria.add(eqNullable("timepoint", tissue.getTimepoint()));
    return (SampleTissue) criteria.uniqueResult();
  }

  private Criterion eqNullable(String propertyName, Object value) {
    return value == null ? Restrictions.isNull(propertyName) : Restrictions.eq(propertyName, value);
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
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
      case "effectiveExternalNames":
        return "identityAttributes.externalName";
      case "effectiveTissueOriginAlias":
        return "tissueOrigin.alias";
      case "effectiveTissueTypeAlias":
        return "tissueType.alias";
      case "sampleClassId":
        return "sampleClass.alias";
      default:
        return original;
    }
  }

  @Override
  public Class<? extends Sample> getRealClass() {
    return SampleImpl.class;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
      case CREATE:
        return detailedSample ? "creationDate" : null;
      case ENTERED:
        return "creationTime";
      case UPDATE:
        return "lastModified";
      default:
        return null;
    }
  }

  @Override
  public TemporalType temporalTypeForDate(DateType type) {
    switch (type) {
      case CREATE:
        return TemporalType.DATE;
      case ENTERED:
      case UPDATE:
        return TemporalType.TIMESTAMP;
      default:
        return null;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public String getFriendlyName() {
    return "Sample";
  }

  @Override
  public String[] getIdentifierProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public long getChildSampleCount(Sample sample) {
    return (long) currentSession().createCriteria(DetailedSampleImpl.class)
        .add(Restrictions.eqOrIsNull("parent", sample))
        .setProjection(Projections.rowCount())
        .uniqueResult();
  }

  @Override
  public EntityReference getNextInProject(Sample sample) {
    return (EntityReference) currentSession().createCriteria(SampleImpl.class)
        .add(Restrictions.eq("project", sample.getProject()))
        .add(Restrictions.gt("sampleId", sample.getId()))
        .addOrder(Order.asc("sampleId"))
        .setMaxResults(1)
        .setProjection(EntityReference.makeProjectionList("id", "name"))
        .setResultTransformer(EntityReference.RESULT_TRANSFORMER)
        .uniqueResult();
  }

  @Override
  public EntityReference getPreviousInProject(Sample sample) {
    return (EntityReference) currentSession().createCriteria(SampleImpl.class)
        .add(Restrictions.eq("project", sample.getProject()))
        .add(Restrictions.lt("sampleId", sample.getId()))
        .addOrder(Order.desc("sampleId"))
        .setMaxResults(1)
        .setProjection(EntityReference.makeProjectionList("id", "name"))
        .setResultTransformer(EntityReference.RESULT_TRANSFORMER)
        .uniqueResult();
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
    Criteria criteria = currentSession().createCriteria(SampleImpl.class)
        .createAlias("parent", "parent")
        .createAlias("sampleClass", "sampleClass")
        .add(Restrictions.in("parent.sampleId", parentIds))
        .setProjection(
            Projections.projectionList()
                .add(Projections.property("sampleId"))
                .add(Projections.property("sampleClass.sampleCategory")));
    if (effectiveRequisitionId != null) {
      criteria = criteria.createAlias(SampleImpl_.REQUISITION, "requisition", JoinType.LEFT_OUTER_JOIN)
          .add(Restrictions.or(Restrictions.eq("requisition.id", effectiveRequisitionId),
              Restrictions.isNull("requisition")));
    }
    @SuppressWarnings("unchecked")
    List<Object[]> results = criteria.list();

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
  public void restrictPaginationByExternalName(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "externalName", "secondaryIdentifier"));
  }

  @Override
  public void restrictPaginationByLab(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("lab", "lab", JoinType.LEFT_OUTER_JOIN);
    criteria.add(DbUtils.textRestriction(query, "lab.alias"));
  }

  @Override
  public void restrictPaginationByGroupId(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "groupId"));
  }

  @Override
  public void restrictPaginationByGhost(Criteria criteria, boolean isGhost, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("isSynthetic", isGhost));
  }

  @Override
  public void restrictPaginationByClass(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.createAlias("sampleClass", "sampleClass");
    criteria.add(DbUtils.textRestriction(name, "sampleClass.alias"));
  }

  @Override
  public void restrictPaginationByArrayed(Criteria criteria, boolean isArrayed, Consumer<String> errorHandler) {
    DetachedCriteria subquery = DetachedCriteria.forClass(Array.class)
        .createAlias("samples", "sample")
        .setProjection(Projections.property("sample.id"));
    if (isArrayed) {
      criteria.add(Property.forName("id").in(subquery));
    } else {
      criteria.add(Property.forName("id").notIn(subquery));
    }
  }

  @Override
  public void restrictPaginationByRequisitionId(Criteria criteria, long requisitionId, Consumer<String> errorHandler) {
    criteria.createAlias("requisition", "requisition")
        .add(Restrictions.eq("requisition.requisitionId", requisitionId));
  }

  @Override
  public void restrictPaginationByRequisition(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("requisition", "requisition")
        .add(DbUtils.textRestriction(query, "requisition.alias"));
  }

  @Override
  public void restrictPaginationBySupplementalToRequisitionId(Criteria criteria, long requisitionId,
      Consumer<String> errorHandler) {
    DetachedCriteria subquery = DetachedCriteria.forClass(RequisitionSupplementalSample.class)
        .createAlias("sample", "sample")
        .add(Restrictions.eq("requisitionId", requisitionId))
        .setProjection(Projections.property("sample.id"));
    criteria.add(Property.forName("id").in(subquery));
  }

  @Override
  public void restrictPaginationBySubproject(Criteria criteria, String query, Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.add(Restrictions.isNull("subproject"));
    } else {
      criteria.createAlias("subproject", "subproject");
      criteria.add(DbUtils.textRestriction(query, "subproject.alias"));
    }
  }

  @Override
  public void restrictPaginationByTissueOrigin(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "tissueOrigin.alias"));
  }

  @Override
  public void restrictPaginationByTissueType(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "tissueType.alias"));
  }

  @Override
  public void restrictPaginationByWorksetId(Criteria criteria, long worksetId, Consumer<String> errorHandler) {
    DetachedCriteria subquery = DetachedCriteria.forClass(Workset.class)
        .createAlias("worksetSamples", "worksetSample")
        .createAlias("worksetSample.item", "sample")
        .add(Restrictions.eq("id", worksetId))
        .setProjection(Projections.property("sample.id"));
    criteria.add(Property.forName("id").in(subquery));
  }

  @Override
  public void restrictPaginationByTimepoint(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "timepoint"));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    DbUtils.restrictPaginationByDistributionRecipient(criteria, query, "samples", "sampleId");
  }

  @Override
  public void restrictPaginationByIdentityIds(Criteria criteria, List<Long> identityIds,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.in("identityAttributes.id", identityIds));
  }

}
