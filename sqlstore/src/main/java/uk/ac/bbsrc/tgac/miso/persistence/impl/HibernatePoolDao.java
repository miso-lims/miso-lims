package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.BoxStore;
import uk.ac.bbsrc.tgac.miso.persistence.PoolStore;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolDao implements PoolStore, HibernatePaginatedBoxableSource<Pool> {

  private static class ChangeLogEntry {
    public Pool pool;
    public String summary;
  }

  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "identificationBarcode", "description" };

  @Autowired
  private BoxStore boxStore;

  private final Queue<ChangeLogEntry> changeLogQueue = new ConcurrentLinkedQueue<>();

  @Autowired
  private SecurityStore securityStore;

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public int count() {
    long c = (Long) createCriteria().setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

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

  public BoxStore getBoxStore() {
    return boxStore;
  }

  @Override
  public Pool getByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new NullPointerException("cannot look up null barcode");
    return (Pool) createCriteria().add(Restrictions.eq("identificationBarcode", barcode)).uniqueResult();
  }

  @Override
  public Pool getByAlias(String alias) throws IOException {
    return (Pool) createCriteria().add(Restrictions.eq("alias", alias)).uniqueResult();
  }

  @Override
  public List<Pool> getByBarcodeList(Collection<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return results;
  }

  public SecurityStore getSecurityStore() {
    return securityStore;
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
  public List<Pool> listAllByCriteria(PlatformType platformType, String query, Integer limit) throws IOException {
    if (limit != null && limit == 0) {
      return Collections.emptyList();
    }
    Criteria criteria = createCriteria();
    if (platformType != null) {
      criteria.add(Restrictions.eq("platformType", platformType));
    }
    if (query != null) {
      criteria.add(DbUtils.searchRestrictions(query, false, SEARCH_PROPERTIES));
    }
    if (limit != null) {
      criteria.setMaxResults(limit);
    }
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return results;
  }

  @Override
  public List<Pool> listByLibraryId(long libraryId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Pool> results = currentSession().createCriteria(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.poolableElementView", "view")
        .add(Restrictions.eq("view.libraryId", libraryId))
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
        .list();
    return results;
  }

  @Override
  public List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Pool> results = currentSession().createCriteria(PoolImpl.class)
        .createAlias("poolElements", "element")
        .createAlias("element.poolableElementView", "view")
        .add(Restrictions.eq("view.aliquotId", aliquotId))
        .list();
    return results;
  }

  @Override
  public List<Pool> listByProjectId(long projectId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(PoolImpl.class, "p");
    idCriteria.createAlias("p.poolElements", "poolElement");
    idCriteria.createAlias("poolElement.poolableElementView", "aliquot");
    idCriteria.add(Restrictions.eq("aliquot.projectId", projectId));
    idCriteria.setProjection(Projections.distinct(Projections.property("p.id")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(PoolImpl.class);
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Pool> pools = criteria.list();
    return pools;
  }

  @Override
  public long save(final Pool pool) throws IOException {
    currentSession().flush();
    Long id;
    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      id = (Long) currentSession().save(pool);
      currentSession().flush();
    } else {
      if (pool.isDiscarded()) {
        getBoxStore().removeBoxableFromBox(pool);
        pool.setVolume(BigDecimal.ZERO);
      }

      id = pool.getId();
      currentSession().update(pool);
      currentSession().flush();
      ChangeLogEntry log;
      while ((log = changeLogQueue.poll()) != null) {
        pool.createChangeLog(log.summary, "contents", log.pool.getLastModifier());
      }
    }

    return id;
  }

  public void setBoxStore(BoxStore boxDAO) {
    this.boxStore = boxDAO;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String getProjectColumn() {
    return "aliquot.projectId";
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForSortColumn(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("id".equals(sortCol)) sortCol = "poolId";
    return sortCol;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("poolElements", "poolElement");
    criteria.createAlias("poolElement.poolableElementView", "aliquot");
    HibernatePaginatedBoxableSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
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
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public Class<? extends Pool> getRealClass() {
    return PoolImpl.class;
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("poolElements", "poolElement");
    criteria.createAlias("poolElement.poolableElementView", "aliquotForIndex");
    criteria.createAlias("aliquotForIndex.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public String getFriendlyName() {
    return "Pool";
  }

  @Override
  public List<Pool> listPoolsById(List<Long> poolIds) {
    if (poolIds.isEmpty()) return Collections.emptyList();
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
