package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;

public interface LibrarySelectionDao extends BulkSaveDao<LibrarySelectionType> {

  LibrarySelectionType getByName(String name) throws IOException;

  long getUsageByLibraries(LibrarySelectionType type) throws IOException;

  long getUsageByLibraryDesigns(LibrarySelectionType type) throws IOException;

}
