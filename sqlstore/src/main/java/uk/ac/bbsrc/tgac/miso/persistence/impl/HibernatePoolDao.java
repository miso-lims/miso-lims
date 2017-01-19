package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.Criteria;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.event.manager.WatchManager;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao.ChangeLogType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolDao implements PoolStore {
  private static final String TABLE_NAME = "Pool";

  private static class ChangeLogEntry {
    public Pool pool;
    public String summary;
  }

  private final Queue<ChangeLogEntry> changeLogQueue = new ConcurrentLinkedQueue<>();

  private final Interceptor interceptor = new EmptyInterceptor() {
    private static final long serialVersionUID = 1303535328531024816L;

    private String buildElementString(Set<Dilution> dilutionss) {
      StringBuilder names = new StringBuilder();
      for (Dilution dilution : dilutionss) {
        if (names.length() > 0) {
          names.append(", ");
        }
        names.append(dilution.getName());
      }
      return names.toString();
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames,
        Type[] types) {

      if (entity instanceof Pool) {
        Pool pool = (Pool) entity;
        for (int i = 0; i < propertyNames.length; i++) {
          if (!propertyNames[i].equals("pooledElements")) continue;
          @SuppressWarnings("unchecked")
          Set<Dilution> original = (Set<Dilution>) previousState[i];
          @SuppressWarnings("unchecked")
          Set<Dilution> updated = (Set<Dilution>) currentState[i];

          Set<Dilution> added = new HashSet<>(updated);
          added.removeAll(original);
          Set<Dilution> removed = new HashSet<>(original);
          removed.removeAll(updated);

          if (!added.isEmpty() || !removed.isEmpty()) {

            String message = pool.getLastModifier().getLoginName() + (removed.isEmpty() ? "" : (" Removed: " + buildElementString(removed)))
                + (added.isEmpty() ? "" : (" Added: " + buildElementString(removed)));
            final ChangeLogEntry changeLog = new ChangeLogEntry();
            changeLog.pool = pool;
            changeLog.summary = message.toString();
            changeLogQueue.add(changeLog);
            System.out.println(message.toString());
          }

        }
      }
      return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);

    }
  };

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private BoxStore boxDAO;

  @Autowired
  private ChangeLogStore changeLogStore;

  @Override
  public Pool get(long poolId) throws IOException {
    return withWatcherGroup((PoolImpl) currentSession().get(PoolImpl.class, poolId));
  }

  @Override
  public Pool getByBarcode(String barcode) throws IOException {
    if (barcode == null) throw new NullPointerException("cannot look up null barcode");
    return withWatcherGroup((PoolImpl) createCriteria().add(Restrictions.eq("identificationBarcode", barcode)).uniqueResult());
  }

  public Criteria createCriteria() {
    return currentSession().createCriteria(PoolImpl.class);
  }

  public Session currentSession() {
    return sessionFactory.withOptions().interceptor(interceptor).openSession();
  }

  @Override
  public Pool getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    if (barcode == null) throw new NullPointerException("cannot look up null barcode");
    if (platformType == null) {
      return getByBarcode(barcode);
    }
    List<Pool> pools = listAllByCriteria(platformType, null, null, false);
    return pools.size() == 1 ? pools.get(0) : null;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  @CoverageIgnore
  public Pool lazyGet(long poolId) throws IOException {
    return get(poolId);
  }

  @Override
  public List<Pool> listAll() throws IOException {
    @SuppressWarnings("unchecked")
    List<Pool> results = createCriteria().list();
    return withWatcherGroup(results);
  }

  private List<Pool> withWatcherGroup(List<Pool> pools) throws IOException {
    Group group = getPoolWatcherGroup();
    for (Pool pool : pools) {
      pool.setWatchGroup(group);
    }
    return pools;
  }

  @Override
  public List<Pool> listByLibraryId(long libraryId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(LibraryImpl.class);
    idCriteria.add(Restrictions.eq("id", libraryId));
    idCriteria.createAlias("libraryDilutions.pools.id", "poolIds");
    idCriteria.setProjection(Projections.distinct(Projections.property("poolIds")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return results;

  }

  @Override
  public List<Pool> listByProjectId(long projectId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(SampleImpl.class);
    idCriteria.createAlias("project", "project");
    idCriteria.add(Restrictions.eq("project.id", projectId));
    idCriteria.createAlias("libraries.libraryDilutions.pools.id", "poolIds");
    idCriteria.setProjection(Projections.distinct(Projections.property("poolIds")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return results;
  }

  private Group getPoolWatcherGroup() throws IOException {
    return securityStore.getGroupByName("PoolWatchers");
  }

  @Autowired
  private SecurityStore securityStore;

  private Pool withWatcherGroup(Pool pool) throws IOException {
    if (pool != null) {
      pool.setWatchGroup(getPoolWatcherGroup());
    }
    return pool;
  }

  @Override
  public long save(final Pool pool) throws IOException {
    Long id;
    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      id = (Long) currentSession().save(pool);
    } else {
      if (pool.isDiscarded()) {
        getBoxDAO().removeBoxableFromBox(pool);
        pool.setVolume(0D);
      }

      id = pool.getId();
      currentSession().update(pool);
      currentSession().flush();
      ChangeLogEntry log;
      while ((log = changeLogQueue.poll()) != null) {
        changeLogStore.create(ChangeLogType.POOL.name(), log.pool.getId(), "contents", log.summary, log.pool.getLastModifier());
      }
    }

    return id;
  }

  @Override
  public int count() {
    long c = (Long) createCriteria().setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public long countPoolsBySearch(PlatformType platform, String queryStr) throws IOException {
    Criteria criteria = createCriteria().setProjection(Projections.rowCount());
    if (platform != null) {
      criteria.add(Restrictions.eq("platformType", platform));
    }
    if (isStringEmptyOrNull(queryStr)) {
      criteria.add(DbUtils.searchRestrictions(queryStr, SEARCH_PROPERTIES));
    }
    long c = (Long) criteria.uniqueResult();
    return (int) c;
  }

  @Override
  public Map<String, Integer> getPoolColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Autowired
  private JdbcTemplate template;

  private String updateSortCol(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("id".equals(sortCol)) sortCol = "poolId";
    return sortCol;
  }

  @Override
  public List<Pool> listBySearchOffsetAndNumResultsAndPlatform(int offset, int resultsPerPage, String search, String sortDir,
      String sortCol, PlatformType platform) throws IOException {

    sortCol = updateSortCol(sortCol);
    if (offset < 0 || resultsPerPage < 0) {
      throw new IOException("Limit and Offset must be greater than zero");
    }

    final Criteria criteria = createCriteria();
    if ("asc".equalsIgnoreCase(sortDir)) {
      criteria.addOrder(Order.asc(sortCol));
    } else if ("desc".equalsIgnoreCase(sortDir)) {
      criteria.addOrder(Order.desc(sortCol));
    }
    criteria.add(Restrictions.eq("platformType", platform));
    if (!isStringEmptyOrNull(search)) {
      criteria.add(DbUtils.searchRestrictions(search, SEARCH_PROPERTIES));
    }
    criteria.setFirstResult(offset);
    criteria.setMaxResults(resultsPerPage);
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return withWatcherGroup(results);
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public boolean remove(Pool t) throws IOException {
    boolean exists = currentSession().get(PoolImpl.class, t.getId()) != null;
    currentSession().delete(t);
    return exists;
  }

  @Override
  public Collection<Pool> getByBarcodeList(List<String> barcodeList) throws IOException {
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.in("identificationBarcode", barcodeList));
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return withWatcherGroup(results);
  }

  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "identificationBarcode", "description" };

  @Override
  public List<Pool> listAllByCriteria(PlatformType platformType, String query, Integer limit, boolean ready) throws IOException {
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

  public SecurityStore getSecurityStore() {
    return securityStore;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public JdbcTemplate getTemplate() {
    return template;
  }

  public void setTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Autowired
  private WatchManager watchManager;

  @Override
  public void removeWatcher(Pool pool, User watcher) {
    watchManager.unwatch(pool, watcher);
    currentSession().update(pool);

  }

  @Override
  public void addWatcher(Pool pool, User watcher) {
    watchManager.watch(pool, watcher);
    currentSession().update(pool);
  }

  public WatchManager getWatchManager() {
    return watchManager;
  }

  public void setWatchManager(WatchManager watchManager) {
    this.watchManager = watchManager;
  }

  public BoxStore getBoxDAO() {
    return boxDAO;
  }

  public void setBoxDAO(BoxStore boxDAO) {
    this.boxDAO = boxDAO;
  }
}
