package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolableElementViewDao extends PaginatedDataSource<PoolableElementView> {

  public PoolableElementView get(Long aliquotId) throws IOException;

  public PoolableElementView getByBarcode(String barcode) throws IOException;

  public PoolableElementView getByPreMigrationId(Long preMigrationId) throws IOException;

  public List<PoolableElementView> list(List<Long> aliquotIds) throws IOException;
}
