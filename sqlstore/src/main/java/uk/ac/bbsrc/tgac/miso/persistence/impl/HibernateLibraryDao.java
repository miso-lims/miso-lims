package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.persistence.TemporalType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryBatch;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RequisitionSupplementalSample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStore;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDao implements LibraryStore, HibernatePaginatedBoxableSource<Library> {

  private interface AdjacencySelector {
    Criterion generateCriterion(String associationPath, Long libraryId);

    Order getOrder(String associationPath);
  }

  private static final AdjacencySelector BEFORE = new AdjacencySelector() {

    @Override
    public Criterion generateCriterion(String associationPath, Long libraryId) {
      return Restrictions.lt(associationPath, libraryId);
    }

    @Override
    public Order getOrder(String associationPath) {
      return Order.desc(associationPath);
    }

  };

  private static final AdjacencySelector AFTER = new AdjacencySelector() {

    @Override
    public Criterion generateCriterion(String associationPath, Long libraryId) {
      return Restrictions.gt(associationPath, libraryId);
    }

    @Override
    public Order getOrder(String associationPath) {
      return Order.asc(associationPath);
    }

  };

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private SampleStore sampleStore;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSample;

  public void setDetailedSample(boolean detailedSample) {
    this.detailedSample = detailedSample;
  }

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  private final static String[] IDENTIFIER_FIELDS = {"name", "alias", "identificationBarcode"};
  private final static String[] SEARCH_FIELDS = {"name", "alias", "description", "identificationBarcode"};
  private final static List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("sample"),
      new AliasDescriptor("sample.parentAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("parentAttributes.tissueAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueOrigin", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueType", JoinType.LEFT_OUTER_JOIN));

  @Override
  public long save(Library library) throws IOException {
    if (!library.isSaved()) {
      return (long) currentSession().save(library);
    } else {
      currentSession().update(library);
      return library.getId();
    }
  }

  @Override
  public Library get(long id) throws IOException {
    return (Library) currentSession().get(LibraryImpl.class, id);
  }

  @Override
  public List<Library> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public List<EntityReference> listByAlias(String alias) throws IOException {
    @SuppressWarnings("unchecked")
    List<EntityReference> results = currentSession().createCriteria(LibraryImpl.class)
        .add(Restrictions.eq("alias", alias))
        .setProjection(EntityReference.makeProjectionList("id", "alias"))
        .setResultTransformer(EntityReference.RESULT_TRANSFORMER)
        .list();
    return results;
  }

  @Override
  public List<Library> listBySampleId(long sampleId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("sample.id", sampleId));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public List<Library> listByProjectId(long projectId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.createAlias("sample.project", "project");
    criteria.add(Restrictions.eq("project.id", projectId));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public List<Library> listByIdList(List<Long> idList) throws IOException {
    if (idList == null || idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.in("id", idList));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public EntityReference getAdjacentLibrary(Library lib, boolean before) throws IOException {
    AdjacencySelector selector = before ? BEFORE : AFTER;

    // get library siblings
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.createAlias("sample", "sample");
    criteria.add(Restrictions.eq("sample.id", lib.getSample().getId()));
    criteria.add(selector.generateCriterion("id", lib.getId()));
    criteria.addOrder(selector.getOrder("id"));
    criteria.setMaxResults(1);
    criteria.setProjection(EntityReference.makeProjectionList("id", "alias"));
    criteria.setResultTransformer(EntityReference.RESULT_TRANSFORMER);
    EntityReference library = (EntityReference) criteria.uniqueResult();
    if (library != null)
      return library;

    // get library cousins
    criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.createAlias("sample", "sample");
    criteria.createAlias("sample.project", "project");
    criteria.add(Restrictions.eq("project.id", lib.getSample().getProject().getId()));
    criteria.add(selector.generateCriterion("id", lib.getId()));
    criteria.addOrder(selector.getOrder("sample.id"));
    criteria.addOrder(selector.getOrder("id"));
    criteria.setMaxResults(1);
    criteria.setProjection(EntityReference.makeProjectionList("id", "alias"));
    criteria.setResultTransformer(EntityReference.RESULT_TRANSFORMER);
    library = (EntityReference) criteria.uniqueResult();
    return library;
  }

  @Override
  public List<Long> listIdsByRequisitionId(long requisitionId) throws IOException {
    List<Long> requisitionSampleIds = currentSession().createCriteria(Sample.class)
        .createAlias("requisition", "requisition")
        .add(Restrictions.eq("requisition.id", requisitionId))
        .setProjection(Projections.property("id"))
        .list();
    requisitionSampleIds.addAll(currentSession().createCriteria(RequisitionSupplementalSample.class)
        .createAlias("sample", "sample")
        .add(Restrictions.eq("requisitionId", requisitionId))
        .setProjection(Projections.property("sample.id"))
        .list());
    if (requisitionSampleIds.isEmpty()) {
      return Collections.emptyList();
    }

    Set<Long> aliquotSampleIds = sampleStore.getChildIds(requisitionSampleIds, SampleAliquot.CATEGORY_NAME);

    Set<Long> parentIds = new HashSet<>(aliquotSampleIds);
    parentIds.addAll(requisitionSampleIds);

    List<Long> results = currentSession().createCriteria(LibraryImpl.class)
        .createAlias("sample", "sample")
        .add(Restrictions.in("sample.id", parentIds))
        .setProjection(Projections.property("id"))
        .list();
    return results;
  }


  public List<Long> listIdsByAncestorSampleIdList(Collection<Long> sampleIds) throws IOException {
    Set<Long> aliquotSampleIds = sampleStore.getChildIds(sampleIds, SampleAliquot.CATEGORY_NAME);

    Set<Long> parentIds = new HashSet<>(aliquotSampleIds);
    parentIds.addAll(sampleIds);

    List<Long> results = currentSession().createCriteria(LibraryImpl.class)
        .createAlias("sample", "sample")
        .add(Restrictions.in("sample.id", parentIds))
        .setProjection(Projections.property("id"))
        .list();
    return results;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public String getProjectColumn() {
    return "sample.project.id";
  }

  @Override
  public String[] getIdentifierProperties() {
    return IDENTIFIER_FIELDS;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_FIELDS;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
      case "parentSampleId":
        return "sample.id";
      case "parentSampleAlias":
        return "sample.alias";
      case "effectiveTissueOriginAlias":
        return "tissueOrigin.alias";
      case "effectiveTissueTypeAlias":
        return "tissueType.alias";
      default:
        return original;
    }
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
      case CREATE:
        return "creationDate";
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
  public Class<? extends Library> getRealClass() {
    return LibraryImpl.class;
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String query, Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.add(Restrictions.isNull("index1"));
    } else {
      criteria.createAlias("index1", "index1")
          .createAlias("index2", "index2", JoinType.LEFT_OUTER_JOIN)
          .add(DbUtils.textRestriction(query, "index1.name", "index1.sequence", "index2.name", "index2.sequence"));
    }
  }

  @Override
  public void restrictPaginationByKitName(Criteria criteria, String query, Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.add(Restrictions.isNull("kitDescriptor"));
    } else {
      criteria.createAlias("kitDescriptor", "kitDescriptor");
      criteria.add(DbUtils.textRestriction(query, "kitDescriptor.name"));
    }
  }

  @Override
  public void restrictPaginationByGroupId(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "groupId"));
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
  public String getFriendlyName() {
    return "Library";
  }

  @Override
  public void restrictPaginationByWorksetId(Criteria criteria, long worksetId, Consumer<String> errorHandler) {
    DetachedCriteria subquery = DetachedCriteria.forClass(Workset.class)
        .createAlias("worksetLibraries", "worksetLibrary")
        .createAlias("worksetLibrary.item", "library")
        .add(Restrictions.eq("id", worksetId))
        .setProjection(Projections.property("library.id"));
    criteria.add(Property.forName("id").in(subquery));
  }

  @Override
  public void restrictPaginationByBatchId(Criteria criteria, String batchId, Consumer<String> errorHandler) {
    LibraryBatch batch = null;
    try {
      batch = new LibraryBatch(batchId);
    } catch (IllegalArgumentException e) {
      errorHandler.accept("Invalid batch ID");
      return;
    }
    criteria.createAlias("creator", "creator")
        .createAlias("sop", "sop")
        .createAlias("kitDescriptor", "kitDescriptor")
        .add(Restrictions.eq("creationDate", batch.getDate()))
        .add(Restrictions.eq("creator.id", batch.getUserId()))
        .add(Restrictions.eq("sop.id", batch.getSopId()))
        .add(Restrictions.eq("kitDescriptor.id", batch.getKitId()))
        .add(Restrictions.eq("kitLot", batch.getKitLot()));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    DbUtils.restrictPaginationByDistributionRecipient(criteria, query, "libraries", "libraryId");
  }

  @Override
  public void restrictPaginationByWorkstation(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("workstation", "workstation")
        .add(DbUtils.textRestriction(query, "workstation.alias", "workstation.identificationBarcode"));
  }

}
