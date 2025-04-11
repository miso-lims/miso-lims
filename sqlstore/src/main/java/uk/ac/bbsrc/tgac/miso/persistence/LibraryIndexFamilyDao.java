package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;

public interface LibraryIndexFamilyDao extends SaveDao<LibraryIndexFamily> {

  public LibraryIndexFamily getByName(String name) throws IOException;

  public long getUsage(LibraryIndexFamily indexFamily) throws IOException;

}
