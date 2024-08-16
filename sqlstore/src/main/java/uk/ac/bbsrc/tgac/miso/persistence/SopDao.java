package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SopDao extends PaginatedDataSource<Sop>, BulkSaveDao<Sop> {

  public Sop get(SopCategory category, String alias, String version) throws IOException;

  public List<Sop> listByCategory(SopCategory category) throws IOException;

  public long getUsageBySamples(Sop sop) throws IOException;

  public long getUsageByLibraries(Sop sop) throws IOException;

  public long getUsageByRuns(Sop sop) throws IOException;

}
