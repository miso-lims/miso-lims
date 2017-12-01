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
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.store.SubmissionStore;
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
public class HibernateSubmissionDao implements SubmissionStore {
  protected static final Logger log = LoggerFactory.getLogger(HibernateSubmissionDao.class);

  private static final String TABLE_NAME = "Submission";

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private JdbcTemplate template;

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(Submission.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public Submission get(long id) throws IOException {
    return (Submission) currentSession().get(Submission.class, id);
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Map<String, Integer> getSubmissionColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Override
  public Collection<Submission> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(Submission.class);
    @SuppressWarnings("unchecked")
    List<Submission> results = criteria.list();
    return results;
  }

  @Override
  public long save(Submission submission) throws IOException {
    if (submission.getId() == Submission.UNSAVED_ID) {
      return (Long) currentSession().save(submission);
    } else {
      currentSession().update(submission);
      return submission.getId();
    }
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
