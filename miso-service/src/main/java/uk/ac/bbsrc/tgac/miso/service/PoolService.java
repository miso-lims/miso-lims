package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolService extends PaginatedDataSource<Pool>, BarcodableService<Pool>, DeleterService<Pool>, NoteService<Pool> {
  @Override
  default EntityType getEntityType() {
    return EntityType.POOL;
  }

  public List<Pool> listBySearch(String query) throws IOException;

  public List<Pool> listWithLimit(int limit) throws IOException;

  public List<Pool> list() throws IOException;

  public List<Pool> listByIdList(List<Long> poolIds) throws IOException;

  public List<Pool> listByPlatform(PlatformType platformType) throws IOException;

  public List<Pool> listByPlatformAndSearch(PlatformType platformType, String query) throws IOException;

  public List<Pool> listByProjectId(long projectId) throws IOException;

  public List<Pool> listByLibraryId(long libraryId) throws IOException;

  public List<Pool> listByDilutionId(long dilutionId) throws IOException;

  public long save(Pool pool) throws IOException;

  public Pool getByBarcode(String barcode) throws IOException;

  public Map<String, Integer> getPoolColumnSizes() throws IOException;

}
