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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.persistence.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Printer;
import uk.ac.bbsrc.tgac.miso.core.store.PrinterStore;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePrinterDao implements PrinterStore, HibernatePaginatedDataSource<Printer> {
  protected static final Logger log = LoggerFactory.getLogger(HibernatePrinterDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(Printer.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public Printer get(long id) throws IOException {
    return (Printer) currentSession().get(Printer.class, id);
  }

  @Override
  public String getFriendlyName() {
    return "Printer";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends Printer> getRealClass() {
    return Printer.class;
  }

  @Override
  public String[] getSearchProperties() {
    return new String[] { "name" };
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Iterable<String> listAliases() {
    return Collections.emptySet();
  }

  @Override
  public Collection<Printer> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(Printer.class);
    @SuppressWarnings("unchecked")
    List<Printer> results = criteria.list();
    return results;
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    switch (original) {
    case "available":
      return "enabled";
    default:
      return original;
    }
  }

  @Override
  public String propertyForUserName(Criteria criteria, boolean creator) {
    return null;
  }

  @Override
  public boolean remove(Printer printer) throws IOException {
    if (!printer.isDeletable())
      return false;
    currentSession().delete(printer);
    return true;
  }

  @Override
  public long save(Printer t) throws IOException {
    if (t.getId() == Printer.UNSAVED_ID) {
      return (Long) currentSession().save(t);
    } else {
      currentSession().update(t);
      return t.getId();
    }
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
