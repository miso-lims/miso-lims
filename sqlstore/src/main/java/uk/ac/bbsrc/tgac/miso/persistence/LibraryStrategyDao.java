package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;

public interface LibraryStrategyDao {

  public LibraryStrategyType get(long id) throws IOException;

  public LibraryStrategyType getByName(String name) throws IOException;

  public List<LibraryStrategyType> list() throws IOException;

  public long create(LibraryStrategyType type) throws IOException;

  public long update(LibraryStrategyType type) throws IOException;

  public long getUsage(LibraryStrategyType type) throws IOException;

}
