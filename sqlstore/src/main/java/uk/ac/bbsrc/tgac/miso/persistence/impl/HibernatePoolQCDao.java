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
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractQC;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolQCDao implements PoolQcStore {
  protected static final Logger log = LoggerFactory.getLogger(HibernatePoolQCDao.class);

  private static final String TABLE_NAME = "PoolQC";

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(PoolQCImpl.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public PoolQC get(long qcId) throws IOException {
    return (PoolQC) currentSession().get(PoolQCImpl.class, qcId);
  }

  @Override
  public QcType getPoolQcTypeById(long qcTypeId) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTypeId", qcTypeId));
    criteria.add(Restrictions.eq("qcTarget", "Pool"));
    return (QcType) criteria.uniqueResult();
  }

  @Override
  public QcType getPoolQcTypeByName(String qcName) throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("name", qcName));
    criteria.add(Restrictions.eq("qcTarget", "Pool"));
    return (QcType) criteria.uniqueResult();
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public PoolQC lazyGet(long qcId) throws IOException {
    return get(qcId);
  }

  @Override
  public Collection<PoolQC> listAll() throws IOException {
    Criteria criteria = currentSession().createCriteria(PoolQCImpl.class);
    @SuppressWarnings("unchecked")
    List<PoolQC> results = criteria.list();
    return results;
  }

  @Override
  public Collection<QcType> listAllPoolQcTypes() throws IOException {
    Criteria criteria = currentSession().createCriteria(QcType.class);
    criteria.add(Restrictions.eq("qcTarget", "Pool"));
    @SuppressWarnings("unchecked")
    List<QcType> results = criteria.list();
    return results;
  }

  @Override
  public Collection<PoolQC> listByPoolId(long poolId) throws IOException {
    Criteria criteria = currentSession().createCriteria(PoolQCImpl.class);
    criteria.add(Restrictions.eq("pool.id", poolId));
    @SuppressWarnings("unchecked")
    List<PoolQC> results = criteria.list();
    return results;
  }

  @Override
  public boolean remove(PoolQC qc) throws IOException {
    if (qc.getId() == AbstractQC.UNSAVED_ID) {
      return false;
    }
    currentSession().delete(qc);
    return true;
  }

  @Override
  public long save(PoolQC poolQC) throws IOException {
    if (poolQC.getId() == AbstractQC.UNSAVED_ID) {
      return (Long) currentSession().save(poolQC);
    } else {
      currentSession().update(poolQC);
      return poolQC.getId();
    }
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
