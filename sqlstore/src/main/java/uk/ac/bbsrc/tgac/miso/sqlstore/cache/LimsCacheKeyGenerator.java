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

package uk.ac.bbsrc.tgac.miso.sqlstore.cache;

import com.googlecode.ehcache.annotations.key.AbstractCacheKeyGenerator;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore.cache
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class LimsCacheKeyGenerator extends AbstractCacheKeyGenerator<Long> {
  public LimsCacheKeyGenerator() {
  }

  public LimsCacheKeyGenerator(boolean includeMethod, boolean includeParameterTypes) {
    super(includeMethod, includeParameterTypes);
  }

  @Override
  public Long generateKey(Object... objects) {
    return this.deepIDCode(objects);
  }

  protected final Long deepIDCode(Object o[]) {
    Long code = 0L;
    if (o == null) return code;

    for (final Object a : o) {
      if (a instanceof Partition) {
        code = ((Partition) a).getId();
      }
      if (a instanceof Project) {
        code = ((Project) a).getId();
      } else if (a instanceof Experiment) {
        code = ((Experiment) a).getId();
      } else if (a instanceof Study) {
        code = ((Study) a).getId();
      }
    }
    return code;
  }
}
