package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractRun;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunDao implements RunStore, HibernatePaginatedDataSource<Run> {

  private static final List<String> STANDARD_ALIASES = Arrays.asList("derivedInfo", "status", "lastModifier", "derivedInfo.creator");

  protected static final Logger log = LoggerFactory.getLogger(HibernateRunDao.class);

  private static final String WATCHERS_GROUP = "RunWatchers";
  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "description" };
  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private SecurityStore securityStore;
  @Autowired
  private JdbcTemplate template;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public static final String TABLE_NAME = "Run";

  @Override
  public long save(Run run) throws IOException {
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
    Run run = (Run) currentSession().get(RunImpl.class, id);
    return withWatcherGroup(run);
  }

  private Group getRunWatcherGroup() throws IOException {
    return securityStore.getGroupByName(WATCHERS_GROUP);
  }

  private Run withWatcherGroup(Run run) throws IOException {
    if (run != null) run.setWatchGroup(getRunWatcherGroup());
    return run;
  }

  private List<Run> withWatcherGroup(List<Run> runs) throws IOException {
    Group group = getRunWatcherGroup();
    for (Run run : runs) {
      run.setWatchGroup(group);
    }
    return runs;
  }

  @Override
  public List<Run> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();

    return withWatcherGroup(records);
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
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(RunImpl.class, "r");
    criteria.createAlias("r.containers", "spc").createAlias("r.status", "status");
    criteria.add(Restrictions.eq("spc.id", containerId));
    criteria.addOrder(Order.desc("status.startDate"));
    criteria.setMaxResults(1);
    return withWatcherGroup((Run) criteria.uniqueResult());
  }

  @Override
  public Run getLatestRunIdRunBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    criteria.addOrder(Order.desc("id"));
    criteria.setMaxResults(1);
    return withWatcherGroup((Run) criteria.uniqueResult());
  }

  @Override
  public List<Run> listBySearch(String query) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(DbUtils.searchRestrictions(query, SEARCH_PROPERTIES));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public Run getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    return withWatcherGroup((Run) criteria.uniqueResult());
  }

  @Override
  public List<Run> listByPoolId(long poolId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers", "spc").createAlias("spc.partitions", "partition");
    criteria.createAlias("partition.pool", "pool");
    criteria.add(Restrictions.eq("pool.id", poolId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.createAlias("containers", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public List<Run> listByProjectId(long projectId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(RunImpl.class, "r");
    idCriteria.createAlias("r.containers", "container").createAlias("container.partitions", "partition");
    idCriteria.createAlias("partition.pool", "pool").createAlias("pool.pooledElementViews", "dilution");
    idCriteria.add(Restrictions.eq("dilution.projectId", projectId));
    idCriteria.setProjection(Projections.distinct(Projections.property("r.id")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public List<Run> listByPlatformId(long platformId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class, "r");
    criteria.createAlias("r.sequencerReference", "sr");
    criteria.add(Restrictions.eq("sr.platform.id", platformId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public List<Run> listByStatus(String health) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class, "r");
    criteria.createAlias("r.status", "status");
    criteria.add(Restrictions.eq("status.health", HealthType.get(health)));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public List<Run> listBySequencerId(long sequencerReferenceId) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.add(Restrictions.eq("sequencerReference.id", sequencerReferenceId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public List<Run> listAllWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(RunImpl.class);
    criteria.addOrder(Order.desc("id"));
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return withWatcherGroup(records);
  }

  @Override
  public void saveAll(Collection<Run> runs) throws IOException {
    log.debug(">>> Entering saveAll with " + runs.size() + " runs");
    for (Run run : runs) {
      save(run);
    }
    log.debug("<<< Exiting saveAll");
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

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  @Override
  public void addWatcher(Run run, User watcher) {
    log.debug("Adding watcher " + watcher.getLoginName() + " to " + run.getName());
    run.addWatcher(watcher);
    currentSession().update(run);
  }

  @Override
  public void removeWatcher(Run run, User watcher) {
    log.debug("Removing watcher " + watcher.getLoginName() + " from " + run.getWatchableIdentifier());
    run.removeWatcher(watcher);
    currentSession().update(run);
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId) {
    criteria.createAlias("containers", "container");
    criteria.createAlias("container.partitions", "partition");
    criteria.createAlias("partition.pool", "pool");
    criteria.createAlias("pool.pooledElementViews", "dilution");
    HibernatePaginatedDataSource.super.restrictPaginationByProjectId(criteria, projectId);
  }

  @Override
  public String getProjectColumn() {
    return "dilution.projectId";
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
    if ("lastModified".equals(original)) return "derivedInfo.lastModified";
    if ("lastUpdated".equals(original)) return "status.lastUpdated";
    return original;

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
  public Class<? extends Run> getRealClass() {
    return RunImpl.class;
  }

  @Override
  public void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths) {
    criteria.add(Restrictions.in("status.health", healths.toArray()));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType) {
    criteria.add(Restrictions.eq("platformType", platformType));
  }
}
