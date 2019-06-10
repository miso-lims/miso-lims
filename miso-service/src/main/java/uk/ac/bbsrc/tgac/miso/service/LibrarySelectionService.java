package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface LibrarySelectionService extends DeleterService<LibrarySelectionType>, SaveService<LibrarySelectionType> {

  public List<LibrarySelectionType> list() throws IOException;

}
