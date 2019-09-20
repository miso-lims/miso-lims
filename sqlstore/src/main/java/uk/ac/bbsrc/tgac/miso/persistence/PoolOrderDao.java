package uk.ac.bbsrc.tgac.miso.persistence;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

import java.util.List;

public interface PoolOrderDao extends PaginatedDataSource<PoolOrder>, SaveDao<PoolOrder> {

    List<PoolOrder> getAllByPoolId(long poolId);
}
