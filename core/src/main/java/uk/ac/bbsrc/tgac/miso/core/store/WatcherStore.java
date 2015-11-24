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

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Watchable;

/**
 * uk.ac.bbsrc.tgac.miso.core.store
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 05/12/11
 * @since 0.1.3
 */
public interface WatcherStore {
  /**
   * Get the list of Users that are watching an entity given an entity name (defined by that entity's {@link Watchable}
   * getWatchableIdentifier method)
   * 
   * @param entityName
   *          of type String
   * @return Collection<User>
   * @throws IOException
   */
  Collection<User> getWatchersByEntityName(String entityName) throws IOException;

  /**
   * Get the list of Users that are in a given watcher group
   * 
   * @param groupName
   *          of type String
   * @return Collection<User>
   * @throws IOException
   */
  Collection<User> getWatchersByWatcherGroup(String groupName) throws IOException;

  /**
   * Unregister a watched entity / user coupling
   * 
   * @param watchable
   *          of type Watchable
   * @param user
   *          of type User
   * @throws IOException
   * @return true if removal was successful
   */
  boolean removeWatchedEntityByUser(Watchable watchable, User user) throws IOException;

  /**
   * Unregister this watched entity
   * 
   * @param watchable
   *          of type Watchable
   * @return true if removal was successful
   * @throws IOException
   */
  boolean removeWatchedEntity(Watchable watchable) throws IOException;

  /**
   * Register a watched entity / user coupling
   * 
   * @param watchable
   *          of type Watchable
   * @param user
   *          of type User
   * @throws IOException
   */
  void saveWatchedEntityUser(Watchable watchable, User user) throws IOException;
}