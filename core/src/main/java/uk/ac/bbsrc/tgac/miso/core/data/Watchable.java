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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.eaglegenomics.simlims.core.User;

/**
 * A Watchable object can be assigned watchers that will receive alerts upon the occurence of defined events.
 * 
 * @author Rob Davey
 * @date 05/12/11
 * @since 0.1.3
 */
public interface Watchable {
  /**
   * Returns the Set of {@link User}s watching this Watchable object
   * 
   * @return the Set of watchers for this object
   */
  @JsonIgnore
  Set<User> getWatchers();

  /**
   * Sets the Set of {@link User}s watching this Watchable object
   * 
   * @param watchers
   */
  void setWatchers(Set<User> watchers);

  /**
   * Adds a {@link User} to this Watchable object's watcher list
   * 
   * @param user
   */
  void addWatcher(User user);

  /**
   * Removes a {@link User} from this Watchable object's watcher list
   * 
   * @param user
   */
  void removeWatcher(User user);

  /**
   * Returns the unique Watchable identifier for this Watchable object. This is usually a property from the object itself, such as a unique
   * name or ID.
   * 
   * @return a String representing the watchable identifier for this object
   */
  @JsonIgnore
  String getWatchableIdentifier();
}
