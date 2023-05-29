package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot.RunPartitionAliquotId;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.ListLibraryAliquotView;
import uk.ac.bbsrc.tgac.miso.persistence.LibraryStore;
import uk.ac.bbsrc.tgac.miso.persistence.ListLibraryAliquotViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.RunPartitionAliquotDao;
import uk.ac.bbsrc.tgac.miso.persistence.RunStore;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateRunPartitionAliquotDao implements RunPartitionAliquotDao {

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private RunStore runStore;
  @Autowired
  private ListLibraryAliquotViewDao listLibraryAliquotViewDao;
  @Autowired
  private LibraryStore libraryStore;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public RunPartitionAliquot get(Run run, Partition partition, ListLibraryAliquotView aliquot) throws IOException {
    RunPartitionAliquotId id = new RunPartitionAliquotId();
    id.setRun(run);
    id.setPartition(partition);
    id.setAliquot(aliquot);
    RunPartitionAliquot result = (RunPartitionAliquot) currentSession().get(RunPartitionAliquot.class, id);
    if (result == null) {
      // ensure the relationship exists before constructing the entity
      List<Object[]> ids = queryIds("WHERE r.runId = ? AND part.partitionId = ? AND pla.aliquotId = ?",
          new long[] {run.getId(), partition.getId(), aliquot.getId()});
      if (ids.isEmpty()) {
        return null;
      }
      result = new RunPartitionAliquot(run, partition, aliquot);
    }
    return result;
  }

  @Override
  public List<RunPartitionAliquot> listByRunId(long runId) throws IOException {
    List<Object[]> ids = queryIds("WHERE r.runId = ?", new long[] {runId});

    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> results = currentSession().createCriteria(RunPartitionAliquot.class)
        .add(Restrictions.eq("run.id", runId))
        .list();

    constructMissing(ids, results);
    return results;
  }

  @Override
  public List<RunPartitionAliquot> listByAliquotId(long aliquotId) throws IOException {
    List<Object[]> ids = queryIds("WHERE pla.aliquotId = ?", new long[] {aliquotId});

    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> results = currentSession().createCriteria(RunPartitionAliquot.class)
        .add(Restrictions.eq("aliquot.aliquotId", aliquotId))
        .list();

    constructMissing(ids, results);
    return results;
  }

  @Override
  public List<RunPartitionAliquot> listByLibraryIdList(Collection<Long> libraryIds) throws IOException {
    if (libraryIds == null || libraryIds.isEmpty()) {
      return Collections.emptyList();
    }
    List<Object[]> ids = queryIds("JOIN LibraryAliquot la ON la.aliquotId = pla.aliquotId"
        + " JOIN Library l ON l.libraryId = la.libraryId"
        + " WHERE l.libraryId IN (:ids)", query -> query.setParameterList("ids", libraryIds));

    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> results = currentSession().createCriteria(RunPartitionAliquot.class)
        .createAlias("aliquot.parentLibrary", "library")
        .add(Restrictions.in("library.libraryId", libraryIds))
        .list();

    constructMissing(ids, results);
    return results;
  }

  private List<Object[]> queryIds(String additionalQuery, Consumer<SQLQuery> addParameters) {
    SQLQuery query = currentSession().createSQLQuery(
        "SELECT r.runId, part.partitionId, pla.aliquotId"
            + " FROM Run r"
            + " JOIN Run_SequencerPartitionContainer rspc ON rspc.Run_runId = r.runId"
            + " JOIN _Partition part ON part.containerId = rspc.containers_containerId"
            + " JOIN Pool_LibraryAliquot pla ON pla.poolId = part.pool_poolId" + " " + additionalQuery)
        .addScalar("runId", new LongType())
        .addScalar("partitionId", new LongType())
        .addScalar("aliquotId", new LongType());
    addParameters.accept(query);
    return query.list();
  }

  private List<Object[]> queryIds(String additionalQuery, long[] parameters) {
    return queryIds(additionalQuery, query -> {
      for (int i = 0; i < parameters.length; i++) {
        // parameters indices start at 1, but parameters array starts at 0
        query.setLong(i + 1, parameters[i]);
      }
    });
  }

  private void constructMissing(List<Object[]> ids, List<RunPartitionAliquot> results) throws IOException {
    List<Long[]> missingIds = new ArrayList<>();
    for (Object[] id : ids) {
      long rowRunId = parseLong(id[0]);
      long partitionId = parseLong(id[1]);
      long aliquotId = parseLong(id[2]);

      if (results.stream().noneMatch(x -> matches(x, rowRunId, partitionId, aliquotId))) {
        missingIds.add(new Long[] {rowRunId, partitionId, aliquotId});
      }
    }

    if (!missingIds.isEmpty()) {
      List<Run> runs = runStore.listByIdList(getIdComponent(missingIds, 0));
      List<Partition> partitions = currentSession().createCriteria(PartitionImpl.class)
          .add(Restrictions.in("id", getIdComponent(missingIds, 1)))
          .list();
      List<ListLibraryAliquotView> aliquots = listLibraryAliquotViewDao.listByIdList(getIdComponent(missingIds, 2));

      for (Long[] id : missingIds) {
        Run run = runs.stream().filter(x -> x.getId() == id[0].longValue()).findFirst().orElseThrow();
        Partition partition = partitions.stream().filter(x -> x.getId() == id[1].longValue()).findFirst().orElseThrow();
        ListLibraryAliquotView aliquot =
            aliquots.stream().filter(x -> x.getId() == id[2].longValue()).findFirst().orElseThrow();
        results.add(new RunPartitionAliquot(run, partition, aliquot));
      }
    }
  }

  private static long parseLong(Object value) {
    return Long.parseLong(value.toString());
  }

  private boolean matches(RunPartitionAliquot rpa, long runId, long partitionId, long aliquotId) {
    return rpa.getRun().getId() == runId
        && rpa.getPartition().getId() == partitionId
        && rpa.getAliquot().getId() == aliquotId;
  }

  private Set<Long> getIdComponent(List<Long[]> ids, int index) {
    return ids.stream().map(id -> id[index]).collect(Collectors.toSet());
  }

  @Override
  public void save(RunPartitionAliquot runPartitionAliquot) throws IOException {
    currentSession().save(runPartitionAliquot);
  }

  @Override
  public void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException {
    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> items = currentSession().createCriteria(RunPartitionAliquot.class)
        .createAlias("partition", "partition")
        .add(Restrictions.eq("run", run))
        .add(Restrictions.eq("partition.sequencerPartitionContainer", container))
        .list();

    for (RunPartitionAliquot item : items) {
      currentSession().delete(item);
    }
  }

  @Override
  public void deleteForPartition(Partition partition) throws IOException {
    @SuppressWarnings("unchecked")
    List<RunPartitionAliquot> items = currentSession().createCriteria(RunPartitionAliquot.class)
        .createAlias("partition", "partition")
        .add(Restrictions.eq("partition", partition))
        .list();

    for (RunPartitionAliquot item : items) {
      currentSession().delete(item);
    }
  }

  @Override
  public void deleteForPoolAliquot(Pool pool, long aliquotId) throws IOException {
    @SuppressWarnings("unchecked")
    List<Run> runs = currentSession().createCriteria(Run.class)
        .createAlias("runPositions", "runPosition")
        .createAlias("runPosition.container", "container")
        .createAlias("container.partitions", "partition")
        .add(Restrictions.eq("partition.pool", pool))
        .list();

    if (!runs.isEmpty()) {
      @SuppressWarnings("unchecked")
      List<RunPartitionAliquot> items = currentSession().createCriteria(RunPartitionAliquot.class)
          .createAlias("partition", "partition")
          .add(Restrictions.in("run", runs))
          .add(Restrictions.eq("aliquot.aliquotId", aliquotId))
          .list();

      for (RunPartitionAliquot item : items) {
        currentSession().delete(item);
      }
    }
  }

}
