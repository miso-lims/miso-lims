package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.WatcherStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunDao implements RunStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateRunDao.class);

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private RunAlertManager runAlertManager;

  private JdbcTemplate template;
  private WatcherStore watcherDAO;
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public static final String TABLE_NAME = "Run";

  private void updateWatchers(Run run) throws IOException {
    // if this is saved by a user, and not automatically saved by the notification system
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    watcherDAO.removeWatchedEntityByUser(run, user);

    for (User u : run.getWatchers()) {
      watcherDAO.saveWatchedEntityUser(run, u);
    }
  }

  @Override
  public long save(Run run) throws IOException {
    updateWatchers(run);
    long id;
    if (run.getId() == AbstractRun.UNSAVED_ID) {
      currentSession().save(run);
    } else {
      currentSession().update(run);
    }
    id = run.getId();
    return id;
  }

  @Override
  public Run get(long id) throws IOException {
    return (Run) currentSession().get(RunImpl.class, id);
  }

  @Override
  public Run lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public List<Run> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public boolean remove(Run run) throws IOException {
    if (run.isDeletable()) {
      Long runId = run.getId();
      currentSession().delete(run);

      Run testIfExists = get(runId);
      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public Run getLatestStartDateRunBySequencerPartitionContainerId(long containerId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    criteria.addOrder(Order.desc("status.startDate"));
    criteria.setMaxResults(1);
    return (Run) criteria.uniqueResult();
  }

  @Override
  public Run getLatestRunIdRunBySequencerPartitionContainerId(long containerId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    criteria.addOrder(Order.desc("run.id"));
    criteria.setMaxResults(1);
    return (Run) criteria.uniqueResult();
  }

  @Override
  public List<Run> listBySearch(String query) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(DbUtils.searchRestrictions(query, "name", "alias", "description"));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public Run getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (Run) criteria.uniqueResult();
  }

  @Override
  public List<Run> listByPoolId(long poolId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers", "spc");
    criteria.createAlias("spc.partitions", "partition");
    criteria.add(Restrictions.eq("partition.pool.id", poolId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listByProjectId(long projectId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers.partitions", "partition");
    criteria.createAlias("partition.pool.libraries", "library");
    criteria.add(Restrictions.eq("library.sample.project.id", projectId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listByPlatformId(long platformId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(Restrictions.eq("sequencerReference.platform.id", platformId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listByStatus(String health) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(Restrictions.eq("status.health", health));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listBySequencerId(long sequencerReferenceId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(Restrictions.eq("sequencerReference.id", sequencerReferenceId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listAllWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.addOrder(Order.desc("id"));
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public int[] saveAll(Collection<Run> runs) throws IOException {
    List<Long> rows = new ArrayList<>();
    log.debug(">>> Entering saveAll with " + runs.size() + " runs");
    for (Run run : runs) {
      Long runId = save(run);
      if (runId != null) rows.add(runId);
    }
    log.debug("<<< Exiting saveAll");
    int[] retval = new int[rows.size()];
    for (int i = 0; i < rows.size(); i++) {
      retval[i] = rows.get(i).intValue();
    }
    return retval;
  }

  @Override
  public Map<String, Integer> getRunColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Override
  public long countRuns() throws IOException {
    long c = (Long) currentSession().createCriteria(RunImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public List<Run> listBySearchOffsetAndNumResults(int offset, int limit, String querystr, String sortDir, String sortCol)
      throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(DbUtils.searchRestrictions(querystr, "name", "alias", "description"));
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(limit);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    @SuppressWarnings("unchecked")
    List<Run> runs = criteria.list();
    return runs;
  }

  @Override
  public List<Run> listByOffsetAndNumResults(int offset, int limit, String sortDir, String sortCol) throws IOException {
    if (offset < 0 || limit < 0) throw new IOException("Limit and Offset must not be less than zero");
    if ("lastModified".equals(sortCol)) sortCol = "derivedInfo.lastModified";
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("derivedInfo", "derivedInfo");
    criteria.setFirstResult(offset);
    criteria.setMaxResults(limit);
    criteria.addOrder("asc".equalsIgnoreCase(sortDir) ? Order.asc(sortCol) : Order.desc(sortCol));
    @SuppressWarnings("unchecked")
    List<Run> runs = criteria.list();
    return runs;
  }

  @Override
  public long countBySearch(String querystr) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(DbUtils.searchRestrictions(querystr, "name", "alias", "description"));
    long c = (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public RunAlertManager getRunAlertManager() {
    return runAlertManager;
  }

  public void setRunAlertManager(RunAlertManager runAlertManager) {
    this.runAlertManager = runAlertManager;
  }

  public WatcherStore getWatcherDAO() {
    return watcherDAO;
  }

  public void setWatcherDAO(WatcherStore watcherDAO) {
    this.watcherDAO = watcherDAO;
  }

  public com.eaglegenomics.simlims.core.manager.SecurityManager getSecurityManager() {
    return securityManager;
  }

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

}
