package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.GetLaneContents;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface RunService extends ProviderService<Run>, PaginatedDataSource<Run>, NoteService<Run> {

  Collection<Run> list() throws IOException;

  int count() throws IOException;

  Map<String, Integer> getRunColumnSizes() throws IOException;

  Collection<Run> listBySearch(String query) throws IOException;

  Collection<Run> listByProjectId(long projectId) throws IOException;

  Collection<Run> listByPoolId(long poolId) throws IOException;

  Collection<Run> listByContainerId(long containerId) throws IOException;

  Collection<Run> listByInstrumentId(long instrumentId) throws IOException;

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

  void addRunWatcher(Run run, User watcher) throws IOException;

  void removeRunWatcher(Run run, User watcher) throws IOException;

  Long create(Run run) throws IOException;

  void update(Run run) throws IOException;

  void saveRuns(Collection<Run> runs) throws IOException;

  /**
   * Save a scanned run to the database or update the run if it exists.
   * 
   * @param run the update from notification server
   * @return true if the run is new, false if it already existed
   * @throws MisoNamingException
   */
  boolean processNotification(Run run, int laneCount, String containerModel, String containerSerialNumber, String instrumentName,
      Predicate<SequencingParameters> filterParameters, GetLaneContents laneContents, String positionName)
      throws IOException, MisoNamingException;

}
