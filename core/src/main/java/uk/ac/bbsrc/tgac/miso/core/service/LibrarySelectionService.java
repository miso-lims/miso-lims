package uk.ac.bbsrc.tgac.miso.core.service;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;

public interface LibrarySelectionService extends DeleterService<LibrarySelectionType>,
    BulkSaveService<LibrarySelectionType>, ListService<LibrarySelectionType> {

}
