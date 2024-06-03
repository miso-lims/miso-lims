package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface ArrayRunStore extends PaginatedDataSource<ArrayRun>, SaveDao<ArrayRun> {

  public ArrayRun getByAlias(String alias) throws IOException;

  public List<ArrayRun> listByArrayId(long arrayId) throws IOException;

  public List<ArrayRun> listBySampleId(long sampleId) throws IOException;

}
