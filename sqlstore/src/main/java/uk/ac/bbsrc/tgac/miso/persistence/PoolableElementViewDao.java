package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolableElementViewDao extends PaginatedDataSource<PoolableElementView> {

  public PoolableElementView get(Long dilutionId);

}
