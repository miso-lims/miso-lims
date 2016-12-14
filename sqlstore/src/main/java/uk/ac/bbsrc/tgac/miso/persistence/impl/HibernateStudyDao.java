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
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

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
public class HibernateStudyDao implements StudyStore {
  private static final String TABLE_NAME = "Study";

  protected static final Logger log = LoggerFactory.getLogger(HibernateStudyDao.class);

  @Autowired
  private JdbcTemplate template;
  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private NamingScheme namingScheme;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void setNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  @CoverageIgnore
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(Study study) throws IOException {
    long id;
    if (study.getId() == StudyImpl.UNSAVED_ID) {
      try {
      namingScheme.generateNameFor(study);
      id = (Long) currentSession().save(study);
      } catch (MisoNamingException e) {
        throw new IOException(e);
      }
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
    return currentSession().createCriteria(StudyImpl.class).setMaxResults((int) limit).list();
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(StudyImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public List<Study> listBySearch(String query) {
    Criteria criteria = currentSession().createCriteria(StudyType.class);
    criteria.add(DbUtils.searchRestrictions(query, "alias", "name", "description"));
    @SuppressWarnings("unchecked")
    List<Study> results = criteria.list();
    return results;
  }

  @Override
  public boolean remove(Study study) throws IOException {
    if (study.isDeletable()) {
      currentSession().delete(study);
      return true;
    }
    return false;
  }

  @Override
  public Study get(long studyId) throws IOException {
    return (Study) currentSession().get(StudyImpl.class, studyId);
  }

  @CoverageIgnore
  @Override
  public Study lazyGet(long studyId) throws IOException {
    return get(studyId);
  }

  @Override
  public List<Study> listByProjectId(long projectId) throws IOException {
    Criteria criteria = currentSession().createCriteria(StudyType.class);
    criteria.createAlias("project", "project");
    criteria.add(Restrictions.eq("project.id", projectId));
    @SuppressWarnings("unchecked")
    List<Study> results = criteria.list();
    return results;
  }

  @Override
  public List<StudyType> listAllStudyTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(StudyType.class);
    @SuppressWarnings("unchecked")
    List<StudyType> results = criteria.list();
    return results;
  }

  @Override
  public Map<String, Integer> getStudyColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }
}
