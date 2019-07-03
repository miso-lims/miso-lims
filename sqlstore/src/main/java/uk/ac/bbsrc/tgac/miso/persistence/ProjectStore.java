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
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Project;

/**
 * Defines a DAO interface for storing Projects
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface ProjectStore extends Store<Project> {
  /**
   * Get a Project given an alias
   * 
   * @param alias
   *          of type String
   * @return Project
   * @throws IOException
   *           when
   */
  Project getByAlias(String alias) throws IOException;

  Project getByShortName(String shortName) throws IOException;

  /**
   * Get a parent Project related to a Study given a Study ID
   * 
   * @param studyId
   *          of type long
   * @return Project
   * @throws IOException
   *           when
   */
  Project getByStudyId(long studyId) throws IOException;

  /**
   * List all Projects that match a search criteria
   * 
   * @param query
   *          of type String
   * @return Collection<Project>
   * @throws IOException
   *           when
   */
  Collection<Project> listBySearch(String query) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Project>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Project> listAllWithLimit(long limit) throws IOException;

}