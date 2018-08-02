package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface WorksetStore extends PaginatedDataSource<Workset> {

  public Workset get(long id);

  public Workset getByAlias(String alias);

  public List<Workset> listBySearch(String query);

  public List<Workset> listBySample(long sampleId);

  public List<Workset> listByLibrary(long libraryId);

  public List<Workset> listByDilution(long dilutionId);

  public long save(Workset workset);

  public Map<String, Integer> getColumnSizes() throws IOException;

}
