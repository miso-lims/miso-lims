package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ArrayRunStore extends Store<ArrayRun>, PaginatedDataSource<ArrayRun> {

  /**
   * @return a map containing all column names and max lengths from the Array table
   * @throws IOException
   */
  public Map<String, Integer> getArrayColumnSizes() throws IOException;

  public ArrayRun getByAlias(String alias) throws IOException;

  public List<ArrayRun> listByArrayId(long arrayId) throws IOException;

  public List<ArrayRun> listBySampleId(long sampleId) throws IOException;

}
