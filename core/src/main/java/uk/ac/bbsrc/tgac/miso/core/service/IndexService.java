package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface IndexService extends DeleterService<Index>, BulkSaveService<Index>, PaginatedDataSource<Index> {

}
