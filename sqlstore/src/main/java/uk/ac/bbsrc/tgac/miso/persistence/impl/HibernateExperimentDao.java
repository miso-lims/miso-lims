/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK MISO project contacts: Robert Davey @
 * TGAC *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MISO. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment.RunPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment_;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Run_;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Submission_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl_;
import uk.ac.bbsrc.tgac.miso.persistence.ExperimentStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateExperimentDao extends HibernateSaveDao<Experiment> implements ExperimentStore {

  public HibernateExperimentDao() {
    super(Experiment.class);
  }

  @Override
  public List<Experiment> listAllWithLimit(long limit) throws IOException {
    return new QueryBuilder<>(currentSession(), Experiment.class, Experiment.class).getResultList((int) limit, 0);
  }

  @Override
  public List<Experiment> listByStudyId(long studyId) {
    QueryBuilder<Experiment, Experiment> builder =
        new QueryBuilder<>(currentSession(), Experiment.class, Experiment.class);
    Root<Experiment> root = builder.getRoot();
    Join<Experiment, StudyImpl> studyJoin = builder.getJoin(root, Experiment_.study);
    builder.addPredicate(builder.getCriteriaBuilder().equal(studyJoin.get(StudyImpl_.studyId), studyId));
    return builder.getResultList();
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
