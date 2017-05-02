package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolDao implements PoolStore, HibernatePaginatedDataSource<Pool> {

  protected static final Logger log = LoggerFactory.getLogger(HibernatePoolDao.class);

  private static class ChangeLogEntry {
    public Pool pool;
    public String summary;
  }

  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "identificationBarcode", "description" };

  private static final String TABLE_NAME = "Pool";

  private static final String WATCHER_GROUP = "PoolWatchers";

  @Autowired
  private BoxStore boxStore;

  private final Queue<ChangeLogEntry> changeLogQueue = new ConcurrentLinkedQueue<>();

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private SecurityStore securityStore;

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public void addWatcher(Pool pool, User watcher) {
    log.debug("Adding watcher " + watcher.getLoginName() + " to " + pool.getName());
    pool.addWatcher(watcher);
    currentSession().update(pool);
  }

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
    return withWatcherGroup((PoolImpl) currentSession().get(PoolImpl.class, poolId));
  }

  public BoxStore getBoxStore() {
    return boxStore;
  }

  @Override
  public Pool getByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new NullPointerException("cannot look up null barcode");
    return withWatcherGroup((Pool) createCriteria().add(Restrictions.eq("identificationBarcode", barcode)).uniqueResult());
  }

  @Override
  public Collection<Pool> getByBarcodeList(Collection<String> barcodeList) throws IOException {
    if (barcodeList.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return withWatcherGroup(results);
  }

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  @Override
  public Map<String, Integer> getPoolColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(jdbcTemplate, TABLE_NAME);
  }

  private Group getPoolWatcherGroup() throws IOException {
    return securityStore.getGroupByName(WATCHER_GROUP);
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
    return withWatcherGroup(results);
  }

  @Override
  public List<Pool> listAllByCriteria(PlatformType platformType, String query, Integer limit, boolean ready) throws IOException {
    if (limit != null && limit == 0) {
      return Collections.emptyList();
    }
    Criteria criteria = createCriteria();
    if (platformType != null) {
      criteria.add(Restrictions.eq("platformType", platformType));
    }
    if (query != null) {
      criteria.add(DbUtils.searchRestrictions(query, SEARCH_PROPERTIES));
    }
    if (limit != null) {
      criteria.setMaxResults(limit);
    }
    if (ready) {
      criteria.add(Restrictions.eq("readyToRun", true));
    }
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return withWatcherGroup(results);
  }

  @Override
  public List<Pool> listByLibraryId(long libraryId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(LibraryImpl.class);
    idCriteria.add(Restrictions.eq("id", libraryId));
    idCriteria.createAlias("libraryDilutions", "libraryDilutions");
    idCriteria.createAlias("libraryDilutions.pools", "pools");
    idCriteria.setProjection(Projections.distinct(Projections.property("pools.id")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return results;

  }

  @Override
  public List<Pool> listByProjectId(long projectId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(PoolImpl.class, "p");
    idCriteria.createAlias("p.pooledElementViews", "dilution");
    idCriteria.add(Restrictions.eq("dilution.projectId", projectId));
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
  public boolean remove(Pool t) throws IOException {
    boolean exists = currentSession().get(PoolImpl.class, t.getId()) != null;
    currentSession().delete(t);
    return exists;
  }

  @Override
  public void removeWatcher(Pool pool, User watcher) {
    log.debug("Removing watcher " + watcher.getLoginName() + " from " + pool.getWatchableIdentifier());
    pool.removeWatcher(watcher);
    currentSession().update(pool);

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
        pool.setVolume(0D);
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

  public void setJdbcTemplate(JdbcTemplate template) {
    this.jdbcTemplate = template;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private List<Pool> withWatcherGroup(List<Pool> pools) throws IOException {
    Group group = getPoolWatcherGroup();
    for (Pool pool : pools) {
      pool.setWatchGroup(group);
    }
    return pools;
  }

  private Pool withWatcherGroup(Pool pool) throws IOException {
    if (pool != null) {
      pool.setWatchGroup(getPoolWatcherGroup());
    }
    return pool;
  }

  private static final List<String> STANDARD_ALIASES = Arrays.asList("derivedInfo", "lastModifier", "derivedInfo.creator");

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public String getProjectColumn() {
    return "dilution.projectId";
  }

  @Override
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForSortColumn(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("id".equals(sortCol)) sortCol = "poolId";
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    return sortCol;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId) {
    criteria.createAlias("pooledElementViews", "dilution");
    HibernatePaginatedDataSource.super.restrictPaginationByProjectId(criteria, projectId);
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }

  @Override
  public String propertyForDate(Criteria criteria, boolean creation) {
    return creation ? "derivedInfo.created" : "derivedInfo.lastModified";
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return creator ? "creator.loginName" : "lastModifier.loginName";
  }

  @Override
  public Class<? extends Pool> getRealClass() {
    return PoolImpl.class;
  }

}
