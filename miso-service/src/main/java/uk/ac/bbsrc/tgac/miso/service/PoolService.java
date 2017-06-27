package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolService extends PaginatedDataSource<Pool> {

  public Collection<Pool> listBySearch(String query) throws IOException;

  public Collection<Pool> listWithLimit(int limit) throws IOException;

  public Collection<Pool> list() throws IOException;

  public Collection<Pool> listByIdList(List<Long> poolIds) throws IOException;

  public Collection<Pool> listByPlatform(PlatformType platformType) throws IOException;

  public Collection<Pool> listByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  public Collection<Pool> listReadyPoolsByPlatform(PlatformType platformType) throws IOException;

  public Collection<Pool> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  public Collection<Pool> listByProjectId(long projectId) throws IOException;

  public Collection<Pool> listByLibraryId(long libraryId) throws IOException;

  public void delete(Pool pool) throws IOException;

  public void deleteNote(Pool pool, Long noteId) throws IOException;

  public long save(Pool pool) throws IOException;

  public long savePoolQC(PoolQC poolQC) throws IOException;

  public void saveNote(Pool pool, Note note) throws IOException;

  public Pool get(long poolId) throws IOException;

  public PoolQC getPoolQC(long poolQcId) throws IOException;

  public Pool getByBarcode(String barcode) throws IOException;

  public Map<String, Integer> getPoolColumnSizes() throws IOException;

  public void addPoolWatcher(Pool pool, User watcher) throws IOException;

  public void removePoolWatcher(Pool pool, User watcher) throws IOException;

  QcType getPoolQcType(long qcTypeId) throws IOException;

  Collection<QcType> listPoolQcTypes() throws IOException;

}
