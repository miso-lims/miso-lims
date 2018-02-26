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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractInstrument;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.InstrumentStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentDao implements InstrumentStore, HibernatePaginatedDataSource<Instrument> {

  private static final String INSTRUMENT_TABLE_NAME = "Instrument";

  protected static final Logger log = LoggerFactory.getLogger(HibernateInstrumentDao.class);

  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public long save(Instrument instrument) throws IOException {
    long id;
    if (instrument.getId() == AbstractInstrument.UNSAVED_ID) {
      id = (Long) currentSession().save(instrument);
    } else {
      currentSession().update(instrument);
      id = instrument.getId();
    }
    return id;
  }

  @Override
  public Instrument get(long id) throws IOException {
    return (Instrument) currentSession().get(InstrumentImpl.class, id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Instrument> listAll() throws IOException {
    return currentSession().createCriteria(InstrumentImpl.class).list();
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(InstrumentImpl.class)
        .setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public Instrument getByName(String name) throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentImpl.class);
    criteria.add(Restrictions.eq("name", name));
    return (Instrument) criteria.uniqueResult();
  }


  @Override
  public Instrument getByUpgradedInstrument(long id) {
    Criteria criteria = currentSession().createCriteria(InstrumentImpl.class);
    criteria.add(Restrictions.eq("upgradedInstrument.id", id));
    return (Instrument) criteria.uniqueResult();
  }

  @Override
  public Map<String, Integer> getInstrumentColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(jdbcTemplate, INSTRUMENT_TABLE_NAME);
  }

  @Override
  public String getFriendlyName() {
    return "Instrument";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Instrument> getRealClass() {
    return InstrumentImpl.class;
  }

  private static final String[] SEARCH_PROPERTIES = new String[] { "name", "platform.instrumentModel" };

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  private static final List<String> STANDARD_ALIASES = Arrays.asList("platform");

  @Override
  public Iterable<String> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return type == DateType.CREATE ? "dateComissioned":null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platform.platformType", platformType));
  }

  @Override
  public void restrictPaginationByArchived(Criteria criteria, boolean isArchived, Consumer<String> errorHandler) {
    criteria.add(isArchived ? Restrictions.isNotNull("dateDecommissioned") : Restrictions.isNull("dateDecommissioned"));
  }

  @Override
  public void restrictPaginationByInstrumentType(Criteria criteria, InstrumentType type, Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("platform.instrumentType", type));
  }

}
