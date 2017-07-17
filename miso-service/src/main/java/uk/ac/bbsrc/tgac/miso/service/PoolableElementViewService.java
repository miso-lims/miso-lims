package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolableElementViewService extends PaginatedDataSource<PoolableElementView> {

  public PoolableElementView get(Long dilutionId) throws IOException;

  public PoolableElementView getByBarcode(String barcode) throws IOException;

  public PoolableElementView getByPreMigrationId(Long preMigrationId) throws IOException;

  public List<PoolableElementView> list(List<Long> dilutionIds) throws IOException;
}
