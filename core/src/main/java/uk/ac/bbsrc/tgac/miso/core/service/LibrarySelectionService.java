package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;

public interface LibrarySelectionService extends DeleterService<LibrarySelectionType>, SaveService<LibrarySelectionType> {

  public List<LibrarySelectionType> list() throws IOException;

}
