package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolService
    extends PaginatedDataSource<Pool>, BarcodableService<Pool>, DeleterService<Pool>, NoteService<Pool>,
    BulkSaveService<Pool> {

  @Override
  default EntityType getEntityType() {
    return EntityType.POOL;
  }

  List<Pool> listByLibraryId(long libraryId) throws IOException;

  List<Pool> listByLibraryAliquotId(long aliquotId) throws IOException;

  Pool getByBarcode(String barcode) throws IOException;

  Pool getByAlias(String alias) throws IOException;

  void saveBarcode(long poolId, String barcode) throws IOException;

}
