package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;

public interface LibrarySelectionDao {

  public LibrarySelectionType get(long id) throws IOException;

  public LibrarySelectionType getByName(String name) throws IOException;

  public List<LibrarySelectionType> list() throws IOException;

  public long create(LibrarySelectionType type) throws IOException;

  public long update(LibrarySelectionType type) throws IOException;

  public long getUsage(LibrarySelectionType type) throws IOException;

}
