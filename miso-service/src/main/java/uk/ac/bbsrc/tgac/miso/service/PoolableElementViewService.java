package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolableElementViewService extends PaginatedDataSource<PoolableElementView> {

  public PoolableElementView get(Long dilutionId) throws IOException;

  public PoolableElementView getByBarcode(String barcode) throws IOException;

}
