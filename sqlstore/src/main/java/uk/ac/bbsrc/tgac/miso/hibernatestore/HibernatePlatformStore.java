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
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;

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
public class HibernatePlatformStore  extends HibernateDaoSupport implements Store<Platform> {
  @Transactional(readOnly = false)
  public long save(Platform platform) throws IOException {
    getHibernateTemplate().saveOrUpdate(platform);
    return platform.getPlatformId();
  }

  @Transactional(readOnly = true)
  public Platform get(long platformId) throws IOException {
    return (Platform) getHibernateTemplate().load(PlatformImpl.class, platformId);
  }

  @Override
  public Platform lazyGet(long id) throws IOException {
    return get(id);
  }

  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Collection<Platform> listAll() throws IOException {
/*    return (Collection<Platform>) getHibernateTemplate().execute(
            new HibernateCallback() {
              public Object doInHibernate(Session session)
                      throws HibernateException {
                return session.createQuery("from AbstractPlatform").list();
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
