/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.StudyStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateStudyDao implements StudyStore, HibernatePaginatedDataSource<Study> {
  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "alias", "description" };
  private static final Iterable<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(new AliasDescriptor("project"));

  protected static final Logger log = LoggerFactory.getLogger(HibernateStudyDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(Study study) throws IOException {
    long id;
    if (!study.isSaved()) {
      id = (Long) currentSession().save(study);
    } else {
      currentSession().update(study);
      id = study.getId();
    }
    return id;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Study> listAll() {
    return currentSession().createCriteria(StudyImpl.class).list();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Study> listAllWithLimit(long limit) throws IOException {
    if (limit == 0) return Collections.emptyList();
    return currentSession().createCriteria(StudyImpl.class).setMaxResults((int) limit).list();
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(StudyImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public List<Study> listBySearch(String query) {
    Criteria criteria = currentSession().createCriteria(StudyImpl.class);
    criteria.add(DbUtils.searchRestrictions(query, false, "alias", "name", "description"));
    @SuppressWarnings("unchecked")
    List<Study> results = criteria.list();
    return results;
  }

  @Override
  public Study get(long studyId) throws IOException {
    return (Study) currentSession().get(StudyImpl.class, studyId);
  }

  @Override
  public Study getByAlias(String alias) throws IOException {
    return (Study) currentSession().createCriteria(StudyImpl.class)
        .add(Restrictions.eq("alias", alias))
        .uniqueResult();
  }

  @Override
  public List<Study> listByProjectId(long projectId) throws IOException {
    Criteria criteria = currentSession().createCriteria(StudyImpl.class);
    criteria.createAlias("project", "project");
    criteria.add(Restrictions.eq("project.id", projectId));
    @SuppressWarnings("unchecked")
    List<Study> results = criteria.list();
    return results;
  }

  @Override
  public String getFriendlyName() {
    return "Study";
  }

  @Override
  public String getProjectColumn() {
    return "project.id";
  }

  @Override
  public Class<? extends Study> getRealClass() {
    return StudyImpl.class;
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
  public String propertyForDate(Criteria criteria, DateType type) {
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
    case "studyTypeId":
      return "studyType.id";
    default:
    return original;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return creator ? "creator" : "lastModifier";
  }

  @Override
  public long getUsage(Study study) throws IOException {
    return (long) currentSession().createCriteria(Experiment.class)
        .add(Restrictions.eq("study", study))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

}
