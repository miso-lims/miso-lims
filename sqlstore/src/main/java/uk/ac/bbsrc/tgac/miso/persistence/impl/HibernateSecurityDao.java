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
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
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
public class HibernateSecurityDao implements SecurityStore {
  private static final String USER_TABLE_NAME = "User";
  private static final String GROUP_TABLE_NAME = "_Group";

  protected static final Logger log = LoggerFactory.getLogger(HibernateSecurityDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public Group getGroupById(Long groupId) throws IOException {
    return (Group) currentSession().get(Group.class, groupId);
  }
  @Override
  public Group getGroupByName(String groupName) throws IOException {
    if (groupName == null) throw new NullPointerException("Can not get by null group name");
    Criteria criteria = currentSession().createCriteria(Group.class);
    criteria.add(Restrictions.eq("name", groupName));
    return (Group) criteria.uniqueResult();
  }

  @Override
  public Map<String, Integer> getGroupColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, GROUP_TABLE_NAME);
  }

  @CoverageIgnore
  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  @Override
  public SecurityProfile getSecurityProfileById(Long profileId) throws IOException {
    return (SecurityProfile) currentSession().get(SecurityProfile.class, profileId);
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public User getUserByEmail(String email) throws IOException {
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    criteria.add(Restrictions.eq("email", email));
    return (User) criteria.uniqueResult();
  }

  @Override
  public User getUserById(Long userId) throws IOException {
    return (User) currentSession().get(UserImpl.class, userId);
  }

  @Override
  public User getUserByLoginName(String loginName) throws IOException {
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    criteria.add(Restrictions.eq("loginName", loginName));
    return (User) criteria.uniqueResult();
  }

  @Override
  public User getUserByFullName(String fullName) throws IOException {
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    criteria.add(Restrictions.eq("fullName", fullName));
    return (User) criteria.uniqueResult();
  }

  @Override
  public Map<String, Integer> getUserColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(template, USER_TABLE_NAME);
  }

  @Override
  public Collection<Group> listAllGroups() throws IOException {
    Criteria criteria = currentSession().createCriteria(Group.class);
    @SuppressWarnings("unchecked")
    List<Group> results = criteria.list();
    return results;
  }

  @Override
  public Collection<User> listAllUsers() throws IOException {
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    @SuppressWarnings("unchecked")
    List<User> results = criteria.list();
    return results;
  }

  @Override
  public Collection<Group> listGroupsByIds(Collection<Long> groupIds) throws IOException {
    if (groupIds.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    criteria.add(Restrictions.in("id", groupIds));
    @SuppressWarnings("unchecked")
    List<Group> results = criteria.list();
    return results;
  }

  public Collection<Group> listGroupsByUserId(Long userId) throws IOException {
    Criteria criteria = currentSession().createCriteria(Group.class);
    criteria.createAlias("users", "user");
    criteria.add(Restrictions.eq("user.id", userId));
    @SuppressWarnings("unchecked")
    List<Group> results = criteria.list();
    return results;
  }

  @Override
  public Collection<User> listUsersByGroupName(String name) throws IOException {
    if (name == null) throw new NullPointerException("Can not search by null group name");
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    criteria.createAlias("groups", "group");
    criteria.add(Restrictions.eq("group.name", name));
    @SuppressWarnings("unchecked")
    List<User> results = criteria.list();
    return results;
  }

  @Override
  public Collection<User> listUsersByIds(Collection<Long> userIds) throws IOException {
    if (userIds.isEmpty()) {
      return Collections.emptyList();
    }
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    criteria.add(Restrictions.in("id", userIds));
    @SuppressWarnings("unchecked")
    List<User> results = criteria.list();
    return results;
  }

  @Override
  public long saveGroup(Group group) throws IOException {
    long id;
    if (group.getGroupId() == Group.UNSAVED_ID) {
      id = (Long) currentSession().save(group);
    } else {
      id = group.getGroupId();
      currentSession().update(group);
    }
    return id;
  }

  @Override
  public long saveUser(User user) throws IOException {
    long id;
    if (user.getUserId() == UserImpl.UNSAVED_ID) {
      id = (Long) currentSession().save(user);
    } else {
      id = user.getUserId();
      currentSession().update(user);
    }
    return id;
  }

  @CoverageIgnore
  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;

  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
