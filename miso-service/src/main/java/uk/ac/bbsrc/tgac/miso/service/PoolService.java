package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolService extends PaginatedDataSource<Pool> {

  public Collection<Pool> listAllPoolsBySearch(String query) throws IOException;

  public Collection<Pool> listAllPoolsWithLimit(int limit) throws IOException;

  public Collection<Pool> listAllPools() throws IOException;

  public Collection<Pool> listAllPoolsByPlatform(PlatformType platformType) throws IOException;

  public Collection<Pool> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  public Collection<Pool> listReadyPoolsByPlatform(PlatformType platformType) throws IOException;

  public Collection<Pool> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  public Collection<Pool> listPoolsByProjectId(long projectId) throws IOException;

  public Collection<Pool> listPoolsByLibraryId(long libraryId) throws IOException;

  public void deletePool(Pool pool) throws IOException;

  public void deletePoolNote(Pool pool, Long noteId) throws IOException;

  public long savePool(Pool pool) throws IOException;

  public long savePoolQC(PoolQC poolQC) throws IOException;

  public void savePoolNote(Pool pool, Note note) throws IOException;

  public Pool getPoolById(long poolId) throws IOException;

  public PoolQC getPoolQCById(long poolQcId) throws IOException;

  public Pool getPoolByBarcode(String barcode) throws IOException;

  public Map<String, Integer> getPoolColumnSizes() throws IOException;

  public void addPoolWatcher(Pool pool, User watcher) throws IOException;

  public void removePoolWatcher(Pool pool, User watcher) throws IOException;

}
