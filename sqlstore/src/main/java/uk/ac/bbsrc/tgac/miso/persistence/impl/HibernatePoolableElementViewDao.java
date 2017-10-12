package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.PoolableElementViewDao;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolableElementViewDao implements PoolableElementViewDao, HibernatePaginatedDataSource<PoolableElementView> {

  // Make sure these match the HiberateLibraryDilutionDao
  private static final String[] SEARCH_PROPERTIES = new String[] { "dilutionName", "dilutionBarcode", "libraryName", "libraryAlias",
      "libraryDescription" };

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public PoolableElementView get(Long dilutionId) throws IOException {
    return (PoolableElementView) currentSession().get(PoolableElementView.class, dilutionId);
  }

  @Override
  public PoolableElementView getByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new IOException("Barcode cannot be null!");
    Criteria criteria = currentSession().createCriteria(PoolableElementView.class);
    criteria.add(Restrictions.eq("dilutionBarcode", barcode));
    return (PoolableElementView) criteria.uniqueResult();
  }

  @Override
  public PoolableElementView getByPreMigrationId(Long preMigrationId) throws IOException {
    if (preMigrationId == null) throw new NullPointerException("preMigrationId cannot be null");
    Criteria criteria = currentSession().createCriteria(PoolableElementView.class);
    criteria.add(Restrictions.eq("preMigrationId", preMigrationId));
    return (PoolableElementView) criteria.uniqueResult();
  }

  @Override
  public String getProjectColumn() {
    return "projectId";
  }

  @Override
  public Class<? extends PoolableElementView> getRealClass() {
    return PoolableElementView.class;
  }

  @Override
  public List<PoolableElementView> list(List<Long> dilutionIds) throws IOException {
    if (dilutionIds.size() == 0) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(PoolableElementView.class);
    criteria.add(Restrictions.in("id", dilutionIds));
    @SuppressWarnings("unchecked")
    List<PoolableElementView> results = criteria.list();
    return results;
  }

  @Override
  public Iterable<String> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
    case "id":
      return "dilutionId";
    case "name":
      return "dilutionName";
    case "volume":
      return "dilutionVolume";
    case "identificationBarcode":
      return "dilutionBarcode";
    case "library.id":
      return "libraryId";
    case "library.alias":
      return "libraryAlias";
    case "library.parentSampleId":
      return "sampleId";
    case "library.parentSampleAlias":
      return "sampleAlias";
    case "dilutionUserName":
      return "creatorName";
    case "creationDate":
      return "created";
    case "library.platformType":
      return "platformType";
    case "concentration":
      return "dilutionConcentration";
    default:
      return original;
    }
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    criteria
        .add(Restrictions.sqlRestriction("EXISTS(SELECT * FROM Pool_Dilution WHERE pool_poolId = ? AND dilution_dilutionId = dilutionId)",
            poolId, LongType.INSTANCE));
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String propertyForDate(Criteria item, DateType type) {
    switch (type) {
    case CREATE:
      return "created";
    case UPDATE:
      return "lastModified";
    default:
      return null;
    }
  }

  @Override
  public String propertyForUserName(Criteria item, boolean creator) {
    return creator ? "creatorName" : "lastModifierName";
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public void restrictPaginationByBox(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.add(DbUtils.searchRestrictions(name, "boxAlias", "boxName", "boxIdentificationBarcode", "boxLocationBarcode"));
  }

  @Override
  public String getFriendlyName() {
    return "Dilution";
  }
}
