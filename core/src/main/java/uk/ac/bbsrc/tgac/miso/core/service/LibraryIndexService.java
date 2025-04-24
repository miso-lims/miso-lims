package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryIndexService
    extends DeleterService<LibraryIndex>, BulkSaveService<LibraryIndex>, PaginatedDataSource<LibraryIndex> {
}
