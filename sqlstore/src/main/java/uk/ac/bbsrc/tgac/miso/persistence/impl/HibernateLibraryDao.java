package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryBatch;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.EntityReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.BoxStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStore;
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

  protected static final Logger log = LoggerFactory.getLogger(HibernateLibraryDao.class);

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private BoxStore boxDao;
  @Value("${miso.detailed.sample.enabled:false}")
  private boolean detailedSampleEnabled;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  private final static String[] SEARCH_FIELDS = new String[] { "name", "alias", "description", "identificationBarcode" };
  private final static List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("sample"),
      new AliasDescriptor("sample.parentAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("parentAttributes.tissueAttributes", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueOrigin", JoinType.LEFT_OUTER_JOIN),
      new AliasDescriptor("tissueAttributes.tissueType", JoinType.LEFT_OUTER_JOIN));

  @Override
  public long save(Library library) throws IOException {
    long id;
    if (library.getId() == LibraryImpl.UNSAVED_ID) {
      id = (long) currentSession().save(library);
    } else {
      if (library.isDiscarded()) {
        getBoxDao().removeBoxableFromBox(library);
      }
      currentSession().update(library);
      id = library.getId();
    }
    return id;
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
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public Library getByBarcode(String barcode) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (Library) criteria.uniqueResult();
  }

  @Override
  public List<Library> listBySearch(String query) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(DbUtils.searchRestrictions(query, false, SEARCH_FIELDS));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public List<Library> listByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
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
  public List<Library> getByIdList(List<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.in("id", idList));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public Boxable getByPositionId(long positionId) {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("boxPositionId", positionId));
    return (Library) criteria.uniqueResult();
  }

  @Override
  public List<Library> getByBarcodeList(Collection<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public LibraryType getLibraryTypeById(long libraryTypeId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryType.class);
    criteria.add(Restrictions.eq("id", libraryTypeId));
    return (LibraryType) criteria.uniqueResult();
  }

  @Override
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryType.class);
    criteria.add(Restrictions.and(Restrictions.eq("description", description), Restrictions.eq("platformType", platformType)));
    return (LibraryType) criteria.uniqueResult();
  }

  @Override
  public List<LibraryType> listAllLibraryTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryType.class);
    @SuppressWarnings("unchecked")
    List<LibraryType> records = criteria.list();
    return records;
  }

  @Override
  public List<LibraryType> listLibraryTypesByPlatform(PlatformType platformType) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryType.class);
    criteria.add(Restrictions.eq("platformType", platformType));
    @SuppressWarnings("unchecked")
    List<LibraryType> records = criteria.list();
    return records;
  }

  @Override
  public long countLibrariesBySearch(String querystr) throws IOException {
    if (isStringEmptyOrNull(querystr)) {
      return count();
    } else {
      Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
      criteria.add(DbUtils.searchRestrictions(querystr, false, SEARCH_FIELDS));
      return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }
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
    if (library != null) return library;

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
  public List<Library> searchByCreationDate(Date from, Date to) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.ge("creationDate", from));
    criteria.add(Restrictions.le("creationDate", to));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public Library getByPreMigrationId(Long preMigrationId) throws IOException {
    if (!detailedSampleEnabled) return null;
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("preMigrationId", preMigrationId));
    return (Library) criteria.uniqueResult();
  }

  @Override
  public List<LibrarySpikeIn> listSpikeIns() throws IOException {
    @SuppressWarnings("unchecked")
    List<LibrarySpikeIn> results = currentSession().createCriteria(LibrarySpikeIn.class).list();
    return results;
  }

  @Override
  public LibrarySpikeIn getSpikeIn(long spikeInId) throws IOException {
    return (LibrarySpikeIn) currentSession().get(LibrarySpikeIn.class, spikeInId);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public BoxStore getBoxDao() {
    return boxDao;
  }

  public void setBoxDao(BoxStore boxDao) {
    this.boxDao = boxDao;
  }

  public void setDetailedSampleEnabled(boolean detailedSampleEnabled) {
    this.detailedSampleEnabled = detailedSampleEnabled;
  }

  @Override
  public String getProjectColumn() {
    return "sample.project.id";
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
    case "effectiveTissueOriginLabel":
      return "tissueOrigin.alias";
    case "effectiveTissueTypeLabel":
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
    case RECEIVE:
      return "receivedDate";
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
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("indices", "indices");
    restrictPaginationByIndices(criteria, index);
  }

  @Override
  public void restrictPaginationByKitName(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.createAlias("kitDescriptor", "kitDescriptor");
    criteria.add(Restrictions.ilike("kitDescriptor.name", name, MatchMode.START));
  }

  public static void restrictPaginationByIndices(Criteria criteria, String index) {
    criteria.add(Restrictions.or(Restrictions.ilike("indices.name", index, MatchMode.ANYWHERE),
        Restrictions.ilike("indices.sequence", index, MatchMode.EXACT)));
  }

  @Override
  public void restrictPaginationByGroupId(Criteria criteria, String groupId, Consumer<String> errorHandler) {
    criteria.add(Restrictions.ilike("groupId", groupId, MatchMode.EXACT));
  }

  @Override
  public void restrictPaginationByTissueOrigin(Criteria criteria, String origin, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("tissueOrigin.alias", origin));
  }

  @Override
  public void restrictPaginationByTissueType(Criteria criteria, String type, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("tissueType.alias", type));
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

}
