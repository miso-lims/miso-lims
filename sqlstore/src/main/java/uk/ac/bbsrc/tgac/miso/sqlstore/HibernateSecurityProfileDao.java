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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.util.Collection;
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

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.store.Store;

@Transactional(rollbackFor = Exception.class)
@Repository
public class HibernateSecurityProfileDao implements Store<SecurityProfile> {
  protected static final Logger log = LoggerFactory.getLogger(HibernateSecurityProfileDao.class);

  @Autowired
  private SessionFactory sessionFactory;

  @Override
  public int count() throws IOException {
    long c = (Long) currentSession().createCriteria(SecurityProfile.class).setProjection(Projections.rowCount()).uniqueResult();
    return (int) c;
  }

  private Session currentSession() {
    return getSessionFactory().getCurrentSession();
  }

  @Override
  public SecurityProfile get(long id) throws IOException {
    SecurityProfile sp = (SecurityProfile) currentSession().get(SecurityProfile.class, id);
    return sp == null ? new SecurityProfile() : sp;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public SecurityProfile lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<SecurityProfile> listAll() throws IOException {
    Criteria critiera = currentSession().createCriteria(SecurityProfile.class);
    @SuppressWarnings("unchecked")
    List<SecurityProfile> results = critiera.list();
    return results;
  }

  @Override
  public long save(SecurityProfile securityProfile) throws IOException {
    long id;
    if (securityProfile.getProfileId() == SecurityProfile.UNSAVED_ID) {
      id = (Long) currentSession().save(securityProfile);
    } else {
      currentSession().update(securityProfile);
      id = securityProfile.getProfileId();
    }
    return id;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }
}
