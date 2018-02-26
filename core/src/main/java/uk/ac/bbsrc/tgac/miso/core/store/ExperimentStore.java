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

package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;

/**
 * Defines a DAO interface for storing Experiments
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface ExperimentStore extends Store<Experiment> {
  /**
   * List all Experiments that match a search criteria
   * 
   * @param query
   *          of type String
   * @return Collection<Experiment>
   * @throws IOException
   *           when
   */
  Collection<Experiment> listBySearch(String query) throws IOException;

  /**
   * List all Experiments that are part of a Study given a Study ID
   * 
   * @param studyId
   *          of type long
   * @return Collection<Experiment>
   * @throws IOException
   *           when
   */
  Collection<Experiment> listByStudyId(long studyId) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Experiment>
   * @throws IOException
   *           when the objects cannot be retrieved
   */
  Collection<Experiment> listAllWithLimit(long limit) throws IOException;
  
  /**
   * @return a map containing all column names and max lengths from the Experiments table
   * @throws IOException
   */
  public Map<String, Integer> getExperimentColumnSizes() throws IOException;

  Collection<Experiment> listByLibrary(long id) throws IOException;

  Collection<Experiment> listByRun(long runId) throws IOException;
}
