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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.QualityControlEntity;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.store.PoolQcStore;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernatePoolQCDao implements PoolQcStore {
  protected static final Logger log = LoggerFactory.getLogger(HibernatePoolQCDao.class);

  @Autowired
  private SessionFactory sessionFactory;


  public Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public QC get(long qcId) throws IOException {
    return (PoolQC) currentSession().get(PoolQC.class, qcId);
  }


  @Override
  public QualityControlEntity getEntity(long id) throws IOException {
    return (Pool) currentSession().get(PoolImpl.class, id);
  }


  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public Collection<? extends QC> listForEntity(long id) throws IOException {
    return ((Pool) currentSession().get(PoolImpl.class, id)).getQCs();
  }

  @Override
  public long save(QC qc) throws IOException {
    PoolQC poolQC = (PoolQC) qc;
    if (poolQC.getId() == QC.UNSAVED_ID) {
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
