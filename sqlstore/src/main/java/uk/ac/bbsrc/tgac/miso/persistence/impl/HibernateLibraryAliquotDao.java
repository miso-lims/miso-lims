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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.BoxStore;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryAliquotStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateLibraryAliquotDao
    implements LibraryAliquotStore, HibernatePaginatedBoxableSource<LibraryAliquot> {

  // Make sure these match the HiberatePoolableElementViewDao
  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "identificationBarcode" };
  private final static List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("library"));

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
  public long save(LibraryAliquot aliquot) throws IOException {
    long id;
    if (aliquot.getId() == LibraryAliquot.UNSAVED_ID) {
      id = (long) currentSession().save(aliquot);
    } else {
      if (aliquot.isDiscarded()) {
        boxStore.removeBoxableFromBox(aliquot);
      }
      currentSession().update(aliquot);
      id = aliquot.getId();
    }
    return id;
  }

  @Override
  public LibraryAliquot get(long id) throws IOException {
    return (LibraryAliquot) currentSession().get(LibraryAliquot.class, id);
  }

  @Override
  public List<LibraryAliquot> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public List<LibraryAliquot> listByLibraryId(long libraryId) throws IOException {
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.add(Restrictions.eq("library.id", libraryId));
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
    return records;
  }

  @Override
  public LibraryAliquot getByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new IOException("Barcode cannot be null!");
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.add(Restrictions.eq("identificationBarcode", barcode));
    return (LibraryAliquot) criteria.uniqueResult();
  }

  @Override
  public Collection<LibraryAliquot> getByBarcodeList(Collection<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
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
  public Class<? extends LibraryAliquot> getRealClass() {
    return LibraryAliquot.class;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
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
    DetachedCriteria subquery = DetachedCriteria.forClass(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.poolableElementView", "view")
        .add(Restrictions.eq("id", poolId))
        .setProjection(Projections.property("view.aliquotId"));
    criteria.add(Property.forName("id").in(subquery));
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
    case ENTERED:
      return "creationTime";
    case UPDATE:
      return "lastUpdated";
    default:
      return null;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("library.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public String getFriendlyName() {
    return "Library Aliquot";
  }

  @Override
  public List<LibraryAliquot> listByIdList(List<Long> idList) throws IOException {
    if (idList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(LibraryAliquot.class);
    criteria.add(Restrictions.in("id", idList));
    @SuppressWarnings("unchecked")
    List<LibraryAliquot> records = criteria.list();
    return records;
  }
}
