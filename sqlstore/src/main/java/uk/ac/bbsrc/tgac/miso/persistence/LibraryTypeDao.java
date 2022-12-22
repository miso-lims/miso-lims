package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface LibraryTypeDao extends BulkSaveDao<LibraryType> {

  LibraryType getByPlatformAndDescription(PlatformType platform, String description) throws IOException;
  
  List<LibraryType> listByPlatform(PlatformType platform) throws IOException;

  long getUsageByLibraries(LibraryType type) throws IOException;

  long getUsageByLibraryTemplates(LibraryType type) throws IOException;

}
