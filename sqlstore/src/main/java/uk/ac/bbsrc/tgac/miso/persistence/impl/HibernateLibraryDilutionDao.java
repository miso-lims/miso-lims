package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryDilutionStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryDilutionDao
    implements LibraryDilutionStore, HibernatePaginatedBoxableSource<LibraryDilution> {

  // Make sure these match the HiberatePoolableElementViewDao
  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "identificationBarcode", "library.name", "library.alias",
      "library.description" };
  private final static List<String> STANDARD_ALIASES = Arrays.asList("library");

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private BoxStore boxStore;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public void setBoxStore(BoxStore boxStore) {
    this.boxStore = boxStore;
  }

  @Override
  public long save(LibraryDilution dilution) throws IOException {
    long id;
    if (dilution.getId() == LibraryDilution.UNSAVED_ID) {
      id = (long) currentSession().save(dilution);
    } else {
      if (dilution.isDiscarded()) {
        boxStore.removeBoxableFromBox(dilution);
      }
      currentSession().update(dilution);
      id = dilution.getId();
    }
    return id;
  }

  @Override
  public LibraryDilution get(long id) throws IOException {
    return (LibraryDilution) currentSession().get(LibraryDilution.class, id);
  }

  @Override
  public Collection<LibraryDilution> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryDilution.class);
    @SuppressWarnings("unchecked")
    List<LibraryDilution> records = criteria.list();
    return records;
  }

  @Override
  public boolean remove(LibraryDilution dilution) throws IOException {
    if (dilution.isDeletable()) {
      currentSession().delete(dilution);
      LibraryDilution testIfExists = get(dilution.getId());

      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryDilution.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public Collection<LibraryDilution> listByLibraryId(long libraryId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryDilution.class);
    criteria.add(Restrictions.eq("library.id", libraryId));
    @SuppressWarnings("unchecked")
    List<LibraryDilution> records = criteria.list();
    return records;
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new IOException("Barcode cannot be null!");
    Criteria criteria = currentSession().createCriteria(LibraryDilution.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (LibraryDilution) criteria.uniqueResult();
  }

  @Override
  public Collection<LibraryDilution> getByBarcodeList(Collection<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryDilution.class);
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<LibraryDilution> records = criteria.list();
    return records;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Class<? extends LibraryDilution> getRealClass() {
    return LibraryDilution.class;
  }

  @Override
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("library.sample", "sample");
    criteria.createAlias("sample.project", "project");
    HibernatePaginatedBoxableSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    criteria.createAlias("pools", "pool");
    criteria.add(Restrictions.eq("pool.id", poolId));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("library.platformType", platformType));
  }

  @Override
  public String propertyForDate(Criteria item, DateType type) {
    switch (type) {
    case CREATE:
      return "creationDate";
    case UPDATE:
      return "lastUpdated";
    default:
      return null;
    }
  }

  @Override
  public String propertyForUserName(Criteria item, boolean creator) {
    return creator ? "dilutionUserName" : "derivedInfo.lastModifier.loginName";
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("library.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public String getFriendlyName() {
    return "Dilution";
  }

  @Override
  public Collection<LibraryDilution> listByIdList(List<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryDilution.class);
    criteria.add(Restrictions.in("id", idList));
    @SuppressWarnings("unchecked")
    List<LibraryDilution> records = criteria.list();
    return records;
  }
}
