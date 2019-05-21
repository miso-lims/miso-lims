/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.store.ExperimentStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateExperimentDao implements ExperimentStore {

  @Autowired
  private SessionFactory sessionFactory;
  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(Experiment.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public Experiment get(long experimentId) throws IOException {
    return (Experiment) currentSession().get(Experiment.class, experimentId);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public List<Experiment> listAll() {
    Criteria criteria = currentSession().createCriteria(Experiment.class);
    @SuppressWarnings("unchecked")
    List<Experiment> results = criteria.list();
    return results;
  }

  @Override
  public List<Experiment> listAllWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(Experiment.class);
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Experiment> results = criteria.list();
    return results;
  }

  @Override
  public List<Experiment> listBySearch(String query) {
    Criteria criteria = currentSession().createCriteria(Experiment.class);
    criteria.add(DbUtils.searchRestrictions(query, false, "name", "alias", "description"));
    @SuppressWarnings("unchecked")
    List<Experiment> results = criteria.list();
    return results;
  }

  @Override
  public List<Experiment> listByStudyId(long studyId) {
    Criteria criteria = currentSession().createCriteria(Experiment.class);
    criteria.createAlias("study", "study");
    criteria.add(Restrictions.eq("study.id", studyId));
    @SuppressWarnings("unchecked")
    List<Experiment> results = criteria.list();
    return results;
  }

  /**
   * Writes the given experiment to the database, using the default transaction strategy configured for the datasource.
   *
   * @param experiment
   *          the experiment to write
   */
  @Override
  public long save(Experiment experiment) throws IOException {
    long id;
    if (!experiment.isSaved()) {
      id = (Long) currentSession().save(experiment);
    } else {
      currentSession().update(experiment);
      id = experiment.getId();
    }
    return id;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Collection<Experiment> listByLibrary(long id) throws IOException {
    Criteria criteria = currentSession().createCriteria(Experiment.class);
    criteria.createAlias("library", "library");
    criteria.add(Restrictions.eq("library.id", id));
    @SuppressWarnings("unchecked")
    List<Experiment> results = criteria.list();
    return results;
  }

  @Override
  public List<Experiment> listByRun(long runId) throws IOException {
    Criteria idCriteria = currentSession().createCriteria(Experiment.class);
    idCriteria.createCriteria("runPartitions").createAlias("run", "run").add(Restrictions.eq("run.id", runId));
    idCriteria.setProjection(Projections.distinct(Projections.property("id")));
    @SuppressWarnings("unchecked")
    List<Long> ids = idCriteria.list();
    if (ids.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(Experiment.class);
    criteria.add(Restrictions.in("id", ids));
    @SuppressWarnings("unchecked")
    List<Experiment> results = criteria.list();
    return results;
  }

}
