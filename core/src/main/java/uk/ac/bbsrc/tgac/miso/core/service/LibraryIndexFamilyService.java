package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;

public interface LibraryIndexFamilyService
    extends DeleterService<LibraryIndexFamily>, ListService<LibraryIndexFamily>, SaveService<LibraryIndexFamily> {

  public LibraryIndexFamily getByName(String name) throws IOException;

}
