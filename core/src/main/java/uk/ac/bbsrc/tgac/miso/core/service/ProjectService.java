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

package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Project;

public interface ProjectService extends DeleterService<Project>, ListService<Project>, SaveService<Project> {


  // GETS
  public Project getProjectByAlias(String projectAlias) throws IOException;

  public Project getProjectByShortName(String projectShortName) throws IOException;

  // LISTS
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException;

  public Collection<Project> listAllProjectsBySearch(String query) throws IOException;

  /**
   * Obtain a list of all the projects the user has access to, sorted alphabetically by shortname.
   * Used for displaying lists in Detailed Sample mode.
   * 
   * @return Collection of Projects sorted alphabetically by shortname.
   * @throws IOException upon failure to access Projects
   */
  public Collection<Project> listAllProjectsByShortname() throws IOException;

}
