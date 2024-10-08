package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

public interface LibraryStrategyDao extends BulkSaveDao<LibraryStrategyType> {

  LibraryStrategyType getByName(String name) throws IOException;

  long getUsageByLibraries(LibraryStrategyType type) throws IOException;

  long getUsageByLibraryDesigns(LibraryStrategyType type) throws IOException;

}
