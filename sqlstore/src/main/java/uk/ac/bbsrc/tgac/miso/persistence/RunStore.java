package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

/**
 * Defines a DAO interface for storing Runs
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public interface RunStore extends SaveDao<Run>, PaginatedDataSource<Run> {

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
   * Retrieve a Run from an underlying data store given a Run alias
   *
   * @param alias of type String
   * @return Run
   * @throws IOException
   */
  Run getByAlias(String alias) throws IOException;

  /**
   * List all Runs using a Pool given a Pool ID
   *
   * @param poolId of type long
   * @return List<Run>
   * @throws IOException
   */
  List<Run> listByPoolId(long poolId) throws IOException;

  List<Run> listByLibraryAliquotId(long libraryAliquotId) throws IOException;

  List<Run> listByLibraryIdList(Collection<Long> libraryIds) throws IOException;

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
   * @throws IOException
   */
  List<Run> listByProjectId(long projectId) throws IOException;

  /**
   * List all Runs by their health given a HealthType
   *
   * @param health status to search for
   * @return all runs with matching status
   * @throws IOException
   */
  List<Run> listByStatus(String health) throws IOException;

  List<Run> listByIdList(Collection<Long> ids) throws IOException;

}
