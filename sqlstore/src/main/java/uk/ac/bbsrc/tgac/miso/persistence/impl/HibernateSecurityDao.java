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
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;

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

  @Autowired
  private SessionFactory sessionFactory;

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

  public SessionFactory getSessionFactory() {
    return sessionFactory;
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
  public List<Group> listAllGroups() throws IOException {
    Criteria criteria = currentSession().createCriteria(Group.class);
    @SuppressWarnings("unchecked")
    List<Group> results = criteria.list();
    return results;
  }

  @Override
  public List<User> listAllUsers() throws IOException {
    Criteria criteria = currentSession().createCriteria(UserImpl.class);
    @SuppressWarnings("unchecked")
    List<User> results = criteria.list();
    return results;
  }

  @Override
  public long saveGroup(Group group) throws IOException {
    long id;
    if (!group.isSaved()) {
      id = (Long) currentSession().save(group);
    } else {
      id = group.getId();
      currentSession().update(group);
    }
    return id;
  }

  @Override
  public long saveUser(User user) throws IOException {
    long id;
    if (!user.isSaved()) {
      id = (Long) currentSession().save(user);
    } else {
      id = user.getId();
      currentSession().update(user);
    }
    return id;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
