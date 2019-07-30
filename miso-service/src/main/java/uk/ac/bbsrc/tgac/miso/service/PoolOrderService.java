package uk.ac.bbsrc.tgac.miso.service;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.service.DeleterService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolOrderService extends DeleterService<PoolOrder>, SaveService<PoolOrder>, PaginatedDataSource<PoolOrder> {

}
