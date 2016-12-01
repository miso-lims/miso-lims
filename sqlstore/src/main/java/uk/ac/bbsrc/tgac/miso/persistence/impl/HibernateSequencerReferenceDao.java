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
import java.util.Map;

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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.store.SequencerReferenceStore;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

@Repository
@Transactional(rollbackFor = Exception.class)
public class HibernateSequencerReferenceDao implements SequencerReferenceStore {

  private static final String SEQUENCER_REFERENCE_TABLE_NAME = "SequencerReference";

  protected static final Logger log = LoggerFactory.getLogger(HibernateSequencerReferenceDao.class);

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

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public long save(SequencerReference sr) throws IOException {
    long id;
    if (sr.getId() == AbstractSequencerReference.UNSAVED_ID) {
      id = (Long) currentSession().save(sr);
    } else {
      currentSession().update(sr);
      id = sr.getId();
    }
    return id;
  }

  @Override
  public SequencerReference get(long id) throws IOException {
    SequencerReference sr = (SequencerReference) currentSession().get(SequencerReferenceImpl.class, id);
    SequencerReference preUpgradeSR = getByUpgradedReference(id);
    if (preUpgradeSR != null) {
      sr.setPreUpgradeSequencerReference(preUpgradeSR);
    }
    return sr;
  }

  @Override
  public SequencerReference lazyGet(long id) throws IOException {
    return (SequencerReference) currentSession().get(SequencerReferenceImpl.class, id);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<SequencerReference> listAll() throws IOException {
    return currentSession().createCriteria(SequencerReferenceImpl.class).list();
  }

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(SequencerReferenceImpl.class)
        .setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  @Override
  public SequencerReference getByName(String referenceName) throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerReferenceImpl.class);
    criteria.add(Restrictions.eq("name", referenceName));
    return (SequencerReference) criteria.uniqueResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<SequencerReference> listByPlatformType(PlatformType platformType) throws IOException {
    Criteria criteria = currentSession().createCriteria(SequencerReferenceImpl.class);
    criteria.add(Restrictions.eq("platform.platformType", platformType));
    return criteria.list();
  }

  private SequencerReference getByUpgradedReference(long id) {
    Criteria criteria = currentSession().createCriteria(SequencerReferenceImpl.class);
    criteria.add(Restrictions.eq("upgradedSequencerReference.id", id));
    return (SequencerReference) criteria.uniqueResult();
  }

  @Override
  public boolean remove(SequencerReference sr) throws IOException {
    if (sr.isDeletable()) {
      currentSession().delete(sr);

      SequencerReference testIfExists = get(sr.getId());

      return testIfExists == null;
    } else {
      return false;
    }
  }

  @Override
  public Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException {
    return DbUtils.getColumnSizes(jdbcTemplate, SEQUENCER_REFERENCE_TABLE_NAME);
  }

}
