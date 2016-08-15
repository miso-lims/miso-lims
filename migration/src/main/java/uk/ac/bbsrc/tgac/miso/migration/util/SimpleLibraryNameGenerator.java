package uk.ac.bbsrc.tgac.miso.migration.util;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NameGenerator;

/**
 * Generates values for the Library 'name' field in format "LIB" + libraryId. e.g. "LIB123"
 */
public class SimpleLibraryNameGenerator implements NameGenerator<Library> {

  private static final String NAME_PREFIX = "LIB";
  
  @Override
  public String getGeneratorName() {
    return getClass().getSimpleName();
  }

  @Override
  public String generateName(Library t) {
    return NAME_PREFIX + t.getId();
  }

  @Override
  public Class<Library> nameGeneratorFor() {
    return Library.class;
  }

}
