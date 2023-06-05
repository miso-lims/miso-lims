package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.TemporalType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunDao implements RunStore, HibernatePaginatedDataSource<Run> {

  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("sequencer"),
      new AliasDescriptor("sequencer.instrumentModel"));

  private static final String[] SEARCH_PROPERTIES = new String[] {"name", "alias", "description"};
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
  public Run getLatestStartDateRunBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships,
    // unexpected associations may
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
    // flush here because if Hibernate has not persisted recent changes to container-run relationships,
    // unexpected associations may
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
  public Run getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(Run.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (Run) criteria.uniqueResult();
  }

  @Override
  public List<Run> listByPoolId(long poolId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Long> ids = currentSession().createCriteria(Run.class)
        .createAlias("runPositions", "runPos")
        .createAlias("runPos.container", "spc")
        .createAlias("spc.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .add(Restrictions.eq("pool.id", poolId))
        .setProjection(Projections.distinct(Projections.property("id")))
        .list();
    return listByIdList(ids);
  }

  @Override
  public List<Run> listByLibraryAliquotId(long libraryAliquotId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Run> results = listByLibraryAliquotBaseCriteria()
        .add(Restrictions.eq("aliquot.id", libraryAliquotId))
        .list();
    return results;
  }

  @Override
  public List<Run> listByLibraryIdList(Collection<Long> libraryIds) throws IOException {
    if (libraryIds == null || libraryIds.isEmpty()) {
      return Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<Long> ids = currentSession().createSQLQuery("SELECT rspc.Run_runId AS runId"
        + " FROM Run_SequencerPartitionContainer rspc"
        + " JOIN _Partition part ON part.containerId = rspc.containers_containerId"
        + " JOIN Pool_LibraryAliquot pla ON pla.poolId = part.pool_poolId"
        + " JOIN LibraryAliquot la ON la.aliquotId = pla.aliquotId"
        + " WHERE la.libraryId IN (:libraryIds)")
        .addScalar("runId", new LongType())
        .setParameterList("libraryIds", libraryIds)
        .list();

    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    List<Run> results = currentSession().createCriteria(Run.class)
        .add(Property.forName("id").in(ids))
        .list();

    return results;
  }

  private Criteria listByLibraryAliquotBaseCriteria() {
    return currentSession().createCriteria(Run.class)
        .createAlias("runPositions", "runPos")
        .createAlias("runPos.container", "spc")
        .createAlias("spc.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .createAlias("pool.poolElements", "element")
        .createAlias("element.aliquot", "aliquot");
  }

  @Override
  public List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException {
    // flush here because if Hibernate has not persisted recent changes to container-run relationships,
    // unexpected associations may
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
    @SuppressWarnings("unchecked")
    List<Long> ids = currentSession().createCriteria(Run.class, "r")
        .createAlias("runPositions", "runPos")
        .createAlias("runPos.container", "spc")
        .createAlias("spc.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .createAlias("pool.poolElements", "poolElement")
        .createAlias("poolElement.aliquot", "aliquot")
        .createAlias("aliquot.parentLibrary", "library")
        .createAlias("library.parentSample", "sample")
        .createAlias("sample.parentProject", "project")
        .add(Restrictions.eq("project.id", projectId))
        .setProjection(Projections.distinct(Projections.property("r.id")))
        .list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    List<Run> records = currentSession().createCriteria(Run.class)
        .add(Restrictions.in("id", ids))
        .list();
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
  public List<Run> listByIdList(Collection<Long> ids) throws IOException {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<Run> results = currentSession().createCriteria(Run.class)
        .add(Restrictions.in("id", ids))
        .list();
    return results;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public String getProjectColumn() {
    return "project.id";
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
    if ("platformType".equals(original))
      return "instrumentModel.platformType";
    if ("status".equals(original))
      return "health";
    if ("endDate".equals(original))
      return "completionDate";
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
  public Class<? extends Run> getRealClass() {
    return Run.class;
  }

  @Override
  public void restrictPaginationByProjectId(Criteria criteria, long projectId, Consumer<String> errorHandler) {
    criteria.createAlias("runPositions", "runPos")
        .createAlias("runPos.container", "spc")
        .createAlias("spc.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .createAlias("pool.poolElements", "poolElement")
        .createAlias("poolElement.aliquot", "aliquot")
        .createAlias("aliquot.parentLibrary", "library")
        .createAlias("library.parentSample", "sample")
        .createAlias("sample.parentProject", "project");
    HibernatePaginatedDataSource.super.restrictPaginationByProjectId(criteria, projectId, errorHandler);
  }

  @Override
  public void restrictPaginationBySequencingParametersName(Criteria criteria, String query,
      Consumer<String> errorHandler) {
    if (LimsUtils.isStringBlankOrNull(query)) {
      criteria.add(Restrictions.isNull("sequencingParameters"));
    } else {
      criteria.createAlias("sequencingParameters", "params");
      criteria.add(DbUtils.textRestriction(query, "params.name"));
    }
  }

  @Override
  public void restrictPaginationByHealth(Criteria criteria, EnumSet<HealthType> healths,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.in("health", healths.toArray()));
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("instrumentModel.platformType", platformType));
  }

  @Override
  public void restrictPaginationByIndex(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.createAlias("runPositions", "runPos")
        .createAlias("runPos.container", "spc")
        .createAlias("spc.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .createAlias("pool.poolElements", "poolElement")
        .createAlias("poolElement.aliquot", "aliquot")
        .createAlias("aliquot.parentLibrary", "library")
        .createAlias("library.index1", "index1")
        .createAlias("library.index2", "index2", JoinType.LEFT_OUTER_JOIN)
        .add(DbUtils.textRestriction(query, "index1.name", "index1.sequence", "index2.name", "index2.sequence"));
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

  @Override
  public List<Run> list(Consumer<String> errorHandler, int offset, int limit, boolean sortDir, String sortCol,
      PaginationFilter... filter)
      throws IOException {
    List<Run> runs = HibernatePaginatedDataSource.super.list(errorHandler, offset, limit, sortDir, sortCol, filter);
    if (runs.isEmpty()) {
      return runs;
    }

    @SuppressWarnings("unchecked")
    List<Object[]> results = currentSession().createCriteria(Run.class)
        .createAlias("runPositions", "position")
        .createAlias("position.container", "container")
        .createAlias("container.partitions", "partition")
        .createAlias("partition.pool", "pool")
        .createAlias("pool.poolElements", "element")
        .createAlias("element.aliquot", "aliquot")
        .createAlias("aliquot.parentLibrary", "library")
        .createAlias("library.parentSample", "sample")
        .createAlias("sample.parentProject", "project")
        .setProjection(Projections.distinct(
            Projections.projectionList()
                .add(Projections.property("id"))
                .add(Projections.property("project.code"))
                .add(Projections.property("project.name"))))
        .add(Restrictions.in("id", runs.stream().map(Run::getId).toArray()))
        .list();

    for (Run run : runs) {
      run.setProjectsLabel(results.stream()
          .filter(arr -> ((Long) arr[0]).longValue() == run.getId())
          .map(arr -> (String) (arr[1] == null ? arr[2] : arr[1]))
          .collect(Collectors.joining(", ")));
    }
    return runs;
  }

}
