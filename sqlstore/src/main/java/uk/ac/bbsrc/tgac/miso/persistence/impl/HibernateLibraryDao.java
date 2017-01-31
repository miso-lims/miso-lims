package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDao implements LibraryStore {

  private interface AdjacencySelector {
    Criterion generateCriterion(String associationPath, Long libraryId);

    Criterion generateCriterion(String associationPathOne, String associationPathTwo);

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

    @Override
    public Criterion generateCriterion(String associationPathOne, String associationPathTwo) {
      return Restrictions.ltProperty(associationPathOne, associationPathTwo);
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

    @Override
    public Criterion generateCriterion(String associationPathOne, String associationPathTwo) {
      return Restrictions.gtProperty(associationPathOne, associationPathTwo);
    }

  };

  protected static final Logger log = LoggerFactory.getLogger(HibernateLibraryDao.class);

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private JdbcTemplate template;
  @Autowired
  private BoxStore boxDao;
  @Value("${miso.detailed.sample.enabled:false}")
  private boolean detailedSampleEnabled;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  private Criterion searchRestrictions(String querystr) {
    return DbUtils.searchRestrictions(querystr, "name", "alias", "description", "identificationBarcode");
  }

  @Override
  public long save(Library library) throws IOException {
    Date now = new Date();
    if (library.getCreationDate() == null) library.setCreationDate(now);
    long id;
    if (library.getId() == AbstractLibrary.UNSAVED_ID) {
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
  public boolean remove(Library library) throws IOException {
    if (library.isDeletable()) {
      currentSession().delete(library);
      Library testIfExists = get(library.getId());

      return testIfExists == null;
    } else {
      return false;
    }
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
    criteria.add(searchRestrictions(query));
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
  public List<Library> listAllWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.setMaxResults(((Long) limit).intValue());
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
  public List<Library> getByBarcodeList(List<String> barcodeList) throws IOException {
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
  public LibrarySelectionType getLibrarySelectionTypeById(long librarySelectionTypeId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibrarySelectionType.class);
    criteria.add(Restrictions.eq("id", librarySelectionTypeId));
    return (LibrarySelectionType) criteria.uniqueResult();
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibrarySelectionType.class);
    criteria.add(Restrictions.eq("name", name));
    return (LibrarySelectionType) criteria.uniqueResult();
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryStrategyType.class);
    criteria.add(Restrictions.eq("id", libraryStrategyTypeId));
    return (LibraryStrategyType) criteria.uniqueResult();
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryStrategyType.class);
    criteria.add(Restrictions.eq("name", name));
    return (LibraryStrategyType) criteria.uniqueResult();
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
  public List<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibrarySelectionType.class);
    @SuppressWarnings("unchecked")
    List<LibrarySelectionType> records = criteria.list();
    return records;
  }

  @Override
  public List<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryStrategyType.class);
    @SuppressWarnings("unchecked")
    List<LibraryStrategyType> records = criteria.list();
    return records;
  }

  @Override
  public Map<String, Integer> getLibraryColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, "Library");
  }

  @Override
  public List<Library> listBySearchOffsetAndNumResults(int offset, int limit, String querystr, String sortDir, String sortCol)
      throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(searchRestrictions(querystr));
    // required to sort by 'derivedInfo.lastModified', which is the field on which we
    // want to sort most List X pages
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(limit);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    criteria.setProjection(Projections.property("id"));
    @SuppressWarnings("unchecked")
    List<Long> ids = criteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    // We do this in two steps to make a smaller query that that the database can optimise
    Criteria query = currentSession().createCriteria(LibraryImpl.class);
    query.add(Restrictions.in("id", ids));
    query.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    query.createAlias("derivedInfo", "derivedInfo");
    @SuppressWarnings("unchecked")
    List<Library> records = query.list();
    return records;
  }

  @Override
  public List<Library> listByOffsetAndNumResults(int offset, int limit, String sortDir, String sortCol) throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    // required to sort by 'derivedInfo.lastModified', which is the field on which we
    // want to sort most List X pages
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(limit);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir.toLowerCase()) ? Order.asc(sortCol) : Order.desc(sortCol));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public long countLibrariesBySearch(String querystr) throws IOException {
    if (isStringEmptyOrNull(querystr)) {
      return count();
    } else {
      Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
      criteria.add(searchRestrictions(querystr));
      return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }
  }

  @Override
  public Library getAdjacentLibrary(long libraryId, boolean before) throws IOException {
    AdjacencySelector selector = before ? BEFORE : AFTER;

    // get library siblings
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.createAlias("sample.libraries", "targetLibrary", JoinType.INNER_JOIN);
    criteria.add(selector.generateCriterion("targetLibrary.id", libraryId));
    criteria.addOrder(selector.getOrder("id"));
    criteria.setMaxResults(1);
    Library library = (Library) criteria.uniqueResult();
    if (library != null) return library;

    // get library cousins
    criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.createAlias("sample.project.samples", "targetSample", JoinType.INNER_JOIN);
    criteria.add(Restrictions.eq("targetSample.libraries.id", libraryId));
    criteria.add(selector.generateCriterion("targetSample.id", "sample.id"));
    criteria.addOrder(selector.getOrder("sample.id"));
    criteria.addOrder(selector.getOrder("id"));
    criteria.setMaxResults(1);

    library = (Library) criteria.uniqueResult();
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

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @CoverageIgnore
  public void setTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public BoxStore getBoxDao() {
    return boxDao;
  }

  public void setBoxDao(BoxStore boxDao) {
    this.boxDao = boxDao;
  }

}
