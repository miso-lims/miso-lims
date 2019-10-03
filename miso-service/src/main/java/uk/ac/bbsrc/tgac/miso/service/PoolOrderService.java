package uk.ac.bbsrc.tgac.miso.service;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.service.DeleterService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

import java.io.IOException;
import java.util.List;

public interface PoolOrderService extends DeleterService<PoolOrder>, SaveService<PoolOrder>, PaginatedDataSource<PoolOrder> {

    List<PoolOrder> getAllByPoolId(long poolId) throws IOException;
}
