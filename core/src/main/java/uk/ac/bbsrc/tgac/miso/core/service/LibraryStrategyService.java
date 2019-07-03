package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

public interface LibraryStrategyService extends DeleterService<LibraryStrategyType>, SaveService<LibraryStrategyType> {

  public List<LibraryStrategyType> list() throws IOException;

}
