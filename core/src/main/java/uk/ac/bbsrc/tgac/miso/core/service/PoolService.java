package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolService
    extends PaginatedDataSource<Pool>, BarcodableService<Pool>, DeleterService<Pool>, NoteService<Pool>, BulkSaveService<Pool> {

  @Override
  default EntityType getEntityType() {
    return EntityType.POOL;
  }

  @Override
  public List<Pool> listByIdList(List<Long> poolIds) throws IOException;

  public List<Pool> listByLibraryId(long libraryId) throws IOException;

  public List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException;

  public Pool getByBarcode(String barcode) throws IOException;

}
