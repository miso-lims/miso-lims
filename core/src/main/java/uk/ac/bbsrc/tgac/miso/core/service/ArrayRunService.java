package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ArrayRunService
    extends DeleterService<ArrayRun>, PaginatedDataSource<ArrayRun>, SaveService<ArrayRun> {

  public List<ArrayRun> listByArrayId(long arrayId) throws IOException;

  public List<ArrayRun> listBySampleId(long sampleId) throws IOException;

  public List<ArrayRun> listBySampleIds(List<Long> sampleIds) throws IOException;

}
