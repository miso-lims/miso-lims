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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Status;

/**
 * Defines a DAO interface for storing Statuses
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface StatusStore extends Store<Status> {
  /**
   * List all Statuses by their {@link uk.ac.bbsrc.tgac.miso.core.data.type.HealthType} given a string representing a HealthType key
   * 
   * @param health
   *          of type String
   * @return List<Status>
   */
  public List<Status> listByHealth(String health);

  /**
   * Get a Status object by a unique {@link uk.ac.bbsrc.tgac.miso.core.data.Run} name, i.e. run.getName()
   * 
   * @param runName
   * @return
   * @throws IOException
   */
  public Status getByRunName(String runName) throws IOException;

  /**
   * List all Statuses for a given sequencer machine name
   * 
   * @param sequencerName
   *          String
   * @return List<Status>
   */
  public Collection<Status> listAllBySequencerName(String sequencerName);
}
