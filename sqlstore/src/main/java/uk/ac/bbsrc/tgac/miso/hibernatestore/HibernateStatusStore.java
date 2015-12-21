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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.hibernatestore;

import java.io.IOException;
import java.util.Collection;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StatusImpl;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

/**
 * com.eaglegenomics.miso.hibernatestore
 * <p/>
 * TODO Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Deprecated
public class HibernateStatusStore extends HibernateDaoSupport implements Store<Status> {
  @Override
  @Transactional(readOnly = false)
  public long save(Status status) throws IOException {
    getHibernateTemplate().saveOrUpdate(status);
    return status.getStatusId();
  }

  @Override
  @Transactional(readOnly = true)
  public Status get(long statusId) throws IOException {
    // may have to check for null before the cast
    return getHibernateTemplate().get(StatusImpl.class, statusId);
  }

  @Override
  public Status lazyGet(long id) throws IOException {
    return get(id);
  }

  @Transactional(readOnly = true)
  public Status get(String name) throws IOException {
    // may have to check for null before the cast
    return getHibernateTemplate().get(StatusImpl.class, name);
  }

  @Override
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Collection<Status> listAll() throws IOException {
    return null;
  }

  @Override
  public int count() throws IOException {
    return 0;
  }
}
