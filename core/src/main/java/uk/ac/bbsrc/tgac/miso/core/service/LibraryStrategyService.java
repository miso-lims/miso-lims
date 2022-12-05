package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

public interface LibraryStrategyService extends DeleterService<LibraryStrategyType>,
    BulkSaveService<LibraryStrategyType>, ListService<LibraryStrategyType> {

}
