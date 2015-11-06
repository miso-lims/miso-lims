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

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeAware;

/**
 * Defines a DAO interface for storing Runs
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface RunStore extends Store<Run>, Cascadable, Remover<Run>, NamingSchemeAware<Run> {
  /**
   * Retrieve a Run from an underlying data store given a Run ID
   * <p/>
   * This method intends to retrieve objects in an 'ignorant' fashion, i.e.  will not populate
   * parent or child objects that could lead to a circular dependency
   *
   * @param runId of type long
   * @return Run
   * @throws IOException when
   */
  //Run lazyGet(long runId) throws IOException;

  /**
   * Gets the latest Run, by start date, that is associated with the given container
   *
   * @param containerId long
   * @return Run
   * @throws IOException
   */
  Run getLatestStartDateRunBySequencerPartitionContainerId(long containerId) throws IOException;

  /**
   * Gets the latest Run, by run ID, that is associated with the given container
   *
   * @param containerId long
   * @return Run
   * @throws IOException
   */
  Run getLatestRunIdRunBySequencerPartitionContainerId(long containerId) throws IOException;

  /**
   * List all Runs with name, alias, or description containing the query string
   *
   * @param query String to search for
   * @return Collection<Run>
   * @throws IOException
   * @throws NullPOinterException if query is null
   */
  Collection<Run> listBySearch(String query) throws IOException;

  /**
   * Retrieve a Run from an underlying data store given a Run alias
   *
   * @param alias of type String
   * @return Run
   * @throws IOException when
   */
  Run getByAlias(String alias) throws IOException;

  /**
   * List all Runs related to an Experiment given an Experiment ID
   *
   * @param experimentId of type long
   * @return List<Run>
   * @throws IOException when
   */
  @Deprecated
  List<Run> listByExperimentId(long experimentId) throws IOException;

  /**
   * List all Runs using a Pool given a Pool ID
   *
   * @param poolId of type long
   * @return List<Run>
   * @throws IOException when
   */  
  List<Run> listByPoolId(long poolId) throws IOException;

  /**
   * List all Runs using a Container given a Container ID
   *
   * @param containerId of type long
   * @return List<Run>
   * @throws IOException when
   */
  List<Run> listBySequencerPartitionContainerId(long containerId) throws IOException;

  /**
   * List all Runs related to a Project given a Project ID
   *
   * @param projectId of type long
   * @return List<Run>
   * @throws IOException when
   */
  List<Run> listByProjectId(long projectId) throws IOException;

  /**
   * List all Runs carried out on a Platform given a Platform ID 
   *
   * @param platformId of type long
   * @return List<Run>
   * @throws IOException when
   */
  List<Run> listByPlatformId(long platformId) throws IOException;
  
  /**
   * List all Runs by their health given a HealthType 
   *
   * @param health status to search for
   * @return all runs with matching status
   * @throws IOException
   */
  List<Run> listByStatus(String health) throws IOException;

  /**
   * List all persisted objects
   *
   * @param the maximum number of objects to return. If this is negative, no limit will be set
   * @return Collection<Run>
   * @throws IOException when the objects cannot be retrieved
   */
  Collection<Run> listAllWithLimit(long limit) throws IOException;

  int[] saveAll(Collection<Run> runs) throws IOException;
}
