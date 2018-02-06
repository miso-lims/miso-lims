package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ArrayRunService extends PaginatedDataSource<ArrayRun> {

  public ArrayRun get(long arrayRunId) throws IOException;

  public long save(ArrayRun arrayRun) throws IOException;

  public Map<String, Integer> getColumnSizes() throws IOException;

  public List<ArrayRun> listByArrayId(long arrayId) throws IOException;

  public List<ArrayRun> listBySampleId(long sampleId) throws IOException;

}
