package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;

public interface WorksetStore extends SaveDao<Workset> {

  public Workset getByAlias(String alias) throws IOException;

  public List<Workset> listBySample(long sampleId) throws IOException;

  public List<Workset> listByLibrary(long libraryId) throws IOException;

  public List<Workset> listByLibraryAliquot(long aliquotId) throws IOException;

  public List<Workset> listByPool(long poolId) throws IOException;

  public Map<Long, Date> getSampleAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getLibraryAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getLibraryAliquotAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getPoolAddedTimes(long worksetId) throws IOException;

}
