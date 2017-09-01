package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.persister.collection.CollectionPropertyNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.PoolOrderCompletionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolOrderCompletionDao implements PoolOrderCompletionDao, HibernatePaginatedDataSource<PoolOrderCompletion> {
  private static final String[] SEARCH_PROPERTIES = new String[] { "pool.alias", "pool.name", "pool.identificationBarcode",
      "pool.description" };
  private static final List<String> STANDARD_ALIASES = Arrays.asList("pool", "parameters");

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
  public String getProjectColumn() {
    throw new IllegalArgumentException();
  }

  @Override
  public Class<? extends PoolOrderCompletion> getRealClass() {
    return PoolOrderCompletion.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
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
  public void restrictPaginationByFulfilled(Criteria criteria, boolean isFulfilled, Consumer<String> errorHandler) {
    criteria.add(isFulfilled ? Restrictions.le("remaining", 0) : Restrictions.gt("remaining", 0));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("pool.platformType", platformType));
  }

  @Override
  public void restrictPaginationByPoolId(Criteria criteria, long poolId, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("pool.id", poolId));
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case UPDATE:
      return "lastUpdated";
    default:
      return null;
    }
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths, Consumer<String> errorHandler) {
    criteria.createCriteria("items").add(Restrictions.and(Restrictions.in(CollectionPropertyNames.COLLECTION_INDICES, healths.toArray()),
        Restrictions.gt(CollectionPropertyNames.COLLECTION_ELEMENTS, 0)));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("pool.pooledElementViews", "dilutionForIndex");
    criteria.createAlias("dilutionForIndex.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public void restrictPaginationBySequencingParametersId(Criteria criteria, long id, Consumer<String> errorHandler) {
    criteria.createAlias("sequencingParameters", "sequencingParameters");
    criteria.add(Restrictions.eqOrIsNull("sequencingParameters.id", id));
  }

  @Override
  public String getFriendlyName() {
    return "Pool";
  }
}
