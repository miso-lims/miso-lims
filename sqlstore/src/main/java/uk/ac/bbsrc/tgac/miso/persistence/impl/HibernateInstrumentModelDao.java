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
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentModelStore;

/**
 * uk.ac.bbsrc.tgac.miso.hibernate.persistence.impl
 *
 * @author Heather Armstrong
 * @since 0.2.43
 */

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentModelDao implements InstrumentModelStore {

  protected static final Logger log = LoggerFactory.getLogger(HibernateInstrumentModelDao.class);

  @Autowired
  private JdbcTemplate template;

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public long save(InstrumentModel platform) throws IOException {
    return (long) currentSession().save(platform);
  }

  @Override
  public InstrumentModel get(long id) throws IOException {
    return (InstrumentModel) currentSession().get(InstrumentModel.class, id);
  }

  @Override
  public List<InstrumentModel> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentModel.class);
    @SuppressWarnings("unchecked")
    List<InstrumentModel> records = criteria.list();
    return records;
  }

  @Override
  public int count() throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentModel.class);
    return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
  }

  @Override
  public InstrumentModel getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentModel.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (InstrumentModel) criteria.uniqueResult();
  }

  @Override
  public List<InstrumentModel> listByPlatformType(String platformType) throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentModel.class);
    criteria.add(Restrictions.eq("platformType", PlatformType.get(platformType)));
    @SuppressWarnings("unchecked")
    List<InstrumentModel> records = criteria.list();
    return records;
  }

  @Override
  public List<PlatformType> listDistinctPlatformNames() throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentModel.class);
    criteria.setProjection(Projections.distinct(Projections.property("platformType")));
    @SuppressWarnings("unchecked")
    List<PlatformType> records = criteria.list();
    return records;
  }

  @Override
  public InstrumentPosition getInstrumentPosition(long positionId) throws IOException {
    return (InstrumentPosition) currentSession().get(InstrumentPosition.class, positionId);
  }

  private static final RowMapper<PlatformType> platformTypeMapper = (rs, rowNum) -> {
    String plat = rs.getString("platform");
    return PlatformType.valueOf(plat);
  };

  @Override
  public Set<PlatformType> listActivePlatformTypes() throws IOException {
    return Sets.newHashSet(getJdbcTemplate().query("SELECT platform FROM ActivePlatformTypes", platformTypeMapper));
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
