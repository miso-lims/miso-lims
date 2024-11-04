package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment_;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Submission_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.ExperimentStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateExperimentDao extends HibernateSaveDao<Experiment> implements ExperimentStore {

  public HibernateExperimentDao() {
    super(Experiment.class);
  }

  @Override
  public Collection<Experiment> listByLibrary(long id) throws IOException {
    QueryBuilder<Experiment, Experiment> builder =
        new QueryBuilder<>(currentSession(), Experiment.class, Experiment.class);
    Root<Experiment> root = builder.getRoot();
    Join<Experiment, LibraryImpl> libraryJoin = builder.getJoin(root, Experiment_.library);
    builder.addPredicate(builder.getCriteriaBuilder().equal(libraryJoin.get(LibraryImpl_.libraryId), id));
    return builder.getResultList();
  }

  @Override
  public List<Experiment> listByRun(long runId) throws IOException {
    QueryBuilder<Long, Experiment> idBuilder =
        new QueryBuilder<>(currentSession(), Experiment.class, Long.class);
    Join<Experiment, RunPartition> rpJoin = idBuilder.getJoin(idBuilder.getRoot(), Experiment_.runPartitions);
    Join<RunPartition, Run> runJoin = idBuilder.getSingularJoin(rpJoin, "run", Run.class);
    idBuilder.addPredicate(idBuilder.getCriteriaBuilder().equal(runJoin.get(Run_.runId), runId));
    idBuilder.setColumns(idBuilder.getRoot().get(Experiment_.EXPERIMENT_ID));
    List<Long> ids = idBuilder.getResultList();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }

    QueryBuilder<Experiment, Experiment> builder =
        new QueryBuilder<>(currentSession(), Experiment.class, Experiment.class);
    In<Long> inClause = builder.getCriteriaBuilder().in(builder.getRoot().get(Experiment_.experimentId));
    for (Long id : ids) {
      inClause.value(id);
    }
    builder.addPredicate(inClause);
    return builder.getResultList();
  }

  @Override
  public long getUsage(Experiment experiment) throws IOException {
    return getUsageInCollection(Submission.class, Submission_.EXPERIMENTS, experiment);
  }

}
