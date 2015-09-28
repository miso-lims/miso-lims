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

import uk.ac.bbsrc.tgac.miso.core.store.Store;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Lane;

import java.io.IOException;
import java.util.Collection;

/**
 * com.eaglegenomics.miso.hibernatestore
 * <p/>
 * TODO Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Deprecated
public class HibernateLaneStore  extends HibernateDaoSupport implements Store<Lane> {
  @Transactional(readOnly = false)
  public long save(Lane lane) throws IOException {
    getHibernateTemplate().saveOrUpdate(lane);
    return lane.getId();
  }

  @Transactional(readOnly = true)
  public Lane get(long laneId) throws IOException {
    return (Lane) getHibernateTemplate().get(Lane.class, laneId);
  }

  @Override
  public Lane lazyGet(long id) throws IOException {
    return get(id);
  }

  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Collection<Lane> listAll() throws IOException {
/*    return (Collection<Lane>) getHibernateTemplate().execute(
            new HibernateCallback() {
              public Object doInHibernate(Session session)
                      throws HibernateException {
                return session.createQuery("from Lane").list(); 
              }
            });
            */
    return null;
  }

  @Override
  public int count() throws IOException {
    return 0;
  }
}
