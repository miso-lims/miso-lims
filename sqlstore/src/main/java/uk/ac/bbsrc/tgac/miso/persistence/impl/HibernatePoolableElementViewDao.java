package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;

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
import uk.ac.bbsrc.tgac.miso.persistence.PoolableElementViewDao;

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
  public Iterable<String> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId) {
    criteria
        .add(Restrictions.sqlRestriction("EXISTS(SELECT * FROM Pool_Dilution WHERE pool_poolId = ? AND dilution_dilutionId = dilutionId)",
        poolId, LongType.INSTANCE));
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String propertyForDate(Criteria item, boolean creation) {
    return creation ? "created" : "lastModified";
  }

  @Override
  public String propertyForUserName(Criteria item, boolean creator) {
    return creator ? "creatorName" : "lastModifier";
  }
}
