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

import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.PlatformStore;

/**
 * uk.ac.bbsrc.tgac.miso.hibernate.persistence.impl
 *
 * @author Heather Armstrong
 * @since 0.2.43
 */

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernatePlatformDao implements PlatformStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernatePlatformDao.class);

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(Platform platform) throws IOException {
    return (long) currentSession().save(platform);
  }

  @Override
  public Platform get(long id) throws IOException {
    return (Platform) currentSession().get(Platform.class, id);
  }

  @Override
  public List<Platform> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(Platform.class);
    @SuppressWarnings("unchecked")
    List<Platform> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(Platform.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public Platform getByModel(String model) throws IOException {
    Criteria criteria = currentSession().createCriteria(Platform.class);
    criteria.add(Restrictions.eq("instrumentModel", model));
    return (Platform) criteria.uniqueResult();
  }

  @Override
  public List<Platform> listByName(String name) throws IOException {
    Criteria criteria = currentSession().createCriteria(Platform.class);
    criteria.add(Restrictions.eq("platformType", PlatformType.get(name)));
    @SuppressWarnings("unchecked")
    List<Platform> records = criteria.list();
    return records;
  }

  @Override
  public List<PlatformType> listDistinctPlatformNames() throws IOException {
    Criteria criteria = currentSession().createCriteria(Platform.class);
    criteria.setProjection(Projections.distinct(Projections.property("platformType")));
    @SuppressWarnings("unchecked")
    List<PlatformType> records = criteria.list();
    return records;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
