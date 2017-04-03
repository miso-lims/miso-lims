package uk.ac.bbsrc.tgac.miso.service;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PoolPaginationFilter;

public interface PoolService extends PaginatedDataSource<Pool, PoolPaginationFilter> {

}
