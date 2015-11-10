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

import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.Assignable;
import uk.ac.bbsrc.tgac.miso.core.data.Watchable;

import java.io.IOException;
import java.util.Collection;

/**
 * Interface to persist relationships between {@link Assignable} objects to {@link User}s.
 *
 * @author Rob Davey
 * @date 09/05/14
 * @since 0.2.1
 */
public interface AssigneeStore {
  /**
   * Get the User that are assigned to an entity given an entity name (defined by that entity's {@link uk.ac.bbsrc.tgac.miso.core.data.Assignable}
   * getAssignableIdentifier method)
   *
   * @param entityName of type String
   * @return User
   * @throws java.io.IOException
   */
  User getAssigneeByEntityName(String entityName) throws IOException;

  /**
   * Retrieve entities by their assigned user
   *
   * @param assignee of type User
   * @return Collection<Assignable> entities
   * @throws IOException
   */
  Collection<Assignable> getEntitiesByAssignee(User assignee) throws IOException;

  /**
   * Unregister an assigned entity / user coupling
   *
   * @param assignable of type Assignable
   * @param user of type User
   * @throws java.io.IOException
   * @return true if removal was successful
   */
  boolean removeAssignedEntityByUser(Assignable assignable, User user) throws IOException;

  /**
   * Unregister this assigned entity
   *
   * @param assignable of type Assignable
   * @return true if removal was successful
   * @throws java.io.IOException
   */
  boolean removeAssignedEntity(Assignable assignable) throws IOException;

  /**
   * Register an assigned entity / user coupling
   *
   * @param assignable of type Assignable
   * @param user of type User
   * @throws java.io.IOException
   */
  void saveAssignedEntityUser(Assignable assignable, User user) throws IOException;
}