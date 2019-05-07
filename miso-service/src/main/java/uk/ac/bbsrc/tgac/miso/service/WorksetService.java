package uk.ac.bbsrc.tgac.miso.service;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Workset;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface WorksetService extends PaginatedDataSource<Workset>, DeleterService<Workset>, SaveService<Workset> {

  public List<Workset> listBySearch(String query);

  public List<Workset> listBySample(long sampleId);

  public List<Workset> listByLibrary(long libraryId);

  public List<Workset> listByDilution(long dilutionId);

}
