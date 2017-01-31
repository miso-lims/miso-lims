package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.BoxStore;
import uk.ac.bbsrc.tgac.miso.core.store.PoolStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolDao implements PoolStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernatePoolDao.class);

  private static class ChangeLogEntry {
    public Pool pool;
    public String summary;
  }

  private final static String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "identificationBarcode", "description" };

  private static final String TABLE_NAME = "Pool";

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

  @Override
  public long countPoolsBySearch(PlatformType platform, String queryStr) throws IOException {
    Criteria criteria = createCriteria().setProjection(Projections.rowCount());
    if (platform != null) {
      criteria.add(Restrictions.eq("platformType", platform));
    }
    if (!isStringEmptyOrNull(queryStr)) {
      criteria.add(DbUtils.searchRestrictions(queryStr, SEARCH_PROPERTIES));
    }
    long c = (Long) criteria.uniqueResult();
    return (int) c;
  }

  public Criteria createCriteria() {
    return currentSession().createCriteria(PoolImpl.class);
  }

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
  public Collection<Pool> getByBarcodeList(List<String> barcodeList) throws IOException {
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
    return securityStore.getGroupByName("PoolWatchers");
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
    idCriteria.createAlias("libraries", "libraries");
    idCriteria.createAlias("libraries.libraryDilutions", "libraryDilutions");
    idCriteria.createAlias("libraryDilutions.pools", "pools");
    idCriteria.setProjection(Projections.distinct(Projections.property("pools.id")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    Criteria criteria = createCriteria();
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Pool> results = criteria.list();
    return results;
  }

  @Override
  public List<Pool> listBySearchOffsetAndNumResultsAndPlatform(int offset, int resultsPerPage, String search, String sortDir,
      String sortCol, PlatformType platform) throws IOException {

    sortCol = updateSortCol(sortCol);
    if (offset < 0 || resultsPerPage < 0) {
      throw new IOException("Limit and Offset must be greater than zero");
    }

    final Criteria criteria = createCriteria();
    // required to sort by 'derivedInfo.lastModified', which is the field on which we
    // want to sort most List X pages
    criteria.createAlias("derivedInfo", "derivedInfo");
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
    Long id;
    if (pool.getId() == PoolImpl.UNSAVED_ID) {
      id = (Long) currentSession().save(pool);
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

  private String updateSortCol(String sortCol) {
    sortCol = sortCol.replaceAll("[^\\w]", "");
    if ("id".equals(sortCol)) sortCol = "poolId";
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    return sortCol;
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
}
