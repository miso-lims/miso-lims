/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK MISO project contacts: Robert Davey,
 * Mario Caccamo @ TGAC *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MISO. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */
package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QC;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentStore;
import uk.ac.bbsrc.tgac.miso.persistence.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentDao implements InstrumentStore, HibernatePaginatedDataSource<Instrument> {

  private static final String[] SEARCH_PROPERTIES = new String[] {"name", "serialNumber", "identificationBarcode"};
  private static final List<AliasDescriptor> STANDARD_ALIASES = Arrays.asList(
      new AliasDescriptor("instrumentModel"),
      new AliasDescriptor("workstation", JoinType.LEFT_OUTER_JOIN));

  @Autowired
  private SessionFactory sessionFactory;

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
    if (!instrument.isSaved()) {
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

  @Override
  public List<Instrument> listAll() throws IOException {
    @SuppressWarnings("unchecked")
    List<Instrument> results = currentSession().createCriteria(InstrumentImpl.class).list();
    return results;
  }

  @Override
  public List<Instrument> listByType(InstrumentType type) throws IOException {
    @SuppressWarnings("unchecked")
    List<Instrument> results = currentSession().createCriteria(InstrumentImpl.class)
        .createAlias("instrumentModel", "instrumentModel")
        .add(Restrictions.eq("instrumentModel.instrumentType", type))
        .list();
    return results;
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

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return STANDARD_ALIASES;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return type == DateType.CREATE ? "dateCommissioned" : null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
      case "platformType":
        return "instrumentModel.platformType";
      case "instrumentModelAlias":
        return "instrumentModel.alias";
      case "workstationAlias":
        return "workstation.alias";
      default:
        return original;
    }
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public void restrictPaginationByPlatformType(Criteria criteria, PlatformType platformType,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("instrumentModel.platformType", platformType));
  }

  @Override
  public void restrictPaginationByArchived(Criteria criteria, boolean isArchived, Consumer<String> errorHandler) {
    criteria.add(isArchived ? Restrictions.isNotNull("dateDecommissioned") : Restrictions.isNull("dateDecommissioned"));
  }

  @Override
  public void restrictPaginationByInstrumentType(Criteria criteria, InstrumentType type,
      Consumer<String> errorHandler) {
    criteria.add(Restrictions.eq("instrumentModel.instrumentType", type));
  }

  @Override
  public void restrictPaginationByModel(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "instrumentModel.alias"));
  }

  @Override
  public void restrictPaginationByWorkstation(Criteria criteria, String query, Consumer<String> errorHandler) {
    criteria.add(DbUtils.textRestriction(query, "workstation.alias", "workstation.identificationBarcode"));
  }

  @Override
  public long getUsageByRuns(Instrument instrument) throws IOException {
    return (long) currentSession().createCriteria(Run.class)
        .add(Restrictions.eq("sequencer", instrument))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public long getUsageByArrayRuns(Instrument instrument) throws IOException {
    return (long) currentSession().createCriteria(ArrayRun.class)
        .add(Restrictions.eq("instrument", instrument))
        .setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public long getUsageByQcs(Instrument instrument) throws IOException {
    @SuppressWarnings("unchecked")
    List<Long> counts = currentSession().createCriteria(QC.class)
        .add(Restrictions.eq("instrument", instrument))
        .setProjection(Projections.rowCount()).list(); // returns one count per QC table (samples, libraries...)
    return counts.stream().mapToLong(Long::longValue).sum();
  }

  @Override
  public Instrument getByServiceRecord(ServiceRecord record) throws IOException {
    return (Instrument) currentSession().createCriteria(Instrument.class)
        .createAlias("serviceRecords", "serviceRecords")
        .add(Restrictions.eq("serviceRecords.recordId", record.getId()))
        .uniqueResult();
  }

}
