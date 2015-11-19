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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractExperiment;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
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
public class HibernateExperimentStore extends HibernateDaoSupport implements Store<Experiment> {
  @Override
  @Transactional(readOnly = false)
  public long save(Experiment experiment) throws IOException {
    getHibernateTemplate().merge(experiment);
    return experiment.getExperimentId();
  }

  @Override
  @Transactional(readOnly = true)
  public Experiment get(long experimentId) throws IOException {
    return getHibernateTemplate().get(AbstractExperiment.class, experimentId);
  }

  @Override
  public Experiment lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  @Transactional(readOnly = true)
  @SuppressWarnings("unchecked")
  public Collection<Experiment> listAll() throws IOException {
    return null;
  }

  @Override
  public int count() throws IOException {
    return 0;
  }
}
