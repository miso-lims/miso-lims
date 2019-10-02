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

package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.InstrumentModel;
import uk.ac.bbsrc.tgac.miso.core.data.InstrumentPosition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Platforms
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface InstrumentModelStore extends PaginatedDataSource<InstrumentModel>, SaveDao<InstrumentModel> {

  /**
   * Get an Instrument Model given a model alias
   * 
   * @param alias
   * @return Platform
   * @throws IOException
   */
  public InstrumentModel getByAlias(String alias) throws IOException;

  public Set<PlatformType> listActivePlatformTypes() throws IOException;

  public long getUsage(InstrumentModel model) throws IOException;

  /**
   * @param model
   * @return the max number of containers attached to a run on an instrument of the specified model
   * @throws IOException
   */
  public int getMaxContainersUsed(InstrumentModel model) throws IOException;

  public InstrumentPosition getPosition(long id) throws IOException;

  public long createPosition(InstrumentPosition position) throws IOException;

  public void deletePosition(InstrumentPosition position) throws IOException;

  public long getPositionUsage(InstrumentPosition position) throws IOException;

}
