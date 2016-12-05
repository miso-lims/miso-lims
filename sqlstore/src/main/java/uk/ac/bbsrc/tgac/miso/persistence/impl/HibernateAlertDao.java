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
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.eaglegenomics.simlims.core.store.SecurityStore;

import net.sf.ehcache.CacheManager;

import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.event.impl.DefaultAlert;
import uk.ac.bbsrc.tgac.miso.core.event.type.AlertLevel;
import uk.ac.bbsrc.tgac.miso.core.store.AlertStore;

/**
 * uk.ac.bbsrc.tgac.miso.hibernate.persistence.impl
 *
 * @author Heather Armstrong
 * @since 0.2.43
 */

@Transactional(rollbackFor = Exception.class)
public class HibernateAlertDao implements AlertStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateAlertDao.class);

  private SecurityStore securityDao;

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private CacheManager cacheManager;

  public SecurityStore getSecurityDao() {
    return securityDao;
  }

  public void setSecurityStore(SecurityStore securityStore) {
    this.securityDao = securityStore;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public CacheManager getCacheManager() {
    return cacheManager;
  }

  public void setCacheManager(CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Override
  public Collection<Alert> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(DefaultAlert.class);
    @SuppressWarnings("unchecked")
    List<Alert> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Alert> listByUserId(long userId) throws IOException {
    Criteria criteria = currentSession().createCriteria(DefaultAlert.class);
    criteria.add(Restrictions.eq("user.id", userId));
    @SuppressWarnings("unchecked")
    List<Alert> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Alert> listByUserId(long userId, long limit) throws IOException {
    Criteria criteria = currentSession().createCriteria(DefaultAlert.class);
    criteria.add(Restrictions.eq("user.id", userId));
    criteria.setMaxResults((int) limit);
    @SuppressWarnings("unchecked")
    List<Alert> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Alert> listByAlertLevel(AlertLevel alertLevel) throws IOException {
    Criteria criteria = currentSession().createCriteria(DefaultAlert.class);
    criteria.add(Restrictions.eq("alertLevel", AlertLevel.get(alertLevel.getKey())));
    @SuppressWarnings("unchecked")
    List<Alert> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Alert> listUnreadByUserId(long userId) throws IOException {
    Criteria criteria = currentSession().createCriteria(DefaultAlert.class);
    criteria.add(Restrictions.eq("user.id", userId));
    criteria.add(Restrictions.eq("alertRead", false));
    @SuppressWarnings("unchecked")
    List<Alert> records = criteria.list();
    return records;
  }

  @Override
  public Collection<Alert> listUnreadByAlertLevel(AlertLevel alertLevel) throws IOException {
    Criteria criteria = currentSession().createCriteria(DefaultAlert.class);
    criteria.add(Restrictions.eq("alertLevel", AlertLevel.get(alertLevel.getKey())));
    criteria.add(Restrictions.eq("alertRead", false));
    @SuppressWarnings("unchecked")
    List<Alert> records = criteria.list();
    return records;
  }

  @Override
  public long save(Alert alert) throws IOException {
    return (long) currentSession().save(alert);
  }

  @Override
  public Alert get(long id) throws IOException {
    return (Alert) currentSession().get(DefaultAlert.class, id);
  }

  @Override
  public Alert lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public int count() throws IOException {
    Query query = currentSession().createQuery("select count(*) from DefaultAlert");
    return ((Long) query.uniqueResult()).intValue();
  }

  @Override
  public boolean remove(Alert alert) throws IOException {
    if (alert.isDeletable()) {
      long alertId = alert.getAlertId();
      currentSession().delete(alert);
      return get(alertId) == null;
    }
    return false;
  }
}
