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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.DateType;
import uk.ac.bbsrc.tgac.miso.persistence.InstrumentModelStore;

/**
 * uk.ac.bbsrc.tgac.miso.hibernate.persistence.impl
 *
 * @author Heather Armstrong
 * @since 0.2.43
 */

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateInstrumentModelDao extends HibernateSaveDao<InstrumentModel>
    implements HibernatePaginatedDataSource<InstrumentModel>, InstrumentModelStore {

  private static final String[] SEARCH_PROPERTIES = new String[] { "alias" };

  @Autowired
  private JdbcTemplate template;

  public HibernateInstrumentModelDao() {
    super(InstrumentModel.class);
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public Session currentSession() {
    return super.currentSession();
  }

  @Override
  public InstrumentModel getByAlias(String alias) throws IOException {
    Criteria criteria = currentSession().createCriteria(InstrumentModel.class);
    criteria.add(Restrictions.eq("alias", alias));
    return (InstrumentModel) criteria.uniqueResult();
  }

  private static final RowMapper<PlatformType> platformTypeMapper = (rs, rowNum) -> {
    String plat = rs.getString("platform");
    return PlatformType.valueOf(plat);
  };

  @Override
  public Set<PlatformType> listActivePlatformTypes() throws IOException {
    return Sets.newHashSet(getJdbcTemplate().query("SELECT platform FROM ActivePlatformTypes", platformTypeMapper));
  }

  @Override
  public long getUsage(InstrumentModel model) throws IOException {
    return getUsageBy(InstrumentImpl.class, "instrumentModel", model);
  }

  @Override
  public int getMaxContainersUsed(InstrumentModel model) throws IOException {
    @SuppressWarnings("unchecked")
    List<Instrument> instruments = currentSession().createCriteria(InstrumentImpl.class)
        .add(Restrictions.eq("instrumentModel", model))
        .list();

    if (instruments.isEmpty()) {
      return 0;
    }

    @SuppressWarnings("unchecked")
    List<Run> runs = currentSession().createCriteria(Run.class)
        .add(Restrictions.in("sequencer", instruments))
        .list();

    return runs.stream().mapToInt(run -> run.getRunPositions().size()).max().orElse(0);
  }

  @Override
  public InstrumentPosition getPosition(long id) {
    return (InstrumentPosition) currentSession().createCriteria(InstrumentPosition.class)
        .add(Restrictions.eq("positionId", id))
        .uniqueResult();
  }

  @Override
  public String getFriendlyName() {
    return "Instrument Model";
  }

  @Override
  public String getProjectColumn() {
    return null;
  }

  @Override
  public Class<? extends InstrumentModel> getRealClass() {
    return InstrumentModel.class;
  }

  @Override
  public String[] getSearchProperties() {
    return SEARCH_PROPERTIES;
  }

  @Override
  public Iterable<AliasDescriptor> listAliases() {
    return Collections.emptyList();
  }

  @Override
  public String propertyForDate(Criteria criteria, DateType type) {
    return null;
  }

  @Override
  public String propertyForSortColumn(String original) {
    return original;
  }

  @Override
  public String propertyForUser(boolean creator) {
    return null;
  }

  @Override
  public long createPosition(InstrumentPosition position) throws IOException {
    return (long) currentSession().save(position);
  }

  @Override
  public void deletePosition(InstrumentPosition position) throws IOException {
    currentSession().delete(position);
  }

  @Override
  public long getPositionUsage(InstrumentPosition position) throws IOException {
    return (long) currentSession().createCriteria(Run.class)
        .createAlias("runPositions", "runPosition")
        .add(Restrictions.eq("runPosition.position", position))
        .setProjection(Projections.rowCount()).uniqueResult();
  }
}
