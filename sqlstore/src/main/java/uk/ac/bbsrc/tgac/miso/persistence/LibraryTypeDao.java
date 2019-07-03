package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface LibraryTypeDao extends SaveDao<LibraryType> {

  public LibraryType getByPlatformAndDescription(PlatformType platform, String description) throws IOException;
  
  public List<LibraryType> listByPlatform(PlatformType platform) throws IOException;

  public long getUsageByLibraries(LibraryType type) throws IOException;

  public long getUsageByLibraryTemplates(LibraryType type) throws IOException;

}
