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

import com.eaglegenomics.simlims.core.User;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A Assignable object can be assigned Assignees that will be responsible for the object
 *
 * @author Rob Davey
 * @date 26/02/14
 * @since 0.2.1-SNAPSHOT
 */
public interface Assignable {
  /**
   * Returns the Set of {@link com.eaglegenomics.simlims.core.User}s assigned to this Assignable object
   * @return the Set of assignees for this object
   */
  @JsonIgnore
  User getAssignee();

  /**
   * Sets the {@link com.eaglegenomics.simlims.core.User} who is assigned to this Assignable object
   * @param assignee
   */
  void setAssignee(User assignee);

  /**
   * Returns the unique Assignable identifier for this Assignable object. This is usually a property from the object
   * itself, such as a unique name or ID.
   * @return a String representing the assignable identifier for this object
   */
  @JsonIgnore
  String getAssignableIdentifier();
}
