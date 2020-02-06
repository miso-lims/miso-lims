package uk.ac.bbsrc.tgac.miso.core.service.naming.generation;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;

public class DefaultLibraryAliquotAliasGenerator implements NameGenerator<LibraryAliquot> {

  @Override
  public String generate(LibraryAliquot object) throws MisoNamingException, IOException {
    return object.getLibrary().getAlias();
  }

}
