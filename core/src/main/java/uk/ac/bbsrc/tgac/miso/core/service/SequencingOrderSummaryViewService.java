package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.SequencingOrderSummaryView;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SequencingOrderSummaryViewService extends PaginatedDataSource<SequencingOrderSummaryView> {

  List<SequencingOrderSummaryView> listByPoolId(Long poolId) throws IOException;

}
