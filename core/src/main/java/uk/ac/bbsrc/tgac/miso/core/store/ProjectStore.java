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

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;

import java.io.IOException;
import java.util.Collection;

/**
 * Defines a DAO interface for storing Projects
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface ProjectStore extends Store<Project>, Cascadable, Remover<Project> {
  /**
   * Retrieve a Project from an underlying data store given a Project ID
   * <p/>
   * This method intends to retrieve objects in an 'ignorant' fashion, i.e.  will not populate
   * parent or child objects that could lead to a circular dependency
   *
   * @param projectId of type long
   * @return Project
   * @throws IOException when
   */
  Project lazyGet(long projectId) throws IOException;

  /**
   * Get a parent Project related to a Study given a Study ID
   *
   * @param studyId of type long
   * @return Project
   * @throws IOException when
   */
  Project getByStudyId(long studyId) throws IOException;

  /**
   * List all Projects that match a search criteria
   *
   * @param query of type String
   * @return Collection<Project>
   * @throws IOException when
   */
  Collection<Project> listBySearch(String query) throws IOException;

  /**
   * Get a ProjectOverview given a ProjectOverview ID
   *
   * @param overviewId of type long
   * @return ProjectOverview
   * @throws IOException when
   */
  ProjectOverview getProjectOverviewById(long overviewId) throws IOException;

   /**
   * Get  ProjectOverviews given a Project ID
   *
   * @param projectId of type long
   * @return Collection<ProjectOverview>
   * @throws IOException when
   */
  Collection<ProjectOverview> listOverviewsByProjectId(long projectId) throws IOException;

  /**
   * Save a ProjectOverview
   *
   * @param overview of type ProjectOverview
   * @return long
   * @throws IOException when
   */
  long saveOverview(ProjectOverview overview) throws IOException;
}