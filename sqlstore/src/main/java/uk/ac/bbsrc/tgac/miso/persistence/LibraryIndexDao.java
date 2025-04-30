package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface LibraryIndexDao extends PaginatedDataSource<LibraryIndex>, BulkSaveDao<LibraryIndex> {

  public LibraryIndex getByFamilyPositionAndName(LibraryIndexFamily family, int position, String name)
      throws IOException;

  public long getUsage(LibraryIndex index) throws IOException;

}
