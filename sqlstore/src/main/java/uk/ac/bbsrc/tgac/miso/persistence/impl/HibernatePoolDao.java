package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.persistence.TemporalType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.PoolStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolDao implements PoolStore, HibernatePaginatedBoxableSource<Pool> {

  private final static String[] IDENTIFIER_PROPERTIES = {"name", "alias", "identificationBarcode"};
  private final static String[] SEARCH_PROPERTIES = {"name", "alias", "identificationBarcode", "description"};

  @Autowired
  private SessionFactory sessionFactory;

  public Criteria createCriteria() {
    return currentSession().createCriteria(PoolImpl.class);
  }

  @Override
  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Pool get(long poolId) throws IOException {
    return (PoolImpl) currentSession().get(PoolImpl.class, poolId);
  }

  @Override
  public Pool getByBarcode(String barcode) throws IOException {
    if (barcode == null)
      throw new NullPointerException("cannot look up null barcode");
    return (Pool) createCriteria().add(Restrictions.eq("identificationBarcode", barcode)).uniqueResult();
  }

  @Override
  public Pool getByAlias(String alias) throws IOException {
    return (Pool) createCriteria().add(Restrictions.eq("alias", alias)).uniqueResult();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public List<Pool> listAll() throws IOException {
    @SuppressWarnings("unchecked")
    List<Pool> results = createCriteria().list();
    return results;
  }

  @Override
  public List<Pool> listByLibraryId(long libraryId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Pool> results = currentSession().createCriteria(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.aliquot", "aliquot")
        .createAlias("aliquot.parentLibrary", "library")
        .add(Restrictions.eq("library.id", libraryId))
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        .list();
    return results;
  }

  @Override
  public List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Pool> results = currentSession().createCriteria(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.aliquot", "aliquot")
        .add(Restrictions.eq("aliquot.id", aliquotId))
        .list();
    return results;
  }

  @Override
  public long save(final Pool pool) throws IOException {
    if (!pool.isSaved()) {
      return (Long) currentSession().save(pool);
    } else {
      currentSession().update(pool);
      return pool.getId();
    }
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public String[] getIdentifierProperties() {
    return IDENTIFIER_PROPERTIES;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForSortColumn(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("id".equals(sortCol))
      sortCol = "poolId";
    return sortCol;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("poolElements", "poolElement")
        .createAlias("poolElement.aliquot", "aliquot")
        .createAlias("aliquot.parentLibrary", "library")
        .createAlias("library.parentSample", "sample")
        .createAlias("sample.parentProject", "project");

    HibernatePaginatedBoxableSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platformType", platformType));
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
  public Class<? extends Pool> getRealClass() {
    return PoolImpl.class;
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("poolElements", "poolElement")
        .createAlias("poolElement.aliquot", "aliquot")
        .createAlias("aliquot.parentLibrary", "library")
        .createAlias("library.index1", "index1")
        .createAlias("library.index2", "index2", JoinType.LEFT_OUTER_JOIN)
        .add(DbUtils.textRestriction(query, "index1.name", "index1.sequence", "index2.name", "index2.sequence"));
  }

  @Override
  public void restrictPaginationByDistributionRecipient(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    DbUtils.restrictPaginationByDistributionRecipient(criteria, query, "pools", "poolId");
  }

  @Override
  public String getFriendlyName() {
    return "Pool";
  }

  @Override
  public List<Pool> listByIdList(List<Long> poolIds) {
    if (poolIds.isEmpty())
      return Collections.emptyList();
    Criteria criteria = currentSession().createCriteria(PoolImpl.class);
    criteria.add(Restrictions.in("id", poolIds));
    @SuppressWarnings("unchecked")
    List<Pool> pools = criteria.list();
    return pools;
  }

  @Override
  public long getPartitionCount(Pool pool) {
    return (long) currentSession().createCriteria(PartitionImpl.class)
        .add(Restrictions.eq("pool", pool))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
