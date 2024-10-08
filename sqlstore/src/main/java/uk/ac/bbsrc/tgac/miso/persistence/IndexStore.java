package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface IndexStore extends PaginatedDataSource<Index>, BulkSaveDao<Index> {

  public Index getByFamilyPositionAndName(IndexFamily family, int position, String name) throws IOException;

  public long getUsage(Index index) throws IOException;

}
