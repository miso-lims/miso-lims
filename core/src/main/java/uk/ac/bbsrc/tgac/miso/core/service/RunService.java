package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RunService extends DeleterService<Run>, SaveService<Run>, PaginatedDataSource<Run>, NoteService<Run> {

  Collection<Run> listByProjectId(long projectId) throws IOException;

  Collection<Run> listByPoolId(long poolId) throws IOException;

  List<Run> listByLibraryAliquotId(long libraryAliquotId) throws IOException;

  List<Run> listByLibraryIdList(Collection<Long> libraryIds) throws IOException;

  Collection<Run> listByContainerId(long containerId) throws IOException;

  List<Run> listByIdList(Collection<Long> ids) throws IOException;

  /**
   * Throws AuthorizationException if user is not authorized to read the retrieved run.
   * 
   * @param alias
   * @return
   * @throws IOException
   */
  Run getRunByAlias(String alias) throws IOException;

  /**
   * Throws AuthorizationException if user is not authorized to read the retrieved run.
   * 
   * @param alias
   * @return
   * @throws IOException
   */
  Run getLatestRunBySequencerPartitionContainerId(long containerId) throws IOException;

  void saveRuns(Collection<Run> runs) throws IOException;

  /**
   * Save a scanned run to the database or update the run if it exists.
   * 
   * @param run the update from notification server
   * @return true if the run is new, false if it already existed
   * @throws MisoNamingException
   */
  boolean processNotification(Run run) throws IOException, MisoNamingException;

}
