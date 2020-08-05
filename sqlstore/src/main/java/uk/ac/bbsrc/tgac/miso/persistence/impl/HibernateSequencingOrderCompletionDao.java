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

import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.SequencingOrderCompletionDao;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSequencingOrderCompletionDao implements SequencingOrderCompletionDao, HibernatePaginatedDataSource<SequencingOrderCompletion> {
  private static final String[] SEARCH_PROPERTIES = new String[] { "pool.alias", "pool.name", "pool.identificationBarcode",
      "pool.description" };
  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("pool"),
      new AliasDescriptor("parameters"));

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
  public Class<? extends SequencingOrderCompletion> getRealClass() {
    return SequencingOrderCompletion.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
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
  public void restrictPaginationByFulfilled(Criteria criteria, boolean isFulfilled, Consumer<String> errorHandler) {
    criteria.add(isFulfilled ? Restrictions.geProperty("loaded", "remaining")
        : Restrictions.ltProperty("loaded", "remaining"));
  }

  @Override
  public void restrictPaginationByPending(Criteria criteria, Consumer<String> errorHandler) {
    criteria.add(Restrictions.gt("loaded", 0));
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
  public void restrictPaginationById(Criteria criteria, long id, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by ID", getFriendlyName()));
  }

  @Override
  public void restrictPaginationByIds(Criteria criteria, List<Long> ids, Consumer<String> errorHandler) {
    errorHandler.accept(String.format("%s cannot be filtered by ID", getFriendlyName()));
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths, Consumer<String> errorHandler) {
    criteria.createCriteria("items").add(Restrictions.and(Restrictions.in(CollectionPropertyNames.COLLECTION_INDICES, healths.toArray()),
        Restrictions.gt(CollectionPropertyNames.COLLECTION_ELEMENTS, 0)));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("pool.poolElements", "poolElement");
    criteria.createAlias("poolElement.poolableElementView", "aliquotForIndex");
    criteria.createAlias("aliquotForIndex.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public void restrictPaginationBySequencingParametersId(Criteria criteria, long id, Consumer<String> errorHandler) {
    criteria.createAlias("parameters", "parameters");
    criteria.add(Restrictions.eqOrIsNull("parameters.id", id));
  }

  @Override
  public String getFriendlyName() {
    return "Pool";
  }
}
