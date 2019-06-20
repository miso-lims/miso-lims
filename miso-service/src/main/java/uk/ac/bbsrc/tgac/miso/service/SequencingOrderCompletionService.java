package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SequencingOrderCompletion;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SequencingOrderCompletionService extends PaginatedDataSource<SequencingOrderCompletion> {

  List<SequencingOrderCompletion> listByPoolId(Long poolId) throws IOException;
}
