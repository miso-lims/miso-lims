package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import static uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils.searchRestrictions;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

public class HibernateLibraryDao implements LibraryStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateSampleQcDao.class);

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private JdbcTemplate template;
  @Autowired
  private NamingScheme namingScheme;
  @Value("${miso.autoGenerateIdentificationBarcodes:true}")
  private boolean autoGenerateIdentificationBarcodes;
  @Value("${miso.detailed.sample.enabled:false}")
  private Boolean detailedSampleEnabled;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  /**
   * Generates a unique barcode based on the library's name and alias.
   * Note that the barcode will change when the alias is changed.
   *
   * @param library
   */
  public void autoGenerateIdBarcode(Library library) {
    String barcode = library.getName() + "::" + library.getAlias();
    library.setIdentificationBarcode(barcode);
  }

  @Override
  public long save(Library library) throws IOException {
    long id;
    if (library.getId() == AbstractLibrary.UNSAVED_ID) {
      if (!namingScheme.duplicateLibraryAliasAllowed() && !listByAlias(library.getAlias()).isEmpty()
          && (library.getLibraryAdditionalInfo() != null ? !library.getLibraryAdditionalInfo().hasNonStandardAlias() : true)) {
        // throw if duplicate aliases are not allowed and the library has a standard alias (detailed sample only)
        throw new IOException("NEW: A library with this alias already exists in the database");
      }
      id = (long) currentSession().save(library);
    } else {
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
  public Library lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<Library> listAll() throws IOException {
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
  public Collection<Library> listBySearch(String query) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(searchRestrictions(query, "name", "alias", "description", "identificationBarcode"));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Library> listByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Library> listBySampleId(long sampleId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("sample.id", sampleId));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Library> listByProjectId(long projectId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryImpl.class);
    criteria.add(Restrictions.eq("sample.project.id", projectId));
    @SuppressWarnings("unchecked")
    List<Library> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Library> getByIdList(List<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Query query = currentSession().createQuery("from LibraryImpl where libraryId in (:ids)");
    query.setParameterList("ids", idList, LongType.INSTANCE);
    @SuppressWarnings("unchecked")
    List<Library> records = query.list();
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
    criteria.add(Restrictions.eq("description", description));
    criteria.add(Restrictions.eq("platformType", platformType));
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
  public Collection<LibraryType> listAllLibraryTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryType.class);
    @SuppressWarnings("unchecked")
    List<LibraryType> records = criteria.list();
    return records;
  }

  @Override
  public Collection<LibraryType> listLibraryTypesByPlatform(String platformName) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryType.class);
    criteria.add(Restrictions.eq("platformType", PlatformType.get(platformName)));
    @SuppressWarnings("unchecked")
    List<LibraryType> records = criteria.list();
    return records;
  }

  @Override
  public Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibrarySelectionType.class);
    @SuppressWarnings("unchecked")
    List<LibrarySelectionType> records = criteria.list();
    return records;
  }

  @Override
  public Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryStrategyType.class);
    @SuppressWarnings("unchecked")
    List<LibraryStrategyType> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Library> listAllWithLimit(long limit) throws IOException {
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
  public Collection<Library> getByBarcodeList(List<String> barcodeList) throws IOException {
    Query query = currentSession().createQuery("from LibraryImpl where identificationBarcode in (:barcodes)");
    query.setParameterList("barcodes", barcodeList, StringType.INSTANCE);
    @SuppressWarnings("unchecked")
    List<Library> records = query.list();
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
    criteria.add(searchRestrictions(querystr, "name", "alias", "description", "identificationBarcode"));
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
    // TODO Auto-generated method stub
    return null;
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

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @CoverageIgnore
  public JdbcTemplate getTemplate() {
    return template;
  }

  @CoverageIgnore
  public void setTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public boolean isAutoGenerateIdentificationBarcodes() {
    return autoGenerateIdentificationBarcodes;
  }

  public void setAutoGenerateIdentificationBarcodes(boolean autoGenerateIdentificationBarcodes) {
    this.autoGenerateIdentificationBarcodes = autoGenerateIdentificationBarcodes;
  }

  public Boolean getDetailedSampleEnabled() {
    return detailedSampleEnabled;
  }

  public void setDetailedSampleEnabled(Boolean detailedSampleEnabled) {
    this.detailedSampleEnabled = detailedSampleEnabled;
  }

}
