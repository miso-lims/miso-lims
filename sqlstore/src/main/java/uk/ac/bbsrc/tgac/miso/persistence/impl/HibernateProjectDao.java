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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateProjectDao implements ProjectStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateProjectDao.class);

  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "alias",
      "description", "shortName" };
  private static final String TABLE_NAME = "Project";

  @Autowired
  private SecurityStore securityStore;

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  @Override
  public void addWatcher(Project project, User watcher) {
    log.debug("Adding watcher " + watcher.getLoginName() + " to " + project.getName());
    project.addWatcher(watcher);
    currentSession().update(project);
  }

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
    return withWatcherGroup(result);
  }

  @Override
  public Project getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.add(Restrictions.eq("alias", alias));
    return withWatcherGroup((Project) criteria.uniqueResult());
  }

  @Override
  public Project getByShortName(String shortName) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.add(Restrictions.eq("shortName", shortName));
    return withWatcherGroup((Project) criteria.uniqueResult());
  }

  @Override
  public Project getByStudyId(long studyId) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.createAlias("studies", "study");
    criteria.add(Restrictions.eq("study.id", studyId));
    return withWatcherGroup((Project) criteria.uniqueResult());
  }

  @CoverageIgnore
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @Override
  public Map<String, Integer> getProjectColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, TABLE_NAME);
  }

  @Override
  @CoverageIgnore
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    return withWatcherGroup((ProjectOverview) currentSession().get(ProjectOverview.class, overviewId));
  }

  private Group getProjectWatcherGroup() throws IOException {
    return getSecurityStore().getGroupByName("ProjectWatchers");
  }

  public SecurityStore getSecurityStore() {
    return securityStore;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public ProjectOverview lazyGetProjectOverviewById(long overviewId) throws IOException {
    return getProjectOverviewById(overviewId);
  }

  @Override
  public List<Project> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    @SuppressWarnings("unchecked")
    List<Project> results = criteria.list();
    return withWatcherGroup(results);
  }
  @Override
  public List<Project> listAllWithLimit(long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.setMaxResults((int) limit);
    criteria.addOrder(Order.desc("projectId"));
    @SuppressWarnings("unchecked")
    List<Project> results = criteria.list();
    return withWatcherGroup(results);
  }

  @Override
  public List<Project> listBySearch(String query) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectImpl.class);
    criteria.add(DbUtils.searchRestrictions(query, false, SEARCH_PROPERTIES));
    @SuppressWarnings("unchecked")
    List<Project> results = criteria.list();
    return withWatcherGroup(results);
  }

  @Override
  public List<ProjectOverview> listOverviewsByProjectId(long projectId) throws IOException {
    Criteria criteria = currentSession().createCriteria(ProjectOverview.class);
    criteria.createAlias("project", "project");
    criteria.add(Restrictions.eq("project.id", projectId));
    @SuppressWarnings("unchecked")
    List<ProjectOverview> results = criteria.list();
    return withOverviewWatcherGroup(results);
  }

  public boolean removeOverview(ProjectOverview overview) throws IOException {
    if (overview.isDeletable()) {
      overview.setProject(null);
      currentSession().delete(overview);
      return true;
    }
    return false;
  }

  @Override
  public void removeWatcher(Project project, User watcher) {
    log.debug("Removing watcher " + watcher.getLoginName() + " from " + project.getWatchableIdentifier());
    project.removeWatcher(watcher);
    currentSession().update(project);
  }

  @Override
  public long save(Project project) throws IOException {
    Date timestamp = new Date();
    project.setLastUpdated(timestamp);
    if (project.getId() == ProjectImpl.UNSAVED_ID) {
      project.setCreationDate(timestamp);
      return (Long) currentSession().save(project);
    } else {
      currentSession().update(project);
      return project.getProjectId();
    }
  }

  @Override
  public long saveOverview(ProjectOverview overview) throws IOException {
    long id;
    if (overview.getId() == ProjectOverview.UNSAVED_ID) {
      id = (Long) currentSession().save(overview);
    } else {
      currentSession().update(overview);
      id = overview.getId();
    }
    return id;
  }

  @CoverageIgnore
  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityStore = securityStore;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  private List<ProjectOverview> withOverviewWatcherGroup(List<ProjectOverview> projectoverviews) throws IOException {
    Group group = getProjectWatcherGroup();
    for (ProjectOverview overview : projectoverviews) {
      overview.setWatchGroup(group);
    }
    return projectoverviews;
  }

  private List<Project> withWatcherGroup(List<Project> projects) throws IOException {
    Group group = getProjectWatcherGroup();
    for (Project project : projects) {
      project.setWatchGroup(group);
    }
    return projects;
  }

  private Project withWatcherGroup(Project project) throws IOException {
    if (project != null) {
      project.setWatchGroup(getProjectWatcherGroup());
    }
    return project;
  }

  private ProjectOverview withWatcherGroup(ProjectOverview projectoverview) throws IOException {
    if (projectoverview != null) {
      projectoverview.setWatchGroup(getProjectWatcherGroup());
    }
    return projectoverview;
  }
}
