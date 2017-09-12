package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface PoolOrderCompletionService extends PaginatedDataSource<PoolOrderCompletion> {

  List<PoolOrderCompletion> getByPoolId(Long poolId) throws IOException;
}
