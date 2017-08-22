package uk.ac.bbsrc.tgac.miso.service.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.GetLaneContents;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface RunService extends PaginatedDataSource<Run> {

  Collection<Run> list() throws IOException;

  Collection<Run> listWithLimit(long limit) throws IOException;

  int count() throws IOException;

  Map<String, Integer> getRunColumnSizes() throws IOException;

  Collection<Run> listBySearch(String query) throws IOException;

  Collection<Run> listByProjectId(long projectId) throws IOException;

  Collection<Run> listByPoolId(long poolId) throws IOException;

  Collection<Run> listByContainerId(long containerId) throws IOException;

  Collection<Run> listBySequencerId(long sequencerId) throws IOException;

  /**
   * Throws AuthorizationException if user is not authorized to read the retrieved run.
   * 
   * @param runId
   * @return
   * @throws IOException
   */
  Run get(long runId) throws IOException, AuthorizationException;

  /**
   * Throws AuthorizationException if user is not authorized to read the retrieved run.
   * 
   * @param alias
   * @return
   * @throws IOException
   */
  Run getRunByAlias(String alias) throws IOException, AuthorizationException;

  /**
   * Throws AuthorizationException if user is not authorized to read the retrieved run.
   * 
   * @param alias
   * @return
   * @throws IOException
   */
  Run getLatestRunBySequencerPartitionContainerId(long containerId) throws IOException, AuthorizationException;

  void delete(Long runId) throws IOException;

  void addRunWatcher(Run run, User watcher) throws IOException;

  void removeRunWatcher(Run run, User watcher) throws IOException;

  void addNote(Run run, Note note) throws IOException;

  void deleteNote(Run run, Long noteId) throws IOException;

  void addQc(Run run, RunQC qc) throws IOException;

  void bulkAddQcs(Run run) throws IOException;

  void deleteQc(Run run, Long qcId) throws IOException;

  Collection<QcType> listRunQcTypes() throws IOException;

  Long create(Run run) throws IOException;

  void update(Run run) throws IOException;

  void saveRuns(Collection<Run> runs) throws IOException;

  QcType getRunQcType(long qcTypeId) throws IOException;

  QcType getRunQcTypeByName(String qcTypeName) throws IOException;

  /**
   * Save a scanned run to the database or update the run if it exists.
   * 
   * @param run the update from notification server
   * @return true if the run is new, false if it already existed
   * @throws MisoNamingException
   */
  boolean processNotification(Run run, int laneCount, String containerSerialNumber, String sequencerName,
      Predicate<SequencingParameters> filterParameters, GetLaneContents laneContents) throws IOException, MisoNamingException;

}
