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

package uk.ac.bbsrc.tgac.miso.runstats.client.sql;

import java.io.IOException;
import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;

import uk.ac.bbsrc.tgac.miso.runstats.client.RunstatsStore;

/**
 * uk.ac.bbsrc.tgac.miso.runstats.client.sql
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 27/10/11
 * @since 0.1.2
 */
@Deprecated
public class SQLRunstatsDAO implements RunstatsStore {
  private JdbcTemplate template;

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public long save(Object o) throws IOException {
    return 0;
  }

  @Override
  public Object get(long id) throws IOException {
    return null;
  }

  @Override
  public Object lazyGet(long id) throws IOException {
    return null;
  }

  @Override
  public Collection listAll() throws IOException {
    return null;
  }

  @Override
  public int count() throws IOException {
    return 0;
  }
}
