package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunDao implements RunStore, HibernatePaginatedDataSource<Run> {

  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("sequencer"),
      new AliasDescriptor("sequencer.instrumentModel"));

  protected static final Logger log = LoggerFactory.getLogger(HibernateRunDao.class);

  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "description" };
  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public static final String TABLE_NAME = "Run";

  @Override
  public long save(Run run) throws IOException {
    long id;
    if (run.getId() == Run.UNSAVED_ID) {
      currentSession().save(run);
    } else {
      currentSession().update(run);
    }
    id = run.getId();
    return id;
  }

  @Override
  public Run get(long id) throws IOException {
    Run run = (Run) currentSession().get(Run.class, id);
    return run;
  }

  @Override
  public List<Run> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class);
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();

    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public Run getLatestStartDateRunBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(Run.class, "r");
    criteria.createAlias("runPositions", "runPos");
    criteria.createAlias("runPos.container", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    criteria.addOrder(Order.desc("startDate"));
    criteria.setMaxResults(1);
    return (Run) criteria.uniqueResult();
  }

  @Override
  public Run getLatestRunIdRunBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.createAlias("runPositions", "runPos");
    criteria.createAlias("runPos.container", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    criteria.addOrder(Order.desc("id"));
    criteria.setMaxResults(1);
    return (Run) criteria.uniqueResult();
  }

  @Override
  public List<Run> listBySearch(String query) throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.add(DbUtils.searchRestrictions(query, false, SEARCH_PROPERTIES));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public Run getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (Run) criteria.uniqueResult();
  }

  @Override
  public List<Run> listByPoolId(long poolId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(Run.class);
    idCriteria.createAlias("runPositions", "runPos")
        .createAlias("runPos.container", "spc")
        .createAlias("spc.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .add(Restrictions.eq("pool.id", poolId))
        .setProjection(Projections.distinct(Projections.property("id")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    if (ids.isEmpty()) return Collections.emptyList();
    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships, unexpected associations may
    // show up
    currentSession().flush();

    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.createAlias("runPositions", "runPos");
    criteria.createAlias("runPos.container", "spc");
    criteria.add(Restrictions.eq("spc.id", containerId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listByProjectId(long projectId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(Run.class, "r");
    idCriteria.createAlias("runPositions", "runPos")
        .createAlias("runPos.container", "spc")
        .createAlias("spc.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .createAlias("pool.poolElements", "poolElement")
        .createAlias("poolElement.poolableElementView", "aliquot");
    idCriteria.add(Restrictions.eq("aliquot.projectId", projectId));
    idCriteria.setProjection(Projections.distinct(Projections.property("r.id")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listByStatus(String health) throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class, "r");
    criteria.add(Restrictions.eq("health", HealthType.get(health)));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public List<Run> listBySequencerId(long sequencerId) throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.add(Restrictions.eq("sequencer.id", sequencerId));
    @SuppressWarnings("unchecked")
    List<Run> records = criteria.list();
    return records;
  }

  @Override
  public long countRuns() throws IOException {
    long c = (Long) currentSession().createCriteria(Run.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public long countBySearch(String querystr) throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.add(DbUtils.searchRestrictions(querystr, false, "name", "alias", "description"));
    long c = (long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("runPositions", "runPos");
    criteria.createAlias("runPos.container", "spc");
    criteria.createAlias("spc.partitions", "partition");
    criteria.createAlias("partition.pool", "pool");
    criteria.createAlias("pool.poolElements", "poolElement");
    criteria.createAlias("poolElement.poolableElementView", "aliquot");
    HibernatePaginatedDataSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public void restrictPaginationBySequencingParametersName(Criteria criteria, String name, Consumer<String> errorHandler) {
    criteria.createAlias("sequencingParameters", "params");
    criteria.add(Restrictions.ilike("params.name", name, MatchMode.START));
  }

  @Override
  public String getProjectColumn() {
    return "aliquot.projectId";
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
    if ("platformType".equals(original)) return "instrumentModel.platformType";
    if ("status".equals(original)) return "health";
    if ("endDate".equals(original)) return "completionDate";
    return original;

  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    switch (type) {
    case CREATE:
      return "startDate";
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
  public Class<? extends Run> getRealClass() {
    return Run.class;
  }

  @Override
  public void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths, Consumer<String> errorHandler) {
    criteria.add(Restrictions.in("health", healths.toArray()));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("instrumentModel.platformType", platformType));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String index, Consumer<String> errorHandler) {
    criteria.createAlias("runPositions", "runPos");
    criteria.createAlias("runPos.container", "spc");
    criteria.createAlias("spc.partitions", "partition");
    criteria.createAlias("partition.pool", "pool");
    criteria.createAlias("pool.poolElements", "poolElement");
    criteria.createAlias("poolElement.poolableElementView", "aliquotForIndex");
    criteria.createAlias("aliquotForIndex.indices", "indices");
    HibernateLibraryDao.restrictPaginationByIndices(criteria, index);
  }

  @Override
  public void restrictPaginationBySequencerId(Criteria criteria, long id, Consumer<String> errorHandler) {
    criteria.createAlias("sequencer", "sequencer");
    criteria.add(Restrictions.eq("sequencer.id", id));
  }

  @Override
  public String getFriendlyName() {
    return "Run";
  }

}
