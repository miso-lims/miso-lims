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

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Studies
 * 
 * @author Rob Davey
 * @since version
 */
public interface StudyStore extends Store<Study>, Cascadable, Remover<Study>, NamingSchemeAware<Study> {
  /**
   * List all Studies that match a search criteria
   * 
   * @param query
   *          of type String
   * @return Collection<Study>
   * @throws IOException
   *           when
   */
  Collection<Study> listBySearch(String query) throws IOException;

  /**
   * Get a Study related to an Experiment given an Experiment ID
   * 
   * @param experimentId
   *          of type long
   * @return Study
   * @throws IOException
   *           when
   */
  Study getByExperimentId(long experimentId) throws IOException;

  /**
   * List all Studies that are carried out as part of a parent Project given a Project ID
   * 
   * @param projectId
   *          of type long
   * @return Collection<Study>
   * @throws IOException
   *           when
   */
  Collection<Study> listByProjectId(long projectId) throws IOException;

  /**
   * List all Studys that are part of a Submission given a Submission ID
   * 
   * @param submissionId
   *          of type long
   * @return Collection<Study>
   * @throws IOException
   *           when
   */
  Collection<Study> listBySubmissionId(long submissionId) throws IOException;

  /**
   * List all Studies that are carried out as part of a related Library given a Library ID
   * 
   * @param libraryId
   *          of type long
   * @return Collection<Study>
   * @throws IOException
   *           when
   */
  Collection<Study> listByLibraryId(long libraryId) throws IOException;

  /**
   * List all StudyTypes
   * 
   * @return Collection<String>
   * @throws IOException
   *           when
   */
  Collection<String> listAllStudyTypes() throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Study>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Study> listAllWithLimit(long limit) throws IOException;
}
