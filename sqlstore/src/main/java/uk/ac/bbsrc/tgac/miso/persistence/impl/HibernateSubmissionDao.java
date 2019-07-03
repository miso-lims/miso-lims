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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.persistence.SubmissionStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSubmissionDao implements SubmissionStore {

  @Autowired
  private SessionFactory sessionFactory;

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(Submission.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public Submission get(long id) throws IOException {
    return (Submission) currentSession().get(Submission.class, id);
  }

  @Override
  public List<Submission> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(Submission.class);
    @SuppressWarnings("unchecked")
    List<Submission> results = criteria.list();
    return results;
  }

  @Override
  public long save(Submission submission) throws IOException {
    if (!submission.isSaved()) {
      return (Long) currentSession().save(submission);
    } else {
      currentSession().update(submission);
      return submission.getId();
    }
  }
}
