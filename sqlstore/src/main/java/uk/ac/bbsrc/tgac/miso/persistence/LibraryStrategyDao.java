package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

public interface LibraryStrategyDao extends SaveDao<LibraryStrategyType> {

  LibraryStrategyType getByName(String name) throws IOException;

  long getUsageByLibraries(LibraryStrategyType type) throws IOException;

  long getUsageByLibraryDesigns(LibraryStrategyType type) throws IOException;

  List<LibraryStrategyType> listByIdList(List<Long> idList) throws IOException;

}
