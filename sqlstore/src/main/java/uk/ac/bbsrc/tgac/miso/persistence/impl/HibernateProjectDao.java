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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProjectDao implements ProjectStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateProjectDao.class);

  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "alias",
      "description", "shortName" };

  @Autowired
  private SecurityStore securityStore;

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(ProjectImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public Project get(long projectId) throws IOException {
    Project result = (Project) currentSession().get(ProjectImpl.class, projectId);
    return result;
  }

  @Override
  public Project getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (Project) criteria.uniqueResult();
  }

  @Override
  public Project getByShortName(String shortName) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.add(Restrictions.eq("shortName", shortName));
    return (Project) criteria.uniqueResult();
  }

  @Override
  public Project getByStudyId(long studyId) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.createAlias("studies", "study");
    criteria.add(Restrictions.eq("study.id", studyId));
    return (Project) criteria.uniqueResult();
  }

  public SecurityStore getSecurityStore() {
    return securityStore;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public List<Project> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    @SuppressWarnings("unchecked")
    List<Project> results = criteria.list();
    return results;
  }

  @Override
  public List<Project> listAllWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.setMaxResults((int) limit);
    criteria.addOrder(Order.desc("id"));
    @SuppressWarnings("unchecked")
    List<Project> results = criteria.list();
    return results;
  }

  @Override
  public List<Project> listBySearch(String query) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.add(DbUtils.searchRestrictions(query, false, SEARCH_PROPERTIES));
    @SuppressWarnings("unchecked")
    List<Project> results = criteria.list();
    return results;
  }

  @Override
  public long save(Project project) throws IOException {
    if (!project.isSaved()) {
      return (Long) currentSession().save(project);
    } else {
      currentSession().update(project);
      return project.getId();
    }
  }
  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
