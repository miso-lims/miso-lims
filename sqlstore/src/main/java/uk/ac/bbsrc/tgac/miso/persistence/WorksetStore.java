package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;

public interface WorksetStore {

  public Workset get(long id) throws IOException;

  public Workset getByAlias(String alias) throws IOException;

  public List<Workset> listBySample(long sampleId) throws IOException;

  public List<Workset> listByLibrary(long libraryId) throws IOException;

  public List<Workset> listByLibraryAliquot(long aliquotId) throws IOException;

  public long save(Workset workset) throws IOException;

  public Map<Long, Date> getSampleAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getLibraryAddedTimes(long worksetId) throws IOException;

  public Map<Long, Date> getLibraryAliquotAddedTimes(long worksetId) throws IOException;

}
